export interface Match {
    id: number,
    homeTeam: string,
    homeTeamGoals: number,
    homeTeamCrest: string,
    awayTeam: string,
    awayTeamGoals: number,
    awayTeamCrest: string,
    currentMinute: number,
    status: string
}

export interface Log {
    minute: number,
    homeTeam: string,
    awayTeam: string,
    currentRound: number
}

export interface Team {
    name: string,
    points: number,
    crest: string,
    goalsDifference: number,
    matchesPlayed: number
}
