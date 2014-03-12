package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to manage different course contents
 */
public interface CourseService {

    ContentIdentifierDto addOrUpdateCourse(CourseDto courseDto);

    CourseDto getCourse(ContentIdentifierDto courseId);

    List<CourseDto> getAllCourses();
}
