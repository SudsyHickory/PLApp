# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository layout

This repo contains **two independent Spring Boot applications**, not a Maven multi-module reactor:

- `main-app/` — the API/gateway service (Java 21 runtime target via Spring Boot parent, `<java.version>23</java.version>`, Spring Boot **4.0.3**)
- `app-worker/` — the match-simulation worker service (same Spring Boot 4.0.3 parent)

The root `pom.xml` (`packaging=pom`) is a leftover aggregator and does **not** declare `<modules>` — it is not used to build either app. Each app has its own `mvnw`/`mvnw.cmd` and its own `spring-boot-starter-parent` parent, and must be built/tested from inside its own directory.

## Commands

Run from inside `main-app/` or `app-worker/` respectively (use `./mvnw` on Unix shells, `mvnw.cmd` on plain cmd.exe; from this PowerShell/Bash environment `./mvnw` works via the wrapper script):

```bash
./mvnw test                                    # run all tests
./mvnw test -Dtest=MatchesServiceTest           # run a single test class
./mvnw test -Dtest=MatchesServiceTest#verifyMapToEntity   # run a single test method
./mvnw spring-boot:run                          # run the app locally (needs Postgres/Kafka/Redis reachable)
./mvnw clean package                            # build the jar
```

`main-app` integration tests (`src/test/java/org/mainapp/integration`) use **Testcontainers** (`PostgreSQLContainer`) and require a working Docker daemon; they will fail/hang without one.

### Running the full stack

```bash
docker-compose up --build
```

This starts Postgres, a single-node KRaft Kafka broker, Redis, `main-app` (port 8080), and 3 replicas of `app-worker` (see `compose.yaml`, repo root). `main-app` needs a `FOOTBALL_API_KEY` env var (from a root `.env` file) to call the football-data.org API.

## Architecture

### Data flow between the two services

1. **`main-app`** owns Postgres (via JPA + Flyway) and is the source of truth for `Team`, `Match`, `Matchday` entities. On startup, `DataInitializer` (a `CommandLineRunner`) calls `TeamsService.initializeData()` / `MatchesService.initializeData()`, which populate the DB from the football-data.org REST API (via `@ImportHttpServices`-declared `TeamsClientService`/`MatchesClientService` in `config/footballData`) **only if the tables are empty**.
2. To start a simulated matchday, `main-app`'s `SimulationService.startSimulationForMatchday` publishes one `MatchDTOSim` message per match to the Kafka topic `simulation`, keyed by match ID (topic has 3 partitions — see `KafkaConfig`).
3. **`app-worker`** instances consume `simulation` (`MatchStarter` → `SimulationService.simulate`, `@Async`). Each worker simulates a match minute-by-minute (`Thread.sleep(1000)` per simulated minute, random goal events) and publishes a `MatchEvent` per minute to the `match-events` topic, keyed by match ID — Kafka's per-key ordering guarantees one worker instance handles a given match's events in order, which is what allows horizontal scaling across match instances.
4. **`main-app`** has *two* separate consumer groups on `match-events` (both declared as `@KafkaListener` methods in the same `SimulationService`, since a single properties key configures both group IDs — see `spring.kafka.consumer.group-id=events-group, database-group`):
   - `events-group` (`consumeEvents`): forwards events live over WebSocket/STOMP (`/simulation/match-update`, and `/topic/match-logs` for goals only) — no persistence.
   - `database-group` (`storeOrUpdateEvents`): write-behind persistence — every event updates Redis (match state hash + a live-bonus ZSet used to compute in-progress league standings), and only every 10th minute (or on `FINISHED`) does it write through to Postgres via `MatchesRepository`/`TeamsRepository`.
5. **Kafka Streams** (`KafkaStreamsConfig`, `main-app` only) separately aggregates `match-events` into a `match-history-store` (a `KeyValueStore` keyed by match ID, valued as the full event list) for match-replay — `SimulationService.startReplayForMatch` queries this store directly and re-broadcasts events over `/topic/match-ticks`.

### Redis key/data-structure conventions (main-app)

- `week:<matchdayId>` — Hash of `matchId -> JSON(MatchDTOSim)`, TTL 10 min. Read by `MatchesService.getMatchesListForMatchday` (cache-aside: populated from Postgres on miss) and written by `SimulationService.saveMatchToCache` on every live event.
- `table:official` — ZSet of `teamShortName -> points`, seeded from Postgres at the start of a matchday (`initializeOfficialTable`).
- `table:live-bonus` — ZSet of in-progress point deltas for the current matchday, added to by `saveTeamToBonusCache`.
- `table:live` — computed as `ZUNIONSTORE table:official + table:live-bonus`, then broadcast over `/simulation/table-update` as the live table on every goal-affecting event.

### DTO boundary: entity vs. wire format

`Match`/`Team`/`Matchday` are JPA entities (main-app only, one DB). `MatchDTOSim` is the Kafka/Redis/WebSocket wire format shared conceptually between `main-app` and `app-worker` — **note it is duplicated as separate classes** in `org.mainapp.data` and `org.appworker.data` (not a shared library) with the same field shape, since the two apps don't share a common module. When changing this DTO's shape, both copies must be updated in lockstep, and downstream JSON consumers (Redis-cached JSON, WebSocket clients) must stay compatible.

### Migrations

`main-app` uses Flyway (`src/main/resources/db/migration/V*__*.sql`), with `spring.jpa.hibernate.ddl-auto=validate` — schema changes must go through a new `V<n>__description.sql` file, never through Hibernate auto-DDL.

### Spring Boot 4 test API (main-app)

Tests target Spring Boot **4.0.3**, which relocated several test annotations/clients from their Boot 3 packages — don't assume Boot-3-era imports:
- `@WebMvcTest` → `org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest`
- `@DataJpaTest` → `org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest`
- `@AutoConfigureTestDatabase` → `org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase`
- MockMvc-style web tests use `RestTestClient` (`@AutoConfigureRestTestClient`, `org.springframework.test.web.servlet.client.RestTestClient`), not `MockMvc`/`WebTestClient`.

Unit tests (`src/test/java/org/mainapp/unit`) use Mockito (`@ExtendWith(MockitoExtension.class)`, `@Mock`/`@InjectMocks`). Integration tests (`.../integration`) use `@DataJpaTest` + Testcontainers `PostgreSQLContainer` with `@ServiceConnection`.
