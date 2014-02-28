package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

/**
 * Service Interface that exposes APIs to manage different course contents
 */
public interface CourseService {

    ContentIdentifierDto addCourse(CourseDto courseDto);

    ContentIdentifierDto addModule(ModuleDto moduleDto);

    ContentIdentifierDto addChapter(ChapterDto chapterDto);

    ContentIdentifierDto addMessage(MessageDto messageDto);
}
