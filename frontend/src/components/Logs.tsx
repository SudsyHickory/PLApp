import { useLogs } from "../hooks/useLogs";
import { LogCard } from "./LogCard";

export const Logs = () => {

    const logs = useLogs();
    
    return (
        <div className="lg:col-span-3 ml-4 text-lg font-bold text-slate-200 uppercase tracking-wide ">
            <h3 className="flex justify-center mb-7">Live coverage</h3>
            <div className="bg-slate-800/70 rounded-xl p-4 shadow-sm border border-slate-700/50 min-h-150  text-slate-400 text-sm italic">
               {[...logs].reverse().map((l, index) => (
                <LogCard key={index} log={l} />
                ))}
            </div>
        </div>
    )
}