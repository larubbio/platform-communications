package org.motechproject.mtraining.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;
import org.motechproject.mtraining.util.DateTimeUtil;
import org.motechproject.mtraining.util.JSONUtil;

/**
 * Object representing a bookmark node in a bookmark structure.
 * Expected by {@link org.motechproject.mtraining.service.BookmarkService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Bookmark}.
 */

public class BookmarkDto {

    @JsonIgnore
    private String externalId;

    @JsonProperty(value = "course")
    private ContentIdentifierDto course;

    @JsonProperty(value = "module")
    private ContentIdentifierDto module;

    @JsonProperty(value = "chapter")
    private ContentIdentifierDto chapter;

    @JsonProperty(value = "message")
    private ContentIdentifierDto message;

    @JsonProperty
    private String dateModified;

    public BookmarkDto() {
    }

    public BookmarkDto(String externalId, ContentIdentifierDto course, ContentIdentifierDto module, ContentIdentifierDto chapter, ContentIdentifierDto message, DateTime dateModified) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.dateModified = DateTimeUtil.formatDateTime(dateModified);
    }

    public String getExternalId() {
        return externalId;
    }

    public ContentIdentifierDto getCourse() {
        return course;
    }

    public ContentIdentifierDto getModule() {
        return module;
    }

    public ContentIdentifierDto getChapter() {
        return chapter;
    }

    public ContentIdentifierDto getMessage() {
        return message;
    }

    public String getDateModified() {
        return dateModified;
    }

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }
}
