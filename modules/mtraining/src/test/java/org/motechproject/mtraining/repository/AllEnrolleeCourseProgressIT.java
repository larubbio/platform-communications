package org.motechproject.mtraining.repository;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.constants.CourseStatus;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.EnrolleeCourseProgress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllEnrolleeCourseProgressIT {
    @Autowired
    AllEnrolleeCourseProgress allCourseProgress;

    @Test
    public void shouldGetAllEnrolleeCourseProgressForEnrollee() {
        ContentIdentifier course01 = new ContentIdentifier(UUID.randomUUID(), 1);
        ContentIdentifier course02 = new ContentIdentifier(UUID.randomUUID(), 1);
        allCourseProgress.add(new EnrolleeCourseProgress("externalId1", DateTime.now(), CourseStatus.COMPLETED, course01.getContentId()));
        allCourseProgress.add(new EnrolleeCourseProgress("externalId1", DateTime.now(), CourseStatus.STARTED, course02.getContentId()));
        List<EnrolleeCourseProgress> enrolleeCourseProgressList = allCourseProgress.findBy("externalId1");
        assertEquals(2, enrolleeCourseProgressList.size());
        EnrolleeCourseProgress enrolleeCourseProgress01 = enrolleeCourseProgressList.get(0);
        EnrolleeCourseProgress enrolleeCourseProgress02 = enrolleeCourseProgressList.get(1);
        assertEquals(course01.getContentId(), enrolleeCourseProgress01.getCourseContentId());
        assertEquals(CourseStatus.COMPLETED, enrolleeCourseProgress01.getCourseStatus());
        assertEquals(course02.getContentId(), enrolleeCourseProgress02.getCourseContentId());
        assertEquals(CourseStatus.STARTED, enrolleeCourseProgress02.getCourseStatus());
    }

    @Test
    public void shouldGetEnrolleeCourseProgressForEnrolleeGivenCourse() {
        ContentIdentifier course01 = new ContentIdentifier(UUID.randomUUID(), 1);
        ContentIdentifier course02 = new ContentIdentifier(UUID.randomUUID(), 1);
        allCourseProgress.add(new EnrolleeCourseProgress("externalId1", DateTime.now(), CourseStatus.ONGOING, course01.getContentId()));
        allCourseProgress.add(new EnrolleeCourseProgress("externalId1", DateTime.now(), CourseStatus.CLOSED,course02.getContentId() ));

        EnrolleeCourseProgress enrolleeCourseProgress = allCourseProgress.findBy("externalId1", course01.getContentId());
        assertEquals(course01.getContentId(), enrolleeCourseProgress.getCourseContentId());
        assertEquals(CourseStatus.ONGOING, enrolleeCourseProgress.getCourseStatus());
    }

    @After
    @Before
    public void cleanDb() {
        allCourseProgress.removeAll();
    }
}
