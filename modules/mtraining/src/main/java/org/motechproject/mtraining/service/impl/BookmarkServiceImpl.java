package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.util.DateTimeUtil;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for {@link BookmarkService}.
 * Given an external Id, it finds the current {@link org.motechproject.mtraining.domain.Bookmark} and
 * also constructs a {@link org.motechproject.mtraining.domain.Bookmark} given the course details
 */

@Service("bookmarkService")
public class BookmarkServiceImpl implements BookmarkService {

    private static final Logger LOGGER = LoggerFactory.getLogger(BookmarkServiceImpl.class);

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
        return bookmark == null ? null : new BookmarkDto(bookmark.getExternalId(), createContentIdentifierDto(bookmark.getCourse()),
                createContentIdentifierDto(bookmark.getModule()), createContentIdentifierDto(bookmark.getChapter()),
                createContentIdentifierDto(bookmark.getMessage()), bookmark.getDateModified());
    }

    @Override
    public void addBookmark(String externalId, ContentIdentifierDto courseIdentifier) {
        LOGGER.info(String.format("Request for adding bookmark for externalId %s and courseId %s ", externalId, courseIdentifier.getContentId()));
        CourseDto course = courseService.getCourse(courseIdentifier);
        ModuleDto moduleDto = course.getModules().get(0);
        ChapterDto chapterDto = moduleDto.getChapters().get(0);
        MessageDto messageDto = chapterDto.getMessages().get(0);
        Bookmark bookmark = new Bookmark(externalId, createContentIdentifier(course.toContentIdentifierDto()), createContentIdentifier(moduleDto.toContentIdentifierDto()), createContentIdentifier(chapterDto.toContentIdentifierDto()), createContentIdentifier(messageDto.toContentIdentifierDto()));
        allBookmarks.add(bookmark);
        LOGGER.debug(String.format("Added bookmark for externalId %s %s ", externalId, bookmark));
    }

    @Override
    public void update(BookmarkDto bookmarkDto) {
        Bookmark bookmarkFromDb = allBookmarks.findBy(bookmarkDto.getExternalId());
        if (bookmarkFromDb == null) {
            LOGGER.error(String.format("Request for bookmark update failed for externalId %s as no bookmark exists for this id", bookmarkDto.getExternalId()));
            return;
        }
        if (bookmarkFromDb.wasModifiedAfter(DateTimeUtil.parse(bookmarkDto.getDateModified()))) {
            LOGGER.info(String.format("Request for bookmark update ignored for externalId %s as more recent bookmark exists for this id", bookmarkDto.getExternalId()));
            return;
        }
        bookmarkFromDb.update(createContentIdentifier(bookmarkDto.getCourse()), createContentIdentifier(bookmarkDto.getModule()),
                createContentIdentifier(bookmarkDto.getChapter()), createContentIdentifier(bookmarkDto.getMessage()));
        allBookmarks.update(bookmarkFromDb);
        LOGGER.debug(String.format("Bookmark updates for externalId %s with bookmark %s ", bookmarkFromDb.getExternalId(), bookmarkFromDb));
    }

    private ContentIdentifier createContentIdentifier(ContentIdentifierDto contentIdentifierDto) {
        return new ContentIdentifier(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion());
    }

    private ContentIdentifierDto createContentIdentifierDto(ContentIdentifier contentIdentifier) {
        return new ContentIdentifierDto(contentIdentifier.getContentId(), contentIdentifier.getVersion());
    }
}
