package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;

/**
 * Couch document object representing a Module content.
 */

@TypeDiscriminator("doc.type === 'Module'")
public class Module extends Content {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<ContentIdentifier> chapters;

    Module() {
    }

    public Module(String name, String description, List<ContentIdentifier> chapters) {
        this.name = name;
        this.description = description;
        this.chapters = chapters;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<ContentIdentifier> getChapters() {
        return chapters;
    }
}
