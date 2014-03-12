package org.motechproject.mtraining.dto;

import java.util.List;
import java.util.UUID;

/**
 * Object representing a module in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Module}.
 */

public class ModuleDto extends ContentDto {
    private String name;
    private String description;
    private List<ChapterDto> chapters;

    public ModuleDto() {
    }

    public ModuleDto(boolean isActive, String name, String description, List<ChapterDto> chapters) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.chapters = chapters;
    }

    public ModuleDto(UUID contentId, boolean isActive, String name, String description, List<ChapterDto> chapters) {
        super(contentId, null, isActive);
        this.name = name;
        this.description = description;
        this.chapters = chapters;
    }

    public ModuleDto(UUID contentId, Integer version, boolean isActive, String name, String description, List<ChapterDto> chapters) {
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

    public List<ChapterDto> getChapters() {
        return chapters;
    }
}
