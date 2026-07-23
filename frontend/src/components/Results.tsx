import { MatchCard } from "./MatchCard"
import { useMatches } from "../hooks/useMatches"
import { useStompSubscription } from "../hooks/useStompSubscription"
import type { Match } from "../types"

interface MatchUpdateEvent {
    match: Match
}

export const Result = () => {

    const {matches, handleMatchUpdate} = useMatches();

    useStompSubscription<MatchUpdateEvent>('/simulation/match-update', (event) => {
        handleMatchUpdate(event.match);
    });

    return (
    <div className="lg:col-span-4">
            <div className="h-10 flex justify-start align-center ml-4 mb-4">
                <h3 className="text-lg font-bold text-slate-200 uppercase tracking-wide mb-4">Results</h3>
            </div>
            <div className="">
                {matches?.length === 0 || matches === null ? (<p>Czekam na mecze...</p>) : (
                    matches.map((m) => (
                        <MatchCard key={m.id} match={m} />
                    ))
                )}
            </div>
    </div>
    )
}