package org.motechproject.mtraining.dto;

import java.util.List;
import java.util.UUID;

/**
 * Object representing a chapter in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Chapter}.
 */

public class ChapterDto extends ContentDto {
    private String name;
    private String description;
    private List<MessageDto> messages;

    public ChapterDto() {
    }

    public ChapterDto(boolean isActive, String name, String description, List<MessageDto> messages) {
        super(isActive);
        this.name = name;
        this.description = description;
        this.messages = messages;
    }

    public ChapterDto(UUID contentId, boolean isActive, String name, String description, List<MessageDto> messages) {
        super(contentId, null, isActive);
        this.name = name;
        this.description = description;
        this.messages = messages;
    }

    public ChapterDto(UUID contentId, Integer version, boolean isActive, String name, String description, List<MessageDto> messages) {
        super(contentId, version, isActive);
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

    public List<MessageDto> getMessages() {
        return messages;
    }
}
