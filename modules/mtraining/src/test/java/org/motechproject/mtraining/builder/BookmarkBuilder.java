package org.motechproject.mtraining.builder;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import java.util.UUID;

public class BookmarkBuilder {

    private String callerId;
    private DateTime dateModified;

    private ContentIdentifierDto courseIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto moduleIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto chapterIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto messageIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);


    private ContentIdentifier courseIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier moduleIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier chapterIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier messageIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);

    public static final String DATE_TIME_FORMAT = "dd-MM-YYYY HH:mm:ss.SSS";

    private static DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern(DATE_TIME_FORMAT);

    public BookmarkBuilder withExternalId(String callerId) {
        this.callerId = callerId;
        return this;
    }

    public BookmarkBuilder modifiedOn(DateTime dateTime) {
        this.dateModified = DateTime.parse(dateTime.toString(DATE_TIME_FORMAT), dateTimeFormatter);
        return this;
    }

    public Bookmark build() {
        return new Bookmark(callerId, courseIdentifier, moduleIdentifier, chapterIdentifier, messageIdentifier, dateModified);
    }

    public BookmarkDto buildDto() {
        return new BookmarkDto(callerId, courseIdentifierDto, moduleIdentifierDto, chapterIdentifierDto, messageIdentifierDto, dateModified);
    }
}
