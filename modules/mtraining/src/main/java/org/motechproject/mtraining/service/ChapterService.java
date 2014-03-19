package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to chapters
 */
public interface ChapterService {

    /**
     * Add or update a chapter
     * @param chapterDto
     * @return
     */
    ContentIdentifierDto addOrUpdateChapter(ChapterDto chapterDto);

    /**
     * Retrieve a chapter given the chapter identifier
     * @param chapterIdentifier
     * @return
     */
    ChapterDto getChapter(ContentIdentifierDto chapterIdentifier);

    /**
     * Retrieve all the chapters
     * @return
     */
    List<ChapterDto> getAllChapters();
}

