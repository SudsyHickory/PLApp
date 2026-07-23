import { useCallback, useEffect, useState } from "react";
import { useMatchday } from "../context/MatchdayContext";
import type { Match } from "../types";

export function useMatches() {
    const {matchday} = useMatchday();
    const [matches, setMatches] = useState<Match[]>([]);
    
    const handleMatchUpdate = (updatedMatch: Match) => {
        setMatches((matches) =>
            matches.map(match => {
                if(match.id===updatedMatch.id)
                    return updatedMatch
                return match
            })
        );
    }

    const loadMatches = useCallback(async () =>{
        try{
                const response = await fetch(`http://localhost:8080/api/football/matches/${matchday}`);
                if (!response.ok) throw new Error('Błąd sieci!');
                const data: Match[] = await response.json();
                setMatches(data);
            }
        catch(error)
        {
            console.error();
        }
    },[matchday]);

    useEffect(()=>{
        loadMatches();
    },[loadMatches])

    return {matches, handleMatchUpdate};
}