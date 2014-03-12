package org.motechproject.mtraining.dto;

import java.util.List;
import java.util.UUID;

/**
 * Object representing a course node in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Course}.
 */

public class CourseDto extends ContentDto {
    private String name;
    private String description;
    private List<ModuleDto> modules;

    public CourseDto() {
    }

    public CourseDto(boolean isActive, String name, String description, List<ModuleDto> modules) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.modules = modules;
    }

    public CourseDto(UUID contentId, boolean isActive, String name, String description, List<ModuleDto> modules) {
        super(contentId, null, isActive);
        this.name = name;
        this.description = description;
        this.modules = modules;
    }

    public CourseDto(UUID contentId, Integer version, boolean isActive, String name, String description, List<ModuleDto> modules) {
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

    public List<ModuleDto> getModules() {
        return modules;
    }
}
