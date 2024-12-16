package com.company.customeremulation.service.record;

import com.fasterxml.jackson.annotation.JsonProperty;

public record NominatimReverseResponse(
        @JsonProperty("house_number") String house_number,
        @JsonProperty("display_name") String displayName) {
}