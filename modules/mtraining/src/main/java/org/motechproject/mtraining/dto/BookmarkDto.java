package org.motechproject.mtraining.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.motechproject.mtraining.domain.ContentIdentifier;

/**
 * Object representing a bookmark node in a bookmark structure.
 * Expected by {@link org.motechproject.mtraining.service.BookmarkService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Bookmark}.
 */

public class BookmarkDto {
    @JsonIgnore
    private String externalId;
    private ContentIdentifier course;
    private ContentIdentifier module;
    private ContentIdentifier chapter;
    private ContentIdentifier message;

    public BookmarkDto() {
    }

    public BookmarkDto(String externalId, ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
    }

    public String getExternalId() {
        return externalId;
    }

    public ContentIdentifier getCourse() {
        return course;
    }

    public ContentIdentifier getModule() {
        return module;
    }

    public ContentIdentifier getChapter() {
        return chapter;
    }

    public ContentIdentifier getMessage() {
        return message;
    }
}
