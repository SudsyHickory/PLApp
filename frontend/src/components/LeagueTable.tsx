import { useLeagueTable } from "../hooks/useLeagueTable"

const getZoneClasses = (position: number, totalTeams: number) => {
    if (position <= 3) {
        return "border-l-4 border-blue-400 bg-blue-500/10 hover:bg-blue-500/20";
    }
    if (position > totalTeams - 3) {
        return "border-l-4 border-red-400 bg-red-500/10 hover:bg-red-500/20";
    }
    return "hover:bg-white/5";
};

export const LeagueTable = () => {
    const teams = useLeagueTable();

    return (
    <div className="lg:col-span-2">
      <div className="h-10 flex justify-start align-center ml-4">
        <h3 className="text-lg font-bold text-slate-200 uppercase tracking-wide mb-4">Table</h3>
      </div>
      <div className="bg-slate-800/70 rounded-xl shadow-sm border border-slate-700/50 overflow-hidden w-full">
        <table className="w-full">
          <thead className="bg-slate-900/60">
            <tr>
              <th className="px-4 py-3 text-xs font-semibold text-slate-400 uppercase tracking-wider text-left">Pos.</th>
              <th className="px-4 py-3 text-xs font-semibold text-slate-400 uppercase tracking-wider text-left">Team</th>
              <th className="px-4 py-3 text-xs font-semibold text-slate-400 uppercase tracking-wider text-center">Pl</th>
              <th className="px-4 py-3 text-xs font-semibold text-slate-400 uppercase tracking-wider text-center">GD</th>
              <th className="px-4 py-3 text-xs font-semibold text-slate-400 uppercase tracking-wider text-center">Pts</th>
            </tr>
          </thead>
          <tbody>
          {teams?.map((team, index) => (
            <tr key={`team-${team.name}`} className={`border-b border-slate-700/50 last:border-0 transition-colors ${getZoneClasses(index + 1, teams.length)}`}>
              <td className="px-4 py-3 text-sm text-slate-300">{index + 1}</td>
              <td className="px-4 py-3 text-sm text-slate-300">
                <div className="flex items-center gap-3">
                  <img src={team.crest} className="w-6 h-6 object-contain" alt="" />
                  <span className="font-semibold text-slate-100">{team.name}</span>
                </div>
              </td>
              <td className="px-4 py-3 text-sm text-slate-300 text-center">{team.matchesPlayed}</td>
              <td className="px-4 py-3 text-sm text-slate-300 text-center">{team.goalsDifference > 0 ? `+${team.goalsDifference}` : team.goalsDifference}</td>
              <td className="font-bold text-white text-center">{team.points}</td>
            </tr>
          ))}
          </tbody>
        </table>
        
      </div>
    </div>


   
    );
}