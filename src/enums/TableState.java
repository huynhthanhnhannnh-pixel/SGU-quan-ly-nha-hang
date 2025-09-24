package enums;

public enum TableState {
    EMPTY("empty"),
    FULL("full"),
    OCCUPIED("occupied");

    private final String stateName;

    private TableState(String stateName) { this.stateName = stateName; }

    // lấy enum thông qua metadata
    public static TableState fromPosition(String input) {
        for (TableState type : TableState.values()) {
            if (type.getPosition().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null; // or throw an exception if you prefer
    }

    public String getPosition() { return stateName; } // Lấy metadata từ enum
}
