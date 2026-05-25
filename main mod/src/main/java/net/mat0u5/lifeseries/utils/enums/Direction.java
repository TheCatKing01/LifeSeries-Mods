package net.mat0u5.lifeseries.utils.enums;

public enum Direction {
    WEST(-1, 0),
    EAST(1, 0),
    NORTH(0, -1),
    SOUTH(0, 1),

    NORTH_WEST(-1, -1),
    NORTH_EAST(1, -1),
    SOUTH_WEST(-1, 1),
    SOUTH_EAST(1, 1);

    public final int x, z;

    Direction(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public static Direction[] getCardinalDirections() {
        return new Direction[]{WEST, EAST, NORTH, SOUTH};
    }
}
