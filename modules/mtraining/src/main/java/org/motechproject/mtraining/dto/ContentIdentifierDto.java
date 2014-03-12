package org.motechproject.mtraining.dto;

import java.util.UUID;

/**
 * Object identifying the saved content in DB, which is returned when {@link org.motechproject.mtraining.service.CourseService} add content APIs are called
 */

public class ContentIdentifierDto {
    private UUID contentId;
    private Integer version;

    public ContentIdentifierDto() {
    }

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

    @Override
    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        ContentIdentifierDto otherDto = (ContentIdentifierDto) other;
        return this.contentId.equals(otherDto.contentId) && this.version.equals(otherDto.version);
    }

    @Override
    public int hashCode() {
        int result = contentId != null ? contentId.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        return result;
    }
}
