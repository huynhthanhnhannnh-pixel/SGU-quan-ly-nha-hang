package enums;

public enum WorkerType {
    WAITER("waiter"),
    CHEF("chef");

    private final String position;

    private WorkerType(String position) { this.position = position; }

    // lấy enum thông qua metadata
    public static WorkerType fromPosition(String input) {
        for (WorkerType type : WorkerType.values()) {
            if (type.getPosition().equalsIgnoreCase(input)) {
                return type;
            }
        }
        return null; // or throw an exception if you prefer
    }

    public String getPosition() { return position; } // Lấy metadata từ enum
}

