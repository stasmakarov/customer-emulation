package com.company.customeremulation.service.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeoAddress(
        @JsonProperty String road,
        @JsonProperty String house_number,
        @JsonProperty String street,
        @JsonProperty String city
) {
}
