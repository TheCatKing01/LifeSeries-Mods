package net.mat0u5.lifeseries.seasons.session;

public enum SessionStatus {
    NOT_STARTED("Not Started"),
    STARTED("Started"),
    PAUSED("Paused"),
    FINISHED("Finished"),
    UNASSIGNED("Unassigned");

    private String name;

    SessionStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SessionStatus getSessionName(String name) {
        for (SessionStatus status : SessionStatus.values()) {
            if (status.getName().equalsIgnoreCase(name)) {
                return status;
            }
        }
        return UNASSIGNED;
    }
}
