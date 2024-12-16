package com.company.customeremulation.service.record;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Properties(
        @JsonProperty String osm_type,
        @JsonProperty String display_name,
        GeoAddress address
) {
}
