package org.mainapp.data;

public class MatchEvent {

    private MatchDTOSim match;
    private int minute;
    private MatchAction action;

    public MatchEvent(MatchDTOSim match, int minute, MatchAction action) {
        this.match = match;
        this.minute = minute;
        this.action = action;
    }

    public MatchEvent() {
    }

    public MatchDTOSim getMatch() {
        return match;
    }

    public void setMatch(MatchDTOSim match) {
        this.match = match;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public MatchAction getAction() {
        return action;
    }

    public void setAction(MatchAction action) {
        this.action = action;
    }
}
