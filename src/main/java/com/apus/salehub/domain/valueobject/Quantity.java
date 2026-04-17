package com.apus.salehub.domain.valueobject;

public record Quantity(int value) {

    public Quantity {
        if (value < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative: " + value);
        }
    }

    public static Quantity of(int value) {
        return new Quantity(value);
    }

    public static Quantity zero() {
        return new Quantity(0);
    }

    public Quantity add(Quantity other) {
        return new Quantity(this.value + other.value);
    }

    public Quantity subtract(Quantity other) {
        int result = this.value - other.value;
        if (result < 0) {
            throw new IllegalArgumentException("Cannot subtract " + other.value + " from " + this.value + ": result would be negative");
        }
        return new Quantity(result);
    }

    public boolean isZero() {
        return this.value == 0;
    }

    public boolean isLessThan(Quantity other) {
        return this.value < other.value;
    }

    public boolean isLessThanOrEqual(Quantity other) {
        return this.value <= other.value;
    }

    public boolean isGreaterThan(Quantity other) {
        return this.value > other.value;
    }
}
