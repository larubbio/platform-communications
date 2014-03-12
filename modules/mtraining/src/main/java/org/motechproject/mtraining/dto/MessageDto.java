package org.motechproject.mtraining.dto;

import java.util.UUID;

/**
 * Object representing a message in a course structure.
 * Expected by {@link org.motechproject.mtraining.service.CourseService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Message}.
 */

public class MessageDto extends ContentDto {
    private String name;
    private String externalId;
    private String description;

    public MessageDto() {
    }

    public MessageDto(boolean isActive, String name, String externalId, String description) {
        super(isActive);
        this.name = name;
        this.externalId = externalId;
        this.description = description;
    }

    public MessageDto(UUID contentId, boolean isActive, String name, String externalId, String description) {
        super(contentId, null, isActive);
        this.name = name;
        this.externalId = externalId;
        this.description = description;
    }

    public MessageDto(UUID contentId, Integer version, boolean isActive, String name, String externalId, String description) {
        super(contentId, version, isActive);
        this.name = name;
        this.externalId = externalId;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getDescription() {
        return description;
    }
}
