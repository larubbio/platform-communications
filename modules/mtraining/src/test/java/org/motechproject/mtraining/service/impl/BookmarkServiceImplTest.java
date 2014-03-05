package org.motechproject.mtraining.service.impl;

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
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllBookmarks;
import org.motechproject.mtraining.service.CourseService;

import java.util.Arrays;
import java.util.UUID;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class BookmarkServiceImplTest {

    @Mock
    private AllBookmarks allBookmarks;
    @Mock
    private CourseService courseService;

    private BookmarkServiceImpl bookmarkService;
    private ContentIdentifierDto messageIdentifier;

    @Before
    public void setUp() throws Exception {
        bookmarkService = new BookmarkServiceImpl(allBookmarks, courseService);
        messageIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldReturnBookmarkForTheGivenCaller() {
        String externalId = "someId";
        Bookmark expectedBookmark = mock(Bookmark.class);
        when(allBookmarks.findBy(externalId)).thenReturn(expectedBookmark);

        BookmarkDto actualBookmark = bookmarkService.getBookmark(externalId);

        verify(allBookmarks).findBy(externalId);
        assertEquals(expectedBookmark.getExternalId(), actualBookmark.getExternalId());
        assertEquals(expectedBookmark.getCourse(), actualBookmark.getCourse());
        assertEquals(expectedBookmark.getModule(), actualBookmark.getModule());
        assertEquals(expectedBookmark.getChapter(), actualBookmark.getChapter());
    }

    @Test
    public void shouldAddBookmark() {
        ContentIdentifierDto courseContentDto = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto courseContent = new ContentIdentifierDto(UUID.randomUUID(), 1);
        ContentIdentifierDto moduleContent = new ContentIdentifierDto(UUID.randomUUID(), 2);
        ContentIdentifierDto chapterContent = new ContentIdentifierDto(UUID.randomUUID(), 3);
        CourseDto courseDto = new CourseDto("course1", "some description", courseContent,
                Arrays.asList(new ModuleDto("module1", "module", moduleContent,
                        Arrays.asList(new ChapterDto("chapter1", "chapter", chapterContent,
                                Arrays.asList(new MessageDto("message1", "externalId", "message description", messageIdentifier)))))));
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
        BookmarkDto bookmarkDto = new BookmarkDto("externalId", new ContentIdentifier(), new ContentIdentifier(), null, null);
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
