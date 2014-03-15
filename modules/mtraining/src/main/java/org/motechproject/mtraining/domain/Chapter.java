package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;
import java.util.UUID;

/**
 * Couch document object representing a Chapter content.
 */

@TypeDiscriminator("doc.type === 'Chapter'")
public class Chapter extends Content {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<Message> messages;

    Chapter() {
    }

    public Chapter(boolean isActive, String name, String description, List<Message> messageIdentifiers) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.messages = messageIdentifiers;
    }

    public Chapter(UUID contentId, Integer version, boolean isActive, String name, String description, List<Message> messageIdentifiers) {
        super(contentId, version, isActive);
        this.name = name;
        this.description = description;
        this.messages = messageIdentifiers;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Message> getMessages() {
        return messages;
    }
}
