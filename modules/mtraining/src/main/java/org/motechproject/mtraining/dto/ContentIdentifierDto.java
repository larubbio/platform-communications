package org.motechproject.mtraining.dto;

import java.util.UUID;

/**
 * Object identifying the saved content in DB, which is returned when {@link org.motechproject.mtraining.service.CourseService} add content APIs are called
 */

public class ContentIdentifierDto {
    private UUID contentId;
    private Integer version;

    public ContentIdentifierDto(UUID contentId, Integer version) {
        this.contentId = contentId;
        this.version = version;
    }

    public UUID getContentId() {
        return contentId;
    }

    public Integer getVersion() {
        return version;
    }

}
