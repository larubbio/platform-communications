package org.motechproject.mtraining.builder;

import org.junit.Before;
import org.motechproject.mtraining.service.CourseService;

import static org.mockito.Mockito.mock;

public class BookmarkCourseCompletionTest {

    private CourseProgressUpdater courseProgressUpdater;
    private CourseService courseService;

    @Before
    public void before() {
        courseService = mock(CourseService.class);
        BookmarkBuilder bookmarkBuilder = new BookmarkBuilder();
        BookmarkChapterUpdater bookmarkChapterUpdater = new BookmarkChapterUpdater(bookmarkBuilder, new BookmarkMessageUpdater(bookmarkBuilder), new BookmarkQuizUpdater(bookmarkBuilder));
        BookmarkModuleUpdater bookmarkModuleUpdater = new BookmarkModuleUpdater(bookmarkBuilder, bookmarkChapterUpdater);
        courseProgressUpdater = new CourseProgressUpdater(courseService, bookmarkModuleUpdater, bookmarkBuilder);
    }


//    @Test
//    public void shouldUpdateBookmarkWithCourseCompletionStatusIfBookmarkedMessageIsInactivatedAndNoOtherChapterOrModuleHasAnyActiveMessage() {
//        ContentIdentifier course = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier module = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier chapter = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier message = new ContentIdentifier(UUID.randomUUID(), 1);
//
//
//        MessageDto message01 = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "aud001", "desc1", "auth");
//        MessageDto lastActiveMessage = new MessageDto(UUID.randomUUID(), 1, true, "ms004", "aud004", "desc4", "auth");
//        MessageDto bookmarkedMessageNowInactive = new MessageDto(message.getContentId(), 2, false, "ms002", "aud002", "desc2", "auth");
//        MessageDto anotherInactiveMessage = new MessageDto(UUID.randomUUID(), 2, false, "ms003", "aud003", "desc3", "auth");
//
//
//        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "auth", asList(message01, lastActiveMessage, bookmarkedMessageNowInactive, anotherInactiveMessage), null);
//
//
//        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "auth", asList(chapter01));
//
//
//        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
//                .withVersion(4)
//                .withModuleDtos(asList(module01))
//                .buildCourseDTO();
//
//        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);
//
//        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
//        Bookmark bookmark = new Bookmark("r001", course, module, chapter, message, null, now);
//
//        BookmarkDto bookmarkDto = courseProgressUpdater.update(bookmark);
//
//        assertThat(bookmarkDto.getCourseStatus(), Is.is(CourseStatus.COMPLETED));
//
//        assertThat(bookmarkDto.getCourse().getContentId(), Is.is(course01.getContentId()));
//        assertThat(bookmarkDto.getCourse().getVersion(), Is.is(course01.getVersion()));
//
//        assertThat(bookmarkDto.getMessage().getContentId(), Is.is(lastActiveMessage.getContentId()));
//        assertThat(bookmarkDto.getMessage().getVersion(), Is.is(lastActiveMessage.getVersion()));
//
//    }
//
//    @Test
//    public void shouldUpdateBookmarkWithCourseCompletionStatusIfBookmarkedQuizIsInactivatedAndNoOtherChapterOrModuleHasAnyActiveMessage() {
//        ContentIdentifier course = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier module = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier chapter = new ContentIdentifier(UUID.randomUUID(), 1);
//        ContentIdentifier quiz = new ContentIdentifier(UUID.randomUUID(), 1);
//
//
//        MessageDto lastActiveMessage = new MessageDto(UUID.randomUUID(), 1, true, "ms004", "aud004", "desc4", "auth");
//        MessageDto anotherInactiveMessage = new MessageDto(UUID.randomUUID(), 2, false, "ms003", "aud003", "desc3", "auth");
//        MessageDto messageInSecondChapter = new MessageDto(UUID.randomUUID(), 1, false, "ms001", "aud001", "desc1", "auth");
//
//
//        QuizDto bookmarkedQuizNowInactive = new QuizDto(quiz.getContentId(), 2, false, "ms002", Collections.<QuestionDto>emptyList(), 0, 100.0, "auth");
//
//
//        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "auth", asList(messageInSecondChapter, lastActiveMessage, anotherInactiveMessage), bookmarkedQuizNowInactive);
//
//        ChapterDto chapter02 = new ChapterDto(UUID.randomUUID(), 4, false, "ch002", "desc", "auth", asList(messageInSecondChapter), null);
//
//
//        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "auth", asList(chapter01));
//        ModuleDto module02 = new ModuleDto(UUID.randomUUID(), 2, false, "mod002", "des", "auth", asList(chapter02));
//
//
//        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
//                .withVersion(4)
//                .withModuleDtos(asList(module01, module02))
//                .buildCourseDTO();
//
//        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);
//
//        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
//        Bookmark bookmark = new Bookmark("r001", course, module, chapter, null, quiz, now, CourseStatus.ONGOING);
//
//        BookmarkDto bookmarkDto = courseProgressUpdater.update(bookmark);
//
//        assertThat(bookmarkDto.getCourseStatus(), Is.is(CourseStatus.COMPLETED));
//
//        assertThat(bookmarkDto.getCourse().getContentId(), Is.is(course01.getContentId()));
//        assertThat(bookmarkDto.getCourse().getVersion(), Is.is(course01.getVersion()));
//
//        assertThat(bookmarkDto.getMessage().getContentId(), Is.is(lastActiveMessage.getContentId()));
//        assertThat(bookmarkDto.getMessage().getVersion(), Is.is(lastActiveMessage.getVersion()));
//
//    }

}
