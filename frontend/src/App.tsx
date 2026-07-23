import './style.css'
import { Matchday } from './components/Matchday'
import { Result } from './components/Results'
import { Logs } from './components/Logs';
import { useMatchday } from './context/MatchdayContext';
import { LeagueTable } from './components/LeagueTable';
import { useSimulation } from './hooks/useSimulation';

function App() {

  const {matchday, setMatchday} = useMatchday();
  const {startSimulation} = useSimulation(matchday);
  
  const handleMatchdayChange = (round: number ) => {
      setMatchday(round); 
  }
 
  return (
      <div className="min-h-screen bg-[radial-gradient(ellipse_at_top,_#1e293b,_#020617_70%)] font-sans text-slate-200 grid">
        <Matchday changeRound={handleMatchdayChange} />

        <div className="flex justify-center items-center mb-6">
            <button
                className="flex items-center gap-2 rounded-xl bg-gradient-to-r from-sky-500 to-indigo-600 px-6 py-3 text-sm font-bold uppercase tracking-wide text-white shadow-lg shadow-sky-500/30 transition-all hover:scale-105 hover:shadow-xl active:scale-95 focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-sky-400 focus-visible:ring-offset-2"
                onClick={() => startSimulation()}
            >
                Start
            </button>
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-10 gap-6 items-start px-6 py-4">
          
          <Logs/>

          <Result />


          <LeagueTable />

        </div>
        
      </div>
  )
}

export default App
