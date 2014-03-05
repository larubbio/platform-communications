package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

/**
 * Service Interface that exposes APIs to chapters
 */
public interface ChapterService {

    ContentIdentifierDto addChapter(ChapterDto chapterDto);

    ChapterDto getChapter(ContentIdentifierDto chapterIdentifier);

}

