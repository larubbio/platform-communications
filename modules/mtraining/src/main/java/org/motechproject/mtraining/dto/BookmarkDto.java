package org.motechproject.mtraining.dto;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.joda.time.DateTime;

/**
 * Object representing a bookmark node in a bookmark structure.
 * Expected by {@link org.motechproject.mtraining.service.BookmarkService} APIs to manage a mTraining {@link org.motechproject.mtraining.domain.Bookmark}.
 */

public class BookmarkDto {

    @JsonIgnore
    private String externalId;

    @JsonProperty(value = "courseIdentifier")
    private ContentIdentifierDto course;

    @JsonProperty(value = "moduleIdentifier")
    private ContentIdentifierDto module;

    @JsonProperty(value = "chapterIdentifier")
    private ContentIdentifierDto chapter;

    @JsonProperty(value = "messageIdentifier")
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
        this.dateModified = formatDateTime(dateModified);
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

    private static String formatDateTime(DateTime dateTime) {
        return dateTime == null ? null : dateTime.toString("dd-MM-yyyy HH:mm:ss.SSS");
    }
}
