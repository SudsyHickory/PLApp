# Etap 1: Budowanie (używamy Javy 25 do kompilacji)
FROM container-registry.oracle.com/java/openjdk:25 AS build
WORKDIR /app

# Kopiujemy wrapper mavena (jeśli masz) lub instalujemy mavena
# Najbezpieczniej skopiować cały projekt, jeśli budujesz przez IDE
COPY . .
RUN ./mvnw clean package -DskipTests

# Etap 2: Uruchomienie (lekki obraz runtime)
FROM container-registry.oracle.com/java/openjdk:25
WORKDIR /app

# Kopiujemy zbudowany plik jar z poprzedniego etapu
COPY --from=build /app/target/*.jar app.jar

# Odpalamy aplikację
ENTRYPOINT ["java", "-jar", "app.jar"]