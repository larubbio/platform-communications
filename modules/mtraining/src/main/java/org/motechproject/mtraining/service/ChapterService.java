package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to chapters
 */
public interface ChapterService {

    ContentIdentifierDto addOrUpdateChapter(ChapterDto chapterDto);

    ChapterDto getChapter(ContentIdentifierDto chapterIdentifier);

    List<ChapterDto> getAllChapters();
}

