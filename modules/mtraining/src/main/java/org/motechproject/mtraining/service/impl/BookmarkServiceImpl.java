package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllBookmarks;
import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for {@link BookmarkService}.
 * Given an external Id, it finds the current {@link org.motechproject.mtraining.domain.Bookmark} and
 * also constructs a {@link org.motechproject.mtraining.domain.Bookmark} given the course details
 */

@Service("bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {

    private AllBookmarks allBookmarks;
    private CourseService courseService;

    @Autowired
    public BookmarkServiceImpl(AllBookmarks allBookmarks, CourseService courseService) {
        this.allBookmarks = allBookmarks;
        this.courseService = courseService;
    }

    @Override
    public BookmarkDto getBookmark(String externalId) {
        Bookmark bookmark = allBookmarks.findBy(externalId);
        return bookmark == null ? null : new BookmarkDto(bookmark.getExternalId(), bookmark.getCourse(), bookmark.getModule(), bookmark.getChapter(), bookmark.getMessage());
    }

    @Override
    public void addBookmark(String externalId, ContentIdentifierDto courseIdentifier) {
        CourseDto course = courseService.getCourse(courseIdentifier);
        ModuleDto moduleDto = course.getModules().get(0);
        ChapterDto chapterDto = moduleDto.getChapters().get(0);
        MessageDto messageDto = chapterDto.getMessages().get(0);
        Bookmark bookmark = new Bookmark(externalId, createContentIdentifier(course.getCourseIdentifier()), createContentIdentifier(moduleDto.getModuleIdentifier()), createContentIdentifier(chapterDto.getChapterIdentifier()), createContentIdentifier(messageDto.getMessageIdentifier()));
        allBookmarks.add(bookmark);
    }

    @Override
    public void update(BookmarkDto bookmark) {
        Bookmark bookmarkFromDb = allBookmarks.findBy(bookmark.getExternalId());
        bookmarkFromDb.update(bookmark.getCourse(), bookmark.getModule(), bookmark.getChapter(), bookmark.getMessage());
        allBookmarks.update(bookmarkFromDb);
    }

    private ContentIdentifier createContentIdentifier(ContentIdentifierDto contentIdentifier) {
        return new ContentIdentifier(contentIdentifier.getContentId(), contentIdentifier.getVersion());
    }
}
