import { useEffect, useState } from "react";
import { useStompSubscription } from "./useStompSubscription";
import type { Team } from "../types";

export function useLeagueTable() {
    const [teams, setTeams] = useState<Team[] | null>(null);

    useStompSubscription<Team[]>('/simulation/table-update', (list) => {
        setTeams(list);
    });

    const loadTable = async () => {
        try{
            const response = await fetch(`http://localhost:8080/api/football/teams/table`);
            if (!response.ok) throw new Error('Błąd sieci!');
            const data: Team[] = await response.json();
            setTeams(data);
        }
        catch(error)
        {
            console.error();
        }
    }

    useEffect(()=>{
        loadTable();
    },[])

    return teams;
}
