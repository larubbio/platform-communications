package org.motechproject.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.BookmarkBuilder;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.CourseContentBuilder;
import org.motechproject.mtraining.builder.CourseProgressUpdater;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
import org.motechproject.mtraining.builder.TestBookmarkBuilder;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.exception.CourseNotFoundException;
import org.motechproject.mtraining.exception.InvalidBookmarkException;
import org.motechproject.mtraining.repository.AllBookmarks;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.util.ISODateTimeUtil;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.lang.Integer.valueOf;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkServiceImplTest {

    @Mock
    private AllBookmarks allBookmarks;
    @Mock
    private CourseService courseService;
    @Mock
    private CourseProgressUpdater courseProgressUpdater;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private BookmarkServiceImpl bookmarkService;
    private MessageContentBuilder messageContentBuilder;
    private ChapterContentBuilder chapterContentBuilder;
    private ModuleContentBuilder moduleContentBuilder;
    private CourseContentBuilder courseContentBuilder;
    private BookmarkBuilder bookmarkBuilder;


    @Before
    public void setUp() throws Exception {
        messageContentBuilder = new MessageContentBuilder();
        chapterContentBuilder = new ChapterContentBuilder();
        moduleContentBuilder = new ModuleContentBuilder();
        courseContentBuilder = new CourseContentBuilder();
        bookmarkBuilder = new BookmarkBuilder();
        bookmarkService = new BookmarkServiceImpl(allBookmarks, courseService, bookmarkBuilder);
    }

    @Test
    public void shouldReturnBookmarkForTheGivenCallerWhenBookmarkExists() {
        String externalId = "someId";
        UUID contentId = randomUUID();
        CourseDto courseDto = new CourseContentBuilder().withContentId(contentId).withVersion(1).buildCourseDTO();
        Bookmark expectedBookmark = new TestBookmarkBuilder().withExternalId(externalId).withCourse(contentId).withoutQuiz().build();

        when(allBookmarks.findBy(externalId)).thenReturn(expectedBookmark);
        when(courseService.getLatestPublishedCourse(contentId)).thenReturn(courseDto);

        BookmarkDto actualBookmark = bookmarkService.getBookmark(externalId);

        verify(allBookmarks).findBy(externalId);
        assertEquals(expectedBookmark.getExternalId(), actualBookmark.getExternalId());
        assertEquals(expectedBookmark.getCourse().getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(expectedBookmark.getModule().getContentId(), actualBookmark.getModule().getContentId());
        assertEquals(expectedBookmark.getChapter().getContentId(), actualBookmark.getChapter().getContentId());
    }


    @Test
    public void shouldReturnNullBookmarkForTheGivenCallerWhenBookmarkDoesNotExists() {
        String externalId = "someUnknownId";

        when(allBookmarks.findBy("externalId")).thenReturn(null);
        BookmarkDto actualBookmark = bookmarkService.getBookmark(externalId);

        verify(allBookmarks).findBy(externalId);
        assertNull(actualBookmark);
    }

    @Test
    public void shouldReturnNullBookmarkForTheGivenCallerWhenCourseInTheStoredBookmarkDoesNotExists() {
        String externalId = "someId";
        Bookmark bookmark = mock(Bookmark.class);
        UUID invalidCourseId = randomUUID();

        when(bookmark.getCourse()).thenReturn(new ContentIdentifier(invalidCourseId, 1));
        when(courseService.getLatestPublishedCourse(invalidCourseId)).thenReturn(null);
        BookmarkDto actualBookmark = bookmarkService.getBookmark(externalId);

        verify(allBookmarks).findBy(externalId);
        assertNull(actualBookmark);
    }

    @Test
    public void shouldGetInitialBookmarkWithMessageIfFirstChapterContainsAMessage() {
        ContentIdentifierDto courseContentDto = new ContentIdentifierDto(randomUUID(), 1);
        ContentDto courseContent = courseContentBuilder.withContentId(randomUUID()).withVersion(1).buildCourseDTO();
        ContentDto moduleContent = moduleContentBuilder.withContentId(randomUUID()).withVersion(2).buildModuleDTO();
        ContentDto chapterContent = chapterContentBuilder.withContentId(randomUUID()).withVersion(3).buildChapterDTO();
        List<MessageDto> messageDtos = asList(messageContentBuilder.buildMessageDTO());
        List<ChapterDto> chapterDtos = asList(chapterContentBuilder.withContentId(chapterContent.getContentId()).withVersion(chapterContent.getVersion()).withMessageDTOs(messageDtos).buildChapterDTO());
        List<ModuleDto> moduleDtos = asList(moduleContentBuilder.withContentId(moduleContent.getContentId()).withVersion(moduleContent.getVersion()).withChapterDTOs(chapterDtos).buildModuleDTO());
        CourseDto courseDto = courseContentBuilder.withContentId(courseContent.getContentId()).withVersion(courseContent.getVersion()).withModuleDtos(moduleDtos).buildCourseDTO();

        when(courseService.getLatestPublishedCourse(courseContentDto.getContentId())).thenReturn(courseDto);

        BookmarkDto initialBookmark = bookmarkService.getInitialBookmark("someId", courseContentDto);

        verify(courseService).getLatestPublishedCourse(courseContentDto.getContentId());
        verifyZeroInteractions(allBookmarks);
        assertEquals("someId", initialBookmark.getExternalId());
        assertEquals(valueOf(1), initialBookmark.getCourse().getVersion());
        assertEquals(valueOf(2), initialBookmark.getModule().getVersion());
        assertEquals(valueOf(3), initialBookmark.getChapter().getVersion());
        assertNull(initialBookmark.getQuiz());
        assertNotNull(initialBookmark.getMessage());
    }

    @Test
    public void shouldGetInitialBookmarkWithQuizIfFirstChapterDoesNotContainAMessage() {
        ContentIdentifierDto courseContentDto = new ContentIdentifierDto(randomUUID(), 1);
        ContentDto courseContent = courseContentBuilder.withContentId(randomUUID()).withVersion(1).buildCourseDTO();
        ContentDto moduleContent = moduleContentBuilder.withContentId(randomUUID()).withVersion(2).buildModuleDTO();
        ContentDto chapterContent = chapterContentBuilder.withContentId(randomUUID()).withVersion(3).buildChapterDTO();
        QuizDto quizContent = new QuizDto();
        List<ChapterDto> chapterDtos = asList(chapterContentBuilder.withContentId(chapterContent.getContentId()).withVersion(chapterContent.getVersion()).withQuizDTOs(quizContent).buildChapterDTO());
        List<ModuleDto> moduleDtos = asList(moduleContentBuilder.withContentId(moduleContent.getContentId()).withVersion(moduleContent.getVersion()).withChapterDTOs(chapterDtos).buildModuleDTO());
        CourseDto courseDto = courseContentBuilder.withContentId(courseContent.getContentId()).withVersion(courseContent.getVersion()).withModuleDtos(moduleDtos).buildCourseDTO();

        when(courseService.getLatestPublishedCourse(courseContentDto.getContentId())).thenReturn(courseDto);

        BookmarkDto initialBookmark = bookmarkService.getInitialBookmark("someId", courseContentDto);

        verify(courseService).getLatestPublishedCourse(courseContentDto.getContentId());
        verifyZeroInteractions(allBookmarks);
        assertEquals("someId", initialBookmark.getExternalId());
        assertEquals(valueOf(1), initialBookmark.getCourse().getVersion());
        assertEquals(valueOf(2), initialBookmark.getModule().getVersion());
        assertEquals(valueOf(3), initialBookmark.getChapter().getVersion());
        assertNull(initialBookmark.getMessage());
        assertNotNull(initialBookmark.getQuiz());
    }

    @Test
    public void shouldUpdateBookmarkForAGivenCallerId() {
        UUID contentId = randomUUID();
        ContentIdentifierDto contentIdentifier = new ContentIdentifierDto(contentId, 1);
        DateTime now = DateTime.now();
        DateTime updatedOneHourAgo = now.minusHours(1);
        BookmarkDto bookmarkDto = new BookmarkDto("externalId", contentIdentifier,
                contentIdentifier, contentIdentifier, contentIdentifier, null, now);

        Bookmark bookmark = new TestBookmarkBuilder().withExternalId("externalId").modifiedOn(updatedOneHourAgo).withQuiz(contentId).build();

        when(allBookmarks.findBy(bookmarkDto.getExternalId())).thenReturn(bookmark);

        bookmarkService.addOrUpdate(bookmarkDto);

        verify(allBookmarks).findBy(bookmarkDto.getExternalId());
        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).update(bookmarkArgumentCaptor.capture());
        Bookmark bookmarkSaved = bookmarkArgumentCaptor.getValue();
        assertNotNull(bookmarkSaved.getModule());
    }

    @Test(expected = InvalidBookmarkException.class)
    public void shouldThrowInvalidBookmarkExceptionOnUpdateOfBookmarkWithBothMessageAndQuiz() {
        UUID contentId = randomUUID();
        ContentIdentifierDto contentIdentifier = new ContentIdentifierDto(contentId, 1);
        DateTime now = DateTime.now();
        BookmarkDto bookmarkDto = new BookmarkDto("externalId", contentIdentifier,
                contentIdentifier, contentIdentifier, contentIdentifier, contentIdentifier, now);

        bookmarkService.addOrUpdate(bookmarkDto);

    }

    @Test
    public void shouldNotUpdateBookmarkIfAMoreRecentBookmarkExists() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        DateTime someSecondsBeforeNow = now.minusSeconds(100);
        String externalId = "9886557745l";
        BookmarkDto oldBookmark = new TestBookmarkBuilder().withExternalId(externalId).modifiedOn(someSecondsBeforeNow).buildDtoWithMessage();

        Bookmark existingBookmarkInDb = new TestBookmarkBuilder().withExternalId(externalId).modifiedOn(now).build();

        when(allBookmarks.findBy("externalId")).thenReturn(existingBookmarkInDb);

        bookmarkService.addOrUpdate(oldBookmark);

        verify(allBookmarks, never()).update(any(Bookmark.class));
    }

    @Test
    public void shouldAddBookmarkIfBookmarkDoesNotAlreadyExists() {
        DateTime oneSecondAgo = ISODateTimeUtil.nowInTimeZoneUTC().minusSeconds(1);
        String externalId = "9886557745l";
        BookmarkDto newBookmark = new TestBookmarkBuilder().withExternalId(externalId).modifiedOn(oneSecondAgo).buildDtoWithMessage();

        when(allBookmarks.findBy(externalId)).thenReturn(null);

        bookmarkService.addOrUpdate(newBookmark);

        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).add(bookmarkArgumentCaptor.capture());

        Bookmark addedBookmark = bookmarkArgumentCaptor.getValue();
        assertThat(addedBookmark.getExternalId(), Is.is(newBookmark.getExternalId()));
        assertThat(addedBookmark.getCourse().getContentId(), Is.is(newBookmark.getCourse().getContentId()));
        assertThat(addedBookmark.getCourse().getVersion(), Is.is(newBookmark.getCourse().getVersion()));
        assertThat(addedBookmark.getModule().getContentId(), Is.is(newBookmark.getModule().getContentId()));
        assertThat(addedBookmark.getModule().getVersion(), Is.is(newBookmark.getModule().getVersion()));
        assertThat(addedBookmark.getChapter().getContentId(), Is.is(newBookmark.getChapter().getContentId()));
        assertThat(addedBookmark.getChapter().getVersion(), Is.is(newBookmark.getChapter().getVersion()));
        assertThat(addedBookmark.getMessage().getContentId(), Is.is(newBookmark.getMessage().getContentId()));
        assertThat(addedBookmark.getMessage().getVersion(), Is.is(newBookmark.getMessage().getVersion()));
        assertThat(addedBookmark.getDateModified(), Is.is(oneSecondAgo));
    }

    @Test
    public void shouldResetBookmarkToFirstMessageOfExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentIdentifier = randomUUID();
        ContentIdentifier quizContentIdentifier = new ContentIdentifier(contentIdentifier, 1);
        UUID messageContentIdentifier = randomUUID();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentIdentifier, 1);
        Bookmark bookmark = new Bookmark("external1", new ContentIdentifier(contentIdentifier, 1), new ContentIdentifier(contentIdentifier, 1),
                new ContentIdentifier(contentIdentifier, 1), null, quizContentIdentifier, now);
        MessageDto messageDto1 = new MessageDto(messageContentIdentifier, 1, true, "message1", "", "someFile", "");
        MessageDto messageDto2 = new MessageDto(randomUUID(), 1, true, "message2", "", "someFile", "");
        ChapterDto chapterDto = new ChapterDto(contentIdentifier, 1, true, "chapter1", "", "externalId", "", newArrayList(messageDto1, messageDto2), new QuizDto());
        ModuleDto moduleDto = new ModuleDto(contentIdentifier, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto));
        CourseDto courseDto = new CourseDto(contentIdentifier, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        bookmarkService.setBookmarkToFirstActiveContentOfAChapter("external1", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto);

        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).update(bookmarkArgumentCaptor.capture());
        Bookmark addedBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals("external1", addedBookmark.getExternalId());
        assertEquals(messageContentIdentifier, addedBookmark.getMessage().getContentId());
        Assert.assertNull(addedBookmark.getQuiz());
    }

    @Test
    public void shouldResetBookmarkToQuizOfChapterIfNoMessageFoundOfExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentIdentifier = randomUUID();
        ContentIdentifier quizContentIdentifier = new ContentIdentifier(contentIdentifier, 1);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentIdentifier, 1);
        Bookmark bookmark = new Bookmark("external1", new ContentIdentifier(contentIdentifier, 1), new ContentIdentifier(contentIdentifier, 1),
                new ContentIdentifier(contentIdentifier, 1), null, quizContentIdentifier, now);
        QuizDto quiz = new QuizDto(contentIdentifier, 1, true, "quiz", "externalId", Collections.EMPTY_LIST, 2, 100.0, "");
        ChapterDto chapterDto = new ChapterDto(contentIdentifier, 1, true, "chapter1", "", "externalId", "", null, quiz);
        ModuleDto moduleDto = new ModuleDto(contentIdentifier, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto));
        CourseDto courseDto = new CourseDto(contentIdentifier, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        bookmarkService.setBookmarkToFirstActiveContentOfAChapter("external1", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto);

        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).update(bookmarkArgumentCaptor.capture());
        Bookmark addedBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals("external1", addedBookmark.getExternalId());
        assertEquals(contentIdentifier, addedBookmark.getQuiz().getContentId());
        assertNull(addedBookmark.getMessage());
        assertNotNull(addedBookmark.getQuiz());
    }

    @Test
    public void shouldSetBookmarkToFirstActiveMessageOfNextChapterForExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentId = randomUUID();
        UUID contentId2 = randomUUID();
        ContentIdentifier contentIdentifier = new ContentIdentifier(contentId, 1);
        UUID messageContentIdentifier = randomUUID();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        Bookmark bookmark = new Bookmark("external1", contentIdentifier, contentIdentifier,
                contentIdentifier, null, contentIdentifier, now);
        MessageDto messageDto1 = new MessageDto(messageContentIdentifier, 1, false, "message1", "", "someFile", "");
        MessageDto messageDto2 = new MessageDto(contentId2, 1, true, "message2", "", "someFile", "");
        ChapterDto chapterDto = new ChapterDto(contentId, 1, true, "chapter1", "", "externalId", "", Collections.EMPTY_LIST, new QuizDto());
        ChapterDto chapterDto2 = new ChapterDto(contentId2, 1, true, "chapter1", "", "externalId", "", newArrayList(messageDto1, messageDto2), new QuizDto());
        ModuleDto moduleDto = new ModuleDto(contentId, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto, chapterDto2));
        CourseDto courseDto = new CourseDto(contentId, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        BookmarkDto nextBookmark = bookmarkService.getNextBookmark("external1", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto);

        assertEquals("external1", nextBookmark.getExternalId());
        assertEquals(contentId, nextBookmark.getModule().getContentId());
        assertEquals(contentId2, nextBookmark.getChapter().getContentId());
        assertEquals(contentId2, nextBookmark.getMessage().getContentId());
        Assert.assertNull(nextBookmark.getQuiz());
    }

    @Test
    public void shouldSetBookmarkToFirstActiveMessageOfNextChapterWithinGivenModuleForExternalId() {
        MessageDto msg001 = new MessageDto(randomUUID(), 1, true, "message1", "", "someFile", "");
        MessageDto msg002 = new MessageDto(randomUUID(), 1, false, "message2", "", "someFile", "");
        MessageDto inactiveMsg003 = new MessageDto(randomUUID(), 1, false, "message3", "", "someFile", "");
        MessageDto msg004 = new MessageDto(randomUUID(), 1, true, "message4", "", "someFile", "");

        String externalId = "externalId";
        ChapterDto ch001 = new ChapterDto(randomUUID(), 1, true, "chapter1", "", externalId, "", newArrayList(msg001), new QuizDto());
        ChapterDto inactiveCh002 = new ChapterDto(randomUUID(), 1, false, "chapter2", "", externalId, "", newArrayList(msg002), new QuizDto());
        ChapterDto ch003 = new ChapterDto(randomUUID(), 1, true, "chapter3", "", externalId, "", newArrayList(inactiveMsg003, msg004), new QuizDto());

        ModuleDto mod001 = new ModuleDto(randomUUID(), 1, true, "module1", "", externalId, "", newArrayList(ch001, inactiveCh002, ch003));

        CourseDto cs001 = new CourseDto(randomUUID(), 1, true, "course1", "", externalId, "", newArrayList(mod001));

        ContentIdentifierDto course = new ContentIdentifierDto(cs001.getContentId(), cs001.getVersion());
        ContentIdentifierDto module = new ContentIdentifierDto(mod001.getContentId(), mod001.getVersion());
        ContentIdentifierDto chapter = new ContentIdentifierDto(ch001.getContentId(), ch001.getVersion());


        when(courseService.getCourse(course)).thenReturn(cs001);

        BookmarkDto nextBookmark = bookmarkService.getNextBookmark(externalId, course, module, chapter);

        assertEquals(externalId, nextBookmark.getExternalId());
        assertEquals(mod001.getContentId(), nextBookmark.getModule().getContentId());
        assertEquals(ch003.getContentId(), nextBookmark.getChapter().getContentId());
        assertEquals(msg004.getContentId(), nextBookmark.getMessage().getContentId());
        Assert.assertNull(nextBookmark.getQuiz());
    }

    @Test
    public void shouldSetBookmarkToFirstActiveMessageOfNextModuleIfNextChapterOfCurrentModuleNotFoundForExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentId = randomUUID();
        UUID contentId2 = randomUUID();
        ContentIdentifier contentIdentifier = new ContentIdentifier(contentId, 1);
        UUID messageContentIdentifier = randomUUID();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        Bookmark bookmark = new Bookmark("external1", contentIdentifier, contentIdentifier,
                contentIdentifier, null, contentIdentifier, now);
        MessageDto messageDto1 = new MessageDto(messageContentIdentifier, 1, false, "message1", "", "someFile", "");
        MessageDto messageDto2 = new MessageDto(contentId2, 1, true, "message2", "", "someFile", "");
        ChapterDto chapterDto = new ChapterDto(contentId, 1, true, "chapter1", "", "externalId", "", newArrayList(messageDto1, messageDto2), new QuizDto());
        ChapterDto chapterDto2 = new ChapterDto(contentId2, 1, true, "chapter2", "", "externalId", "", newArrayList(messageDto1), null);
        ModuleDto moduleDto = new ModuleDto(contentId, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto(contentId2, 1, true, "module2", "", "externalId", "", newArrayList(chapterDto, chapterDto2));
        CourseDto courseDto = new CourseDto(contentId, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto, moduleDto2));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        BookmarkDto nextBookmark = bookmarkService.getNextBookmark("external1", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto);

        assertEquals("external1", nextBookmark.getExternalId());
        assertEquals(contentId2, nextBookmark.getModule().getContentId());
        assertEquals(contentId, nextBookmark.getChapter().getContentId());
        assertEquals(contentId2, nextBookmark.getMessage().getContentId());
        Assert.assertNull(nextBookmark.getQuiz());
    }

    @Test
    public void shouldResetBookmarkToQuizOfChapterForExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentIdentifier = randomUUID();
        ContentIdentifier quizContentIdentifier = new ContentIdentifier(contentIdentifier, 1);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentIdentifier, 1);
        Bookmark bookmark = new Bookmark("external1", new ContentIdentifier(contentIdentifier, 1), new ContentIdentifier(contentIdentifier, 1),
                new ContentIdentifier(contentIdentifier, 1), null, quizContentIdentifier, now);
        QuizDto quiz = new QuizDto(contentIdentifier, 1, true, "quiz", "externalId", Collections.EMPTY_LIST, 2, 100.0, "");
        ChapterDto chapterDto = new ChapterDto(contentIdentifier, 1, true, "chapter1", "", "externalId", "", null, quiz);
        ModuleDto moduleDto = new ModuleDto(contentIdentifier, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto));
        CourseDto courseDto = new CourseDto(contentIdentifier, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        BookmarkDto expectedBookmark = bookmarkService.getBookmarkForQuizOfAChapter("external1", contentIdentifierDto, contentIdentifierDto, contentIdentifierDto);

        assertEquals("external1", expectedBookmark.getExternalId());
        assertEquals(contentIdentifier, expectedBookmark.getQuiz().getContentId());
        assertNull(expectedBookmark.getMessage());
        assertNotNull(expectedBookmark.getQuiz());
    }

    @Test
    public void shouldSetBookmarkToLastActiveContentOfACourseExternalId() {
        DateTime now = ISODateTimeUtil.nowInTimeZoneUTC();
        UUID contentIdentifier = randomUUID();
        ContentIdentifier quizContentIdentifier = new ContentIdentifier(contentIdentifier, 1);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentIdentifier, 1);
        Bookmark bookmark = new Bookmark("external1", new ContentIdentifier(contentIdentifier, 1), new ContentIdentifier(contentIdentifier, 1),
                new ContentIdentifier(contentIdentifier, 1), null, quizContentIdentifier, now);
        QuizDto quiz = new QuizDto(contentIdentifier, 1, true, "quiz", "externalId", Collections.EMPTY_LIST, 2, 100.0, "");
        ChapterDto chapterDto = new ChapterDto(contentIdentifier, 1, true, "chapter1", "", "externalId", "", null, quiz);
        ModuleDto moduleDto = new ModuleDto(contentIdentifier, 1, true, "module1", "", "externalId", "", newArrayList(chapterDto));
        CourseDto courseDto = new CourseDto(contentIdentifier, 1, true, "course1", "", "externalId", "", newArrayList(moduleDto));
        when(allBookmarks.findBy("external1")).thenReturn(bookmark);
        when(courseService.getCourse(contentIdentifierDto)).thenReturn(courseDto);

        bookmarkService.setBookmarkToLastActiveContentOfACourse("external1", contentIdentifierDto);

        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).update(bookmarkArgumentCaptor.capture());
        Bookmark addedBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals("external1", addedBookmark.getExternalId());
        assertEquals(contentIdentifier, addedBookmark.getQuiz().getContentId());
        assertNull(addedBookmark.getMessage());
        assertNotNull(addedBookmark.getQuiz());
    }

    @Test(expected = CourseNotFoundException.class)
    public void shouldThrowCourseNotFoundExceptionIfNoLatestPublishedCourseFound() {
        String externalId = "external1";
        UUID contentId = randomUUID();
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(contentId, 1);
        when(courseService.getLatestPublishedCourse(courseIdentifier.getContentId())).thenReturn(null);
        bookmarkService.getInitialBookmark(externalId, courseIdentifier);
    }

    private static UUID randomUUID() {
        return UUID.randomUUID();
    }


}
