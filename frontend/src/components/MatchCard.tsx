import type { Match } from "../types";

interface MatchCardProps {
    match: Match
}

export const MatchCard = ({match}: MatchCardProps) => {

    const renderStatusBadge = (status: string) => {
        switch (status) {
            case 'LIVE':
                return (
                    <div className="bg-lime-500/10 text-lime-400 border border-lime-500/30 text-xs font-medium px-2.5 py-1 rounded-full flex items-center gap-1.5">
                        <span className="text-lime-500">●</span>
                        <span>{match.currentMinute}'</span>
                    </div>
                );
            case 'FINISHED':
                return (
                    <div className="bg-red-500/10 text-red-400 border border-red-500/30 text-xs font-medium px-2.5 py-1 rounded-full flex items-center justify-center">
                        <span>FINISHED</span>
                    </div>
                );
            case 'SCHEDULED':
                return (
                    <div className="bg-white/5 border border-white/10 text-slate-300 text-xs font-medium px-2.5 py-1 rounded-full flex items-center justify-center">
                        <span>SCHEDULED</span>
                    </div>
                );
            default:
                return null;
        }
    };



    return (
        
        <div className="bg-slate-800/70 rounded-xl p-3 max-w-200 shadow-sm border border-slate-700/50 flex items-center justify-between mb-6 hover:shadow-md transition-shadow">

            <div className="w-1/3 flex justify-end items-center gap-2 mr-2">

                <span className="font-semibold text-slate-200 text-sm whitespace-nowrap">
                    {match.homeTeam || 'Gospodarze'}
                </span>

                {match.homeTeamCrest ?
                (
                    <img src={match.homeTeamCrest}  alt="" className="w-7 h-7 object-contain" />
                ) :
                (
                    <div className="w-6 h-6 bg-slate-700 rounded-full" />
                )}

            </div>

            <div
                key={`${match.homeTeamGoals}-${match.awayTeamGoals}`}
                className="bg-slate-950 text-white font-bold px-4 py-1.5 rounded-full text-sm font-mono tracking-widest animate-[score-bump_400ms_ease-out]"
            >
                <span>{match.homeTeamGoals} : {match.awayTeamGoals}</span>
            </div>

            <div className="w-1/3 flex justify-start items-center gap-2 ml-2">

                {match.awayTeamCrest ?
                (
                    <img src={match.awayTeamCrest}  alt="" className="w-7 h-7 object-contain" />
                ) :
                (
                    <div className="w-6 h-6 bg-slate-700 rounded-full" />
                )}

                <span className="font-semibold text-slate-200 text-sm">
                    {match.awayTeam || 'Goście'}
                </span>

            </div>
            

            <div>
                {renderStatusBadge(match.status)}
            </div>
           
        </div>
        
    
    )
}