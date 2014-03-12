package org.motechproject.mtraining.service.impl;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllBookmarks;
import org.motechproject.mtraining.service.CourseService;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static java.lang.Integer.valueOf;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkServiceImplTest {

    @Mock
    private AllBookmarks allBookmarks;
    @Mock
    private CourseService courseService;

    private BookmarkServiceImpl bookmarkService;

    @Before
    public void setUp() throws Exception {
        bookmarkService = new BookmarkServiceImpl(allBookmarks, courseService);
    }

    @Test
    public void shouldReturnBookmarkForTheGivenCaller() {
        String externalId = "someId";
        ContentIdentifier content = new ContentIdentifier(UUID.randomUUID(), 1);
        Bookmark expectedBookmark = new Bookmark(externalId, content, content, content, content);
        when(allBookmarks.findBy(externalId)).thenReturn(expectedBookmark);

        BookmarkDto actualBookmark = bookmarkService.getBookmark(externalId);

        verify(allBookmarks).findBy(externalId);
        assertEquals(expectedBookmark.getExternalId(), actualBookmark.getExternalId());
        assertEquals(expectedBookmark.getCourse().getContentId(), actualBookmark.getCourse().getContentId());
        assertEquals(expectedBookmark.getModule().getContentId(), actualBookmark.getModule().getContentId());
        assertEquals(expectedBookmark.getChapter().getContentId(), actualBookmark.getChapter().getContentId());
    }

    @Test
    public void shouldAddBookmark() {
        ContentIdentifierDto courseContentDto = new ContentIdentifierDto(randomUUID(), 1);
        ContentDto courseContent = new CourseDto(randomUUID(), 1, true, "name", "desc", Collections.EMPTY_LIST);
        ContentDto moduleContent = new ModuleDto(randomUUID(), 2, true, "name", "desc", Collections.EMPTY_LIST);
        ContentDto chapterContent = new ChapterDto(randomUUID(), 3, true, "name", "desc", Collections.EMPTY_LIST);
        CourseDto courseDto = new CourseDto(courseContent.getContentId(), courseContent.getVersion(), true, "course1", "some description",
                Arrays.asList(new ModuleDto(moduleContent.getContentId(), moduleContent.getVersion(), true, "module1", "module",
                        Arrays.asList(new ChapterDto(chapterContent.getContentId(), chapterContent.getVersion(), true, "chapter1", "chapter",
                                Arrays.asList(new MessageDto(true, "message1", "externalId", "message description")))))));
        when(courseService.getCourse(courseContentDto)).thenReturn(courseDto);

        bookmarkService.addBookmark("someId", courseContentDto);

        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).add(bookmarkArgumentCaptor.capture());
        Bookmark actualBookmark = bookmarkArgumentCaptor.getValue();
        assertEquals("someId", actualBookmark.getExternalId());
        assertEquals(valueOf(1), actualBookmark.getCourse().getVersion());
        assertEquals(valueOf(2), actualBookmark.getModule().getVersion());
        assertEquals(valueOf(3), actualBookmark.getChapter().getVersion());
    }

    @Test
    public void shouldUpdateBookmarkForAGivenCallerId() {
        ContentIdentifierDto contentIdentifier = new ContentIdentifierDto(randomUUID(), 1);
        BookmarkDto bookmarkDto = new BookmarkDto("externalId", contentIdentifier,
                contentIdentifier, contentIdentifier, contentIdentifier, DateTime.now());
        Bookmark bookmark = new Bookmark("externalId", new ContentIdentifier(), null, null, null);
        when(allBookmarks.findBy(bookmarkDto.getExternalId())).thenReturn(bookmark);

        bookmarkService.update(bookmarkDto);

        verify(allBookmarks).findBy(bookmarkDto.getExternalId());
        ArgumentCaptor<Bookmark> bookmarkArgumentCaptor = ArgumentCaptor.forClass(Bookmark.class);
        verify(allBookmarks).update(bookmarkArgumentCaptor.capture());
        Bookmark bookmarkSaved = bookmarkArgumentCaptor.getValue();
        assertNotNull(bookmarkSaved.getModule());
    }
}
