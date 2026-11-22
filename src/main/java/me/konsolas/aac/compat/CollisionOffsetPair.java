package me.konsolas.aac.compat;

public class CollisionOffsetPair {
    private int x;
    private int z;

    public CollisionOffsetPair(int i, int i2) {
        this.x = i;
        this.z = i2;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        CollisionOffsetPair collisionOffsetPair = (CollisionOffsetPair)obj;
        return this.x == collisionOffsetPair.x && this.z == collisionOffsetPair.z;
    }

    public int hashCode() {
        int value = this.x;
        value = 31 * value + this.z;
        return value;
    }
}

