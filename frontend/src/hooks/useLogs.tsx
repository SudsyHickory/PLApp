import { useEffect, useRef, useState } from "react"
import { useMatchday } from "../context/MatchdayContext";
import { useStompSubscription } from "./useStompSubscription";
import type { Log } from "../types";

interface MatchLogEvent {
    minute: number,
    match: {
        homeTeam: string,
        awayTeam: string,
        matchday: number
    }
}

export function useLogs()
{
    const [logs, setLogs] = useState<Log[]>([]);

    const {matchday,} = useMatchday();

    const matchdayRef = useRef(matchday);

    useEffect(() => {
        matchdayRef.current = matchday;
        setLogs([]);
    }, [matchday]);

    const handleAddLog = (log: Log) => {
        if(matchdayRef.current===log.currentRound)
        {
            setLogs(logs =>
                {
                    const nextLogs = [...logs,log];
                    if (nextLogs.length > 50)
                        return nextLogs.slice(1);
                    return nextLogs;
                }
            )
        }
    }

    useStompSubscription<MatchLogEvent>('/topic/match-logs', (data) => {
        const event: Log = {
            minute: data.minute,
            homeTeam: data.match.homeTeam,
            awayTeam: data.match.awayTeam,
            currentRound: data.match.matchday
        }
        handleAddLog(event);
    });

    return logs;

}