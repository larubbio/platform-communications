package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseConfiguration;
import org.motechproject.mtraining.domain.Location;
import org.motechproject.mtraining.dto.CourseConfigurationDto;
import org.motechproject.mtraining.exception.CourseNotFoundException;
import org.motechproject.mtraining.repository.AllCourseConfigurations;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.service.CourseConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implementation class for {@link org.motechproject.mtraining.service.CourseConfigurationService}.
 * Given a {@link org.motechproject.mtraining.dto.CourseConfigurationDto} for a content, it adds the configurations against the content Id for the corresponding
 * {@link org.motechproject.mtraining.domain.Course}.
 */

@Service("courseConfigurationService")
public class CourseConfigurationServiceImpl implements CourseConfigurationService {

    private AllCourseConfigurations allCourseConfigs;
    private AllCourses allCourses;

    @Autowired
    public CourseConfigurationServiceImpl(AllCourseConfigurations allCourseConfigs, AllCourses allCourses) {
        this.allCourseConfigs = allCourseConfigs;
        this.allCourses = allCourses;
    }

    /**
     * add or update the course configuration for a given course
     * if the course is not found for the given course name then throws @exception CourseNotFoundException
     * otherwise find the courseConfiguration using the contentId of the course
     * if courseConfiguration is found then update it
     * otherwise add the new courseConfiguration for the course
     * @param courseDto
     */

    @Override
    public void addOrUpdateCourseConfiguration(CourseConfigurationDto courseDto) {
        Course course = allCourses.findByName(courseDto.getCourseName());
        if (course == null) {
            throw new CourseNotFoundException(courseDto.getCourseName());
        }
        CourseConfiguration courseConfiguration = allCourseConfigs.findCourseConfigurationFor(course.getContentId());
        Integer courseDuration = courseDto.getCourseDuration();
        Location location = new Location(courseDto.getLocation().getBlock(), courseDto.getLocation().getDistrict(),
                courseDto.getLocation().getState());
        if (courseConfiguration == null) {
            allCourseConfigs.add(new CourseConfiguration(course.getContentId(), courseDuration,
                    location));
            return;
        }
        courseConfiguration.update(courseDuration, location);
        allCourseConfigs.update(courseConfiguration);
    }
}
