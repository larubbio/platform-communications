package org.motechproject.mtraining.dto;


import org.joda.time.DateTime;
import org.junit.Test;
import org.motechproject.mtraining.constants.CourseStatus;

import static junit.framework.Assert.assertEquals;

public class EnrolleeEnrolleeCourseProgressDtoTest {

    @Test
    public void shouldCalculateCorrectNumberOfDaysCompletedForGivenCourseProgress() throws Exception {
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("123321", DateTime.now().minusHours(24), new BookmarkDto(), CourseStatus.STARTED);
        enrolleeCourseProgressDto.setTimeLeftToCompleteCourseInHrs(60);
        int timeLeftToCompleteCourseInHrs = enrolleeCourseProgressDto.getTimeLeftToCompleteCourseInHrs();
        assertEquals(1416, timeLeftToCompleteCourseInHrs);
    }

    @Test
    public void shouldHaveOneDayLeftForATimeDurationOfLessThanOneDayForGivenCourseProgress() throws Exception {
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("123321", DateTime.now().minusHours(60 * 24).plusHours(23), new BookmarkDto(), CourseStatus.STARTED);
        enrolleeCourseProgressDto.setTimeLeftToCompleteCourseInHrs(60);
        int timeLeftToCompleteCourseInHrs = enrolleeCourseProgressDto.getTimeLeftToCompleteCourseInHrs();
        assertEquals(23, timeLeftToCompleteCourseInHrs);
    }

    @Test
    public void shouldHaveZeroDaysLeftForATimeDurationOfOverForGivenCourseProgress() throws Exception {
        EnrolleeCourseProgressDto enrolleeCourseProgressDto = new EnrolleeCourseProgressDto("123321", DateTime.now().minusHours(60*24), new BookmarkDto(), CourseStatus.STARTED);
        enrolleeCourseProgressDto.setTimeLeftToCompleteCourseInHrs(60);
        int timeLeftToCompleteCourseInHrs = enrolleeCourseProgressDto.getTimeLeftToCompleteCourseInHrs();
        assertEquals(0, timeLeftToCompleteCourseInHrs);
    }

}
