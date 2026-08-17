package de.finnk.ghostnet.model;

public enum GhostNetStatus {
//Das Gesiternetz wurde gemeldet und wartet auf eine Bergung
    REPORTED("Gemeldet"),
    //Eine Person hat die Bergung übernommen
    RECOVERY_PENDING("Bergung bevorstehend"),
    //Das Geisternetz wurde erfolgreich geborgen
    RECOVERED("Bergung abgeschlossen"),
    //Das Geisternetz konnte nicht gefunden werden
    MISSING("Vermisst");

    private final String displayName;
    
    GhostNetStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
    }