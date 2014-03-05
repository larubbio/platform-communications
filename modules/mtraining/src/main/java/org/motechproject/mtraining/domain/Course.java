package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.util.List;

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
    private List<ContentIdentifier> modules;

    Course() {
    }

    public Course(String name, String description, List<ContentIdentifier> modules) {
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

    public List<ContentIdentifier> getModules() {
        return modules;
    }
}
