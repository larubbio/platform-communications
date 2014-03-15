package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;
import java.util.UUID;

/**
 * Couch document object representing a Course content.
 */

@TypeDiscriminator("doc.type === 'Course'")
public class Course extends Content {
    @JsonProperty
    private String name;

    @JsonProperty
    private String description;

    @JsonProperty
    private List<Module> modules;

    Course() {
    }

    public Course(boolean isActive, String name, String description, List<Module> modules) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.modules = modules;
    }

    public Course(UUID contentId, Integer version, boolean isActive, String name, String description, List<Module> modules) {
        super(contentId, version, isActive);
        this.name = name;
        this.description = description;
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<Module> getModules() {
        return modules;
    }
}
