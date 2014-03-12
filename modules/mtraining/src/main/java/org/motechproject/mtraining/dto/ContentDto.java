package org.motechproject.mtraining.dto;

import java.util.UUID;

public abstract class ContentDto {
    private UUID contentId;
    private Integer version;
    private boolean isActive;

    protected ContentDto() {
    }

    protected ContentDto(boolean isActive) {
        this.isActive = isActive;
    }

    protected ContentDto(UUID contentId, Integer version, boolean isActive) {
        this(isActive);
        this.contentId = contentId;
        this.version = version;
    }

    public UUID getContentId() {
        return contentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public ContentIdentifierDto toContentIdentifierDto() {
        return new ContentIdentifierDto(contentId, version);
    }

    public boolean isActive() {
        return isActive;
    }
}
