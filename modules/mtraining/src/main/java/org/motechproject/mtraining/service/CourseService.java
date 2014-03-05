package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;

/**
 * Service Interface that exposes APIs to manage different course contents
 */
public interface CourseService {

    ContentIdentifierDto addCourse(CourseDto courseDto);

    CourseDto getCourse(ContentIdentifierDto courseId);

}
