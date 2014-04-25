package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Couch document object representing an Location content.
 * + block : the name of the block for a given geographical location
 * + district : the name of the district for a given geographical location
 * + state : the name of the state for a given geographical location
 */

public class Location {

    @JsonProperty
    private String block;

    @JsonProperty
    private String district;

    @JsonProperty
    private String state;

    public Location() {
    }

    public Location(String block, String district, String state) {
        this.block = block;
        this.district = district;
        this.state = state;
    }

    public String getBlock() {
        return block;
    }

    public String getDistrict() {
        return district;
    }

    public String getState() {
        return state;
    }
}
