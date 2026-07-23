import { createContext, useContext, useState } from "react"

interface MatchdayContextType {
    matchday: number,
    setMatchday: (matchday: number) => void
}

interface MatchdayContextProps {
    children: React.ReactNode
}

const MatchdayContext = createContext<MatchdayContextType | null>(null)

export const MatchdayContextProvider = ({children} : MatchdayContextProps) => {
    const [matchday, setMatchday] = useState<number>(1);

     return (
        <MatchdayContext.Provider value={{ matchday, setMatchday }}>
        {children}
        </MatchdayContext.Provider>
  );
}

export const useMatchday = () => 
    {
        const context =  useContext(MatchdayContext);
        if(!context)
            throw new Error();
        return context;
    }