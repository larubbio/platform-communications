package org.motechproject.mtraining.builder;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.EnrolleeCourseProgressDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EnrolleeCourseProgressUpdaterTest {

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

    @Test
    public void shouldUpdateBookmarkContentVersions() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto message01 = new MessageDto(message.getContentId(), 2, true, "ms001", "desc1", "aud001", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "externalId", "auth", asList(message01), null);

        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));

        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.ONGOING));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getDateModified(), Is.is(bookmarkDto.getDateModified().toString()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getCourse().getContentId(), Is.is(course01.getContentId()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getCourse().getVersion(), Is.is(course01.getVersion()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getModule().getVersion(), Is.is(module01.getVersion()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getChapter().getVersion(), Is.is(chapter01.getVersion()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getMessage().getContentId(), Is.is(message01.getContentId()));
        assertThat(updatedEnrolleeCourseProgressDto.getBookmarkDto().getMessage().getVersion(), Is.is(message01.getVersion()));
    }


    @Test
    public void shouldUpdateBookmarkToMessageInNextActiveModuleIfBookmarkedMessageIsInactiveAndIsTheLastMessageInTheLastChapterOfModule() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto inactiveMessageDto = new MessageDto(message.getContentId(), message.getVersion(), false, "ms001", "desc", "aud001", "auth");
        MessageDto activeMessageDTo = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapterWithInactiveMessageReferencedInBookmark = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(inactiveMessageDto), null);
        ChapterDto chapterWithActiveMessage = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch002", "desc", "externalId", "auth", asList(activeMessageDTo), null);

        ModuleDto moduleReferencedInBookmark = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapterWithInactiveMessageReferencedInBookmark));

        ModuleDto anotherModule = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod003", "des", "externalId", "auth", asList(chapterWithActiveMessage));

        ModuleDto someInactiveModule = new ModuleDto(module.getContentId(), module.getVersion(), false, "mod002", "des", "externalId", "auth", Collections.<ChapterDto>emptyList());


        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(moduleReferencedInBookmark, someInactiveModule, anotherModule))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();

        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();
        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(anotherModule.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(anotherModule.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapterWithActiveMessage.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapterWithActiveMessage.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDTo.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDTo.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToMessageInNextActiveModuleIfBookamrkedModuleIsInactive() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto activeMessageDTo = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapterWithActiveMessage = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch002", "desc", "externalId", "auth", asList(activeMessageDTo), null);

        ModuleDto moduleReferencedInBookmark = new ModuleDto(module.getContentId(), module.getVersion(), false, "mod001", "des", "externalId", "auth", Collections.<ChapterDto>emptyList());

        ModuleDto anotherModule = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod002", "des", "externalId", "auth", asList(chapterWithActiveMessage));

        ModuleDto someInactiveModule = new ModuleDto(module.getContentId(), module.getVersion(), false, "mod003", "des", "externalId", "auth", Collections.<ChapterDto>emptyList());

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(moduleReferencedInBookmark, anotherModule, someInactiveModule))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();

        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();
        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(anotherModule.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(anotherModule.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapterWithActiveMessage.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapterWithActiveMessage.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDTo.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDTo.getVersion()));
    }

    @Test
    public void shouldReturnNullIfBookmarkedModuleIsNotFound() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto activeMessageDTo = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapterWithActiveMessage = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch002", "desc", "externalId", "auth", asList(activeMessageDTo), null);

        ModuleDto anotherModule = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod002", "des", "externalId", "auth", asList(chapterWithActiveMessage));

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(anotherModule))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        ContentIdentifierDto moduleNotPresentInCourse = new ContentIdentifierDto(UUID.randomUUID(), 2);
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, moduleNotPresentInCourse, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);
        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        assertNull(updatedEnrolleeCourseProgressDto);
    }

    @Test
    public void shouldUpdateBookmarkToFirstChapterInModuleIfBookmarkedChapterNotFound() {

        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto activeMessageDto1 = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageDto2 = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapter01 = new ChapterDto(UUID.randomUUID(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto1), null);
        ChapterDto chapter02 = new ChapterDto(UUID.randomUUID(), chapter.getVersion(), true, "ch002", "desc", "externalId", "auth", asList(activeMessageDto2), null);

        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01, chapter02));

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        ContentIdentifierDto bookmarkedChapterWhichDoesNotExist = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, bookmarkedChapterWhichDoesNotExist, message, null, now);

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);
        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto1.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto1.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToFirstMessageInChapterIfBookmarkedMessageNotFound() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto message01 = new MessageDto(UUID.randomUUID(), 1, false, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageInChapter = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "desc1", "aud001", "auth");
        MessageDto message03 = new MessageDto(UUID.randomUUID(), 1, false, "ms001", "desc1", "aud001", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "externalId", "auth", asList(message01, activeMessageInChapter, message03), null);

        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));

        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        ContentIdentifierDto messageNotInAnyChapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, messageNotInAnyChapter, null, now);

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.STARTED);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);
        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.STARTED));
        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(course01.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(course01.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageInChapter.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageInChapter.getVersion()));
    }

    @Test
    public void shouldMoveBookmarkToNextActiveMessageInTheChapterIfBookmarkedMessageIsHasBeenInactivated() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto bookmarkedMessageInactiveNow = new MessageDto(message.getContentId(), 1, false, "ms001", "desc1", "aud001", "auth");
        MessageDto anotherInactiveMessage = new MessageDto(UUID.randomUUID(), 1, false, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageInChapter = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "desc1", "aud001", "auth");
        MessageDto lastActiveMessage = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "desc1", "aud001", "auth");
        QuizDto quiz = new QuizDto(UUID.randomUUID(), 1, true, "quiz001", "externalId", Collections.<QuestionDto>emptyList(), 0, 100.0, "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "externalId", "auth",
                asList(bookmarkedMessageInactiveNow, activeMessageInChapter, anotherInactiveMessage, lastActiveMessage), quiz);

        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));

        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.STARTED);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.STARTED));

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(course01.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(course01.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageInChapter.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageInChapter.getVersion()));
    }

    @Test
    public void shouldMoveBookmarkToQuizInTheChapterIfBookmarkedMessageIsTheLastMessageInChapterAndHasBeenInactivated() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);

        MessageDto firstActiveMessageInChapter = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "desc1", "aud001", "auth");
        MessageDto bookmarkedMessageInactiveNow = new MessageDto(message.getContentId(), 1, false, "ms001", "desc1", "aud001", "auth");

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quiz = new QuizDto(UUID.randomUUID(), 1, true, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), 3, true, "ch001", "desc", "externalId", "auth",
                asList(firstActiveMessageInChapter, bookmarkedMessageInactiveNow), quiz);

        ModuleDto module01 = new ModuleDto(module.getContentId(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));

        CourseDto course01 = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(course01);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.STARTED);
        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);
        BookmarkDto updatedBookmarkDto = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.STARTED));

        assertThat(updatedBookmarkDto.getCourse().getContentId(), Is.is(course01.getContentId()));
        assertThat(updatedBookmarkDto.getCourse().getVersion(), Is.is(course01.getVersion()));

        assertThat(updatedBookmarkDto.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmarkDto.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmarkDto.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmarkDto.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertNull(updatedBookmarkDto.getMessage());

        assertThat(updatedBookmarkDto.getQuiz().getContentId(), Is.is(quiz.getContentId()));
        assertThat(updatedBookmarkDto.getQuiz().getVersion(), Is.is(quiz.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToFirstActiveMessageOfNextActiveChapterIfBookmarkedQuizIsInActivated() {

        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quizDto = new QuizDto(quiz.getContentId(), 1, false, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");

        MessageDto activeMessageDto1 = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageDto2 = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto1), quizDto);
        ChapterDto chapter02 = new ChapterDto(UUID.randomUUID(), chapter.getVersion(), true, "ch002", "desc", "externalId", "auth", asList(activeMessageDto2), null);

        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01, chapter02));

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, null, quiz, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);
        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter02.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter02.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto2.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto2.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToFirstActiveContentOfBookmarkedChapterIfBookmarkedDoesNotHaveAMessageOrQuiz() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);


        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quizDto = new QuizDto(quiz.getContentId(), 1, false, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");


        MessageDto activeMessageDto1 = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageDto2 = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");


        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto1, activeMessageDto2), quizDto);


        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01));


        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, null, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);


        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto1.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto1.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToFirstActiveContentOfBookmarkedModuleIfBookmarkedDoesNotHaveAChapter() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quizDto = new QuizDto(quiz.getContentId(), 1, false, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");

        MessageDto activeMessageDto1 = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageDto2 = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto1, activeMessageDto2), quizDto);

        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01));


        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, null, null, null, now);
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);

        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);

        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.ONGOING));

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto1.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto1.getVersion()));
    }

    @Test
    public void shouldUpdateBookmarkToFirstActiveContentOfBookmarkedCourseIfBookmarkedDoesNotHaveAModule() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quizDto = new QuizDto(quiz.getContentId(), 1, false, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");

        MessageDto activeMessageDto1 = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto activeMessageDto2 = new MessageDto(UUID.randomUUID(), 2, true, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto1, activeMessageDto2), quizDto);

        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01));

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, null, null, null, null, DateTime.now());
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);
        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);
        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.ONGOING));

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto1.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto1.getVersion()));
    }

    @Test
    public void shouldUpdateCOurseProgressWithCourseStatusAsCompletedIfNoActiveMessageFound() {
        ContentIdentifierDto course = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto module = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto chapter = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto message = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto quiz = new ContentIdentifierDto(UUID.randomUUID(), 1);

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto quizDto = new QuizDto(quiz.getContentId(), 1, false, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");

        MessageDto activeMessageDto = new MessageDto(UUID.randomUUID(), 2, true, "ms001", "desc1", "aud001", "auth");
        MessageDto inactiveMessageDto1 = new MessageDto(message.getContentId(), 2, false, "ms002", "desc2", "aud002", "auth");
        MessageDto inactiveMessageDto2 = new MessageDto(UUID.randomUUID(), 3, false, "ms002", "desc2", "aud002", "auth");

        ChapterDto chapter01 = new ChapterDto(chapter.getContentId(), chapter.getVersion(), true, "ch001", "desc", "externalId", "auth", asList(activeMessageDto, inactiveMessageDto1, inactiveMessageDto2), quizDto);
        ChapterDto chapter02 = new ChapterDto(UUID.randomUUID(), 2, false, "ch002", "desc", "externalId", "auth", asList(activeMessageDto, inactiveMessageDto1, inactiveMessageDto2), quizDto);

        ModuleDto module01 = new ModuleDto(module.getContentId(), module.getVersion(), true, "mod001", "des", "externalId", "auth", asList(chapter01, chapter02));

        CourseDto courseDto = new CourseContentBuilder().withContentId(course.getContentId())
                .withVersion(course.getVersion())
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        when(courseService.getLatestPublishedCourse(course.getContentId())).thenReturn(courseDto);

        BookmarkDto bookmarkDto = new BookmarkDto("r001", course, module, chapter, message, null, DateTime.now());

        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("r001", DateTime.now(), bookmarkDto, CourseStatus.ONGOING);
        EnrolleeCourseProgressDto updatedEnrolleeCourseProgressDto = courseProgressUpdater.update(enrolleeCourseProgressDto);
        BookmarkDto updatedBookmark = updatedEnrolleeCourseProgressDto.getBookmarkDto();

        assertThat(updatedEnrolleeCourseProgressDto.getCourseStatus(), Is.is(CourseStatus.COMPLETED));

        assertThat(updatedBookmark.getCourse().getContentId(), Is.is(courseDto.getContentId()));
        assertThat(updatedBookmark.getCourse().getVersion(), Is.is(courseDto.getVersion()));

        assertThat(updatedBookmark.getModule().getContentId(), Is.is(module01.getContentId()));
        assertThat(updatedBookmark.getModule().getVersion(), Is.is(module01.getVersion()));

        assertThat(updatedBookmark.getChapter().getContentId(), Is.is(chapter01.getContentId()));
        assertThat(updatedBookmark.getChapter().getVersion(), Is.is(chapter01.getVersion()));

        assertThat(updatedBookmark.getMessage().getContentId(), Is.is(activeMessageDto.getContentId()));
        assertThat(updatedBookmark.getMessage().getVersion(), Is.is(activeMessageDto.getVersion()));
    }
}
