package com.apus.salehub.domain.valueobject;

public record Address(
        String street,
        String city,
        String state,
        String postalCode,
        String country
) {

    public Address {
        if (street == null) {
            throw new IllegalArgumentException("Street cannot be null");
        }
        if (city == null) {
            throw new IllegalArgumentException("City cannot be null");
        }
        if (country == null) {
            throw new IllegalArgumentException("Country cannot be null");
        }
    }

    public String getFullAddress() {
        var sb = new StringBuilder();
        sb.append(street).append(", ").append(city);
        if (state != null && !state.isBlank()) {
            sb.append(", ").append(state);
        }
        if (postalCode != null && !postalCode.isBlank()) {
            sb.append(" ").append(postalCode);
        }
        sb.append(", ").append(country);
        return sb.toString();
    }
}
