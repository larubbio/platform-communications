package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.CourseConfigurationDto;

/**
 * Service Interface that exposes APIs to manage configurations for different contents
 */
public interface CourseConfigurationService {
    void addOrUpdateCourseConfiguration(CourseConfigurationDto courseDto);
}
