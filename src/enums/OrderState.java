package enums;

public enum OrderState {
    NEW("new"),
    UNFINISHED("unfinished"),
    COMPLETED("completed");

    private final String stateName;

    private OrderState(String stateName) { this.stateName = stateName; }

    // lấy enum thông qua metadata
    public static OrderState fromPosition(String input) {
        for (OrderState type : OrderState.values()) {
            if (type.getPosition().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null; // or throw an exception if you prefer
    }

    public String getPosition() { return stateName; } // Lấy metadata từ enum
}
