package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;
import java.util.UUID;

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
    private List<Chapter> chapters;

    Module() {
    }

    public Module(boolean isActive, String name, String description, List<Chapter> chapters) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.chapters = chapters;
    }

    public Module(UUID contentId, Integer version, boolean isActive, String name, String description, List<Chapter> chapters) {
        super(contentId, version, isActive);
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

    public List<Chapter> getChapters() {
        return chapters;
    }
}
