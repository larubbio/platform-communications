package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.UUID;

/**
 * Couch document object representing a Message content.
 */

@TypeDiscriminator("doc.type === 'Message'")
public class Message extends Content {

    @JsonProperty
    private String name;

    @JsonProperty
    private String externalId;

    @JsonProperty
    private String description;

    Message() {
    }

    public Message(boolean isActive, String name, String externalId, String description) {
        super(isActive);
        this.name = name;
        this.externalId = externalId;
        this.description = description;
    }

    public Message(UUID contentId, Integer version, boolean isActive, String name, String externalId, String description) {
        super(contentId, version, isActive);
        this.name = name;
        this.externalId = externalId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }

}
