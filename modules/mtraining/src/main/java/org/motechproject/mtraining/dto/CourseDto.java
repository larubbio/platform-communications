package org.motechproject.mtraining.dto;

import java.util.List;

/**
 * Object representing a course node in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Course}.
 */

public class CourseDto {
    private String name;
    private String description;
    private ContentIdentifierDto courseIdentifier;
    private List<ModuleDto> modules;


    public CourseDto() {
    }

    public CourseDto(String name, String description, List<ModuleDto> modules) {
        this.name = name;
        this.description = description;
        this.modules = modules;
        this.courseIdentifier = null;
    }

    public CourseDto(String name, String description, ContentIdentifierDto courseIdentifier, List<ModuleDto> modules) {
        this.name = name;
        this.description = description;
        this.courseIdentifier = courseIdentifier;
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public ContentIdentifierDto getCourseIdentifier() {
        return courseIdentifier;
    }

    public List<ModuleDto> getModules() {
        return modules;
    }
}
