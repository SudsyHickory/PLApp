import type { Log } from "../types";


interface LogCardProps {
    log: Log
}


export const LogCard = ({log} : LogCardProps) => {
    return (
        <div className="grid grid-cols-1 lg:grid-cols-5 bg-amber-200 items-center mb-3 rounded-xl p-2">

            <div className="text-left lg:col-span-1">
                <span>{log.minute}'</span> 
            </div>
            
            <div className="text-left lg:col-span-3 font-medium">
                <span>{log.homeTeam} vs {log.awayTeam}</span>
            </div>
            
            <div className="text-right lg:col-span-1">
                <span className="text-red-400 font-bold">GOAL! ⚽</span>
            </div>
        </div>
    );
}