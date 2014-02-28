package org.motechproject.mtraining.dto;

import java.util.List;

/**
 * Object representing a chapter in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Chapter}.
 */

public class ChapterDto {
    private String name;
    private String description;
    private List<MessageDto> messages;

    public ChapterDto() {
    }

    public ChapterDto(String name, String description, List<MessageDto> messages) {
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
