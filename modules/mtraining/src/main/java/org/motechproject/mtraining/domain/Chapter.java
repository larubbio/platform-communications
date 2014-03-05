package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;

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
    private List<ContentIdentifier> messages;

    Chapter() {
    }

    public Chapter(String name, String description, List<ContentIdentifier> messages) {
        this.name = name;
        this.description = description;
        this.messages = messages;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ContentIdentifier> getMessages() {
        return messages;
    }
}
