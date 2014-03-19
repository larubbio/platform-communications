package org.motechproject.mtraining.dto;

import org.junit.Test;
import org.motechproject.mtraining.builder.TestBookmarkBuilder;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BookmarkDtoTest {

    @Test
    public void shouldConsiderBookmarkValidIfAllCourseContentPresent() {
        UUID someId = UUID.randomUUID();
        BookmarkDto validBookmarkDto = new TestBookmarkBuilder().withCourse(someId).withModule(someId).withChapter(someId).withMessage(someId).buildDto();
        assertTrue(validBookmarkDto.isValid());
    }

    @Test
    public void shouldConsiderBookmarkInValidIfCourseIsMissing() {
        UUID someId = UUID.randomUUID();
        BookmarkDto invalidBookmark = new TestBookmarkBuilder().withoutCourse().withModule(someId).withChapter(someId).withMessage(someId).buildDto();
        assertFalse(invalidBookmark.isValid());

        BookmarkDto bookmarkDtoWithInvalidCourse = new TestBookmarkBuilder().withCourse(null, 1).buildDtoWithMessage();
        assertFalse(bookmarkDtoWithInvalidCourse.isValid());
    }

    @Test
    public void shouldConsiderBookmarkInValidIfModuleIsMissing() {
        UUID someId = UUID.randomUUID();
        BookmarkDto invalidBookmark = new TestBookmarkBuilder().withCourse(someId).withoutModule().withChapter(someId).withMessage(someId).buildDto();
        assertFalse(invalidBookmark.isValid());
    }

    @Test
    public void shouldConsiderBookmarkInValidIfChapterIsMissing() {
        UUID someId = UUID.randomUUID();
        BookmarkDto invalidBookmark = new TestBookmarkBuilder().withCourse(someId).withModule(someId).withoutChapter().withMessage(someId).buildDto();
        assertFalse(invalidBookmark.isValid());
    }

    @Test
    public void shouldConsiderBookmarkInValidIfQuizAndMessageBothMissing() {
        UUID someId = UUID.randomUUID();
        BookmarkDto invalidBookmark = new TestBookmarkBuilder().withCourse(someId).withModule(someId).withChapter(someId).withoutMessage().withoutQuiz().buildDto();
        assertFalse(invalidBookmark.isValid());
    }

    @Test
    public void shouldConsiderBookmarkInValidIfQuizAndMessageBothPresent() {
        UUID someId = UUID.randomUUID();
        BookmarkDto invalidBookmark = new TestBookmarkBuilder().withCourse(someId).withModule(someId).withChapter(someId).withMessageAndQuiz(someId).buildDto();
        assertFalse(invalidBookmark.isValid());
    }

}
