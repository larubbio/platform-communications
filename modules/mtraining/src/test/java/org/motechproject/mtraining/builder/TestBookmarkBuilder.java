package org.motechproject.mtraining.builder;

import org.joda.time.DateTime;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import java.util.UUID;

public class TestBookmarkBuilder {

    private String callerId;
    private DateTime dateModified;

    private ContentIdentifierDto courseIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto moduleIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto chapterIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto messageIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
    private ContentIdentifierDto quizIdentifierDto = null;

    private ContentIdentifier courseIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier moduleIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier chapterIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier messageIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
    private ContentIdentifier quizIdentifier = null;

    public TestBookmarkBuilder withExternalId(String callerId) {
        this.callerId = callerId;
        return this;
    }

    public TestBookmarkBuilder modifiedOn(DateTime dateTime) {
        this.dateModified = dateTime;
        return this;
    }

    public Bookmark build() {
        return new Bookmark(callerId, courseIdentifier, moduleIdentifier, chapterIdentifier, messageIdentifier, quizIdentifier, dateModified);
    }

    public BookmarkDto buildDto() {
        return new BookmarkDto(callerId, courseIdentifierDto, moduleIdentifierDto, chapterIdentifierDto, messageIdentifierDto, quizIdentifierDto, dateModified);
    }

    public BookmarkDto buildDtoWithMessage() {
        nullifyQuiz();
        return new BookmarkDto(callerId, courseIdentifierDto, moduleIdentifierDto, chapterIdentifierDto, messageIdentifierDto, quizIdentifierDto, dateModified);
    }

    public TestBookmarkBuilder withModule(UUID moduleIdentifier) {
        this.moduleIdentifier = new ContentIdentifier(moduleIdentifier, 1);
        this.moduleIdentifierDto = new ContentIdentifierDto(moduleIdentifier, 1);
        return this;
    }

    public TestBookmarkBuilder withCourse(UUID courseId) {
        this.courseIdentifier = new ContentIdentifier(courseId, 1);
        this.courseIdentifierDto = new ContentIdentifierDto(courseId, 1);
        return this;
    }

    public TestBookmarkBuilder withoutCourse() {
        this.courseIdentifier = null;
        this.courseIdentifierDto = null;
        return this;
    }

    public TestBookmarkBuilder withCourse(UUID courseId, Integer version) {
        this.courseIdentifier = new ContentIdentifier(courseId, version);
        this.courseIdentifierDto = new ContentIdentifierDto(courseId, version);
        return this;
    }


    public TestBookmarkBuilder withMessage(UUID courseId) {
        this.messageIdentifier = new ContentIdentifier(courseId, 1);
        this.messageIdentifierDto = new ContentIdentifierDto(courseId, 1);
        nullifyQuiz();
        return this;
    }

    public TestBookmarkBuilder withChapter(UUID chapterId) {
        this.chapterIdentifier = new ContentIdentifier(chapterId, 1);
        this.chapterIdentifierDto = new ContentIdentifierDto(chapterId, 1);
        return this;
    }

    public TestBookmarkBuilder withQuiz(UUID quizId) {
        this.quizIdentifier = new ContentIdentifier(quizId, 1);
        this.quizIdentifierDto = new ContentIdentifierDto(quizId, 1);
        nullifyMessage();
        return this;
    }

    private void nullifyMessage() {
        this.messageIdentifier = null;
        this.messageIdentifierDto = null;
    }

    public TestBookmarkBuilder withoutQuiz() {
        nullifyQuiz();
        return this;
    }

    private void nullifyQuiz() {
        this.quizIdentifier = null;
        this.quizIdentifierDto = null;
    }

    public TestBookmarkBuilder withoutModule() {
        this.moduleIdentifier = null;
        this.moduleIdentifierDto = null;
        return this;
    }

    public TestBookmarkBuilder withoutChapter() {
        this.chapterIdentifier = null;
        this.chapterIdentifierDto = null;
        return this;
    }

    public TestBookmarkBuilder withoutMessage() {
        this.messageIdentifier = null;
        this.messageIdentifierDto = null;
        return this;
    }

    public BookmarkDto buildDtoWithoutMessage() {
        return new BookmarkDto(callerId, courseIdentifierDto, moduleIdentifierDto, chapterIdentifierDto, null, null, dateModified);
    }

    public TestBookmarkBuilder withMessageAndQuiz(UUID someId) {
        this.messageIdentifier = new ContentIdentifier(someId,1);
        this.messageIdentifierDto = new ContentIdentifierDto(someId,1);
        this.quizIdentifier = new ContentIdentifier(someId,1);
        this.quizIdentifierDto = new ContentIdentifierDto(someId,1);
        return this;
    }
}
