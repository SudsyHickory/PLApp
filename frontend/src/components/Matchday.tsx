import { useMatchday } from "../context/MatchdayContext"

interface MatchdayProps {
    changeRound: (round: number) => void
}

const TOTAL_ROUNDS = 38

export const Matchday = ({changeRound} : MatchdayProps) => {

    const {matchday, } = useMatchday();
    const nextMatchday= () =>
    {
        if(matchday<TOTAL_ROUNDS)
            changeRound(matchday+1)
    }

    const previousMatchday = () =>
    {
        if(matchday>1)
            changeRound(matchday-1);
    }

    const progress = (matchday / TOTAL_ROUNDS) * 100;

    return (

        <div className="col-span-full flex justify-center w-full py-6">

            <div className="w-full max-w-md bg-gradient-to-br from-slate-900 to-slate-700 rounded-2xl p-6 shadow-lg scale-90">

                <div className="flex items-center justify-around">

                    <button
                        className="bg-slate-800 hover:bg-sky-600 text-white p-3 rounded-lg transition-colors disabled:opacity-30 disabled:hover:bg-slate-800"
                        onClick={() => previousMatchday()}
                        disabled={matchday<=1}
                    >
                        &lt;
                    </button>

                    <div className="round-display flex flex-col items-center">
                        <span className="label text-xs text-slate-300 font-semibold uppercase tracking-widest">Matchday</span>
                        <span
                            key={matchday}
                            id="current-round-val"
                            className="text-6xl font-bold text-white tabular-nums animate-[matchday-fade_300ms_ease-out]"
                        >
                            {matchday}
                        </span>
                    </div>

                    <button
                        className="bg-slate-800 hover:bg-sky-600 text-white p-3 rounded-lg transition-colors disabled:opacity-30 disabled:hover:bg-slate-800"
                        onClick={() => nextMatchday()}
                        disabled={matchday>=TOTAL_ROUNDS}
                    >
                        &gt;
                    </button>

                </div>

                <div className="mt-5">
                    <div className="flex justify-between text-xs text-slate-300 font-semibold mb-1.5 tabular-nums">
                        <span>{matchday}/{TOTAL_ROUNDS}</span>
                    </div>
                    <div className="w-full h-2 bg-white/10 rounded-full overflow-hidden">
                        <div
                            className="h-full bg-sky-500 rounded-full transition-[width] duration-300 ease-out"
                            style={{ width: `${progress}%` }}
                        />
                    </div>
                </div>

            </div>

        </div>

    )

}
