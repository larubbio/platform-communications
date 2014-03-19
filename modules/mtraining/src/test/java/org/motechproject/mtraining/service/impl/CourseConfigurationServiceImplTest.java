package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.CourseConfiguration;
import org.motechproject.mtraining.domain.Location;
import org.motechproject.mtraining.dto.CourseConfigurationDto;
import org.motechproject.mtraining.dto.LocationDto;
import org.motechproject.mtraining.exception.CourseNotFoundException;
import org.motechproject.mtraining.repository.AllCourseConfigurations;
import org.motechproject.mtraining.repository.AllCourses;

import java.util.UUID;

import static java.lang.Integer.valueOf;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.contains;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseConfigurationServiceImplTest {

    @Mock
    private AllCourseConfigurations allCourseConfigs;
    @Mock
    private AllCourses allCourses;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CourseConfigurationServiceImpl courseConfigService;
    private String courseName;
    private LocationDto location;

    @Before
    public void setUp() {
        courseConfigService = new CourseConfigurationServiceImpl(allCourseConfigs, allCourses);
        courseName = "CS001";
        location = new LocationDto("block", "district", "state");

    }

    public void shouldThrowCourseNotFoundExceptionWhenCourseNotAvailable() {
        CourseConfigurationDto course = new CourseConfigurationDto(courseName, 5, location);
        when(allCourses.findByName(courseName)).thenReturn(null);

        expectedException.expect(CourseNotFoundException.class);
        expectedException.expectMessage(contains(courseName));

        courseConfigService.addOrUpdateCourseConfiguration(course);
    }

    @Test
    public void shouldAddCourseConfigIfNotExisting() {
        String courseName = "courseName";
        CourseConfigurationDto course = new CourseConfigurationDto(courseName, 5, location);
        UUID courseId = UUID.randomUUID();
        when(allCourses.findByName(courseName)).thenReturn(new Course(courseId, 1, false, courseName, null, "externalId", null, null));
        when(allCourseConfigs.findCourseConfigurationFor(courseId)).thenReturn(null);

        courseConfigService.addOrUpdateCourseConfiguration(course);

        ArgumentCaptor<CourseConfiguration> courseConfigurationCaptor = ArgumentCaptor.forClass(CourseConfiguration.class);
        verify(allCourseConfigs).add(courseConfigurationCaptor.capture());
        CourseConfiguration courseConfiguration = courseConfigurationCaptor.getValue();
        assertEquals(courseId, courseConfiguration.getCourseId());
        assertEquals(valueOf(5), courseConfiguration.getDurationInDays());
    }

    @Test
    public void shouldUpdateCourseConfigIfAlreadyExists() {
        String courseName = "courseName";
        CourseConfigurationDto course = new CourseConfigurationDto(courseName, 5, location);
        UUID courseId = UUID.randomUUID();
        when(allCourses.findByName(courseName)).thenReturn(new Course(courseId, 1, false, courseName, null, "externalId", null, null));
        when(allCourseConfigs.findCourseConfigurationFor(courseId)).thenReturn(new CourseConfiguration(courseId, 3, new Location("block", "district", "state")));

        courseConfigService.addOrUpdateCourseConfiguration(course);

        ArgumentCaptor<CourseConfiguration> courseConfigurationCaptor = ArgumentCaptor.forClass(CourseConfiguration.class);
        verify(allCourseConfigs).update(courseConfigurationCaptor.capture());
        CourseConfiguration courseConfiguration = courseConfigurationCaptor.getValue();
        assertEquals(courseId, courseConfiguration.getCourseId());
        assertEquals(valueOf(5), courseConfiguration.getDurationInDays());
    }
}
