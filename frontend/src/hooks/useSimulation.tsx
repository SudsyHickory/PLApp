export function useSimulation(matchday: number) {
    const startSimulation = () => {
        const url = `http://localhost:8080/api/football/matches/simulation/${matchday}`
        fetch(url, { method: 'POST' });
    }

    return { startSimulation };
}
