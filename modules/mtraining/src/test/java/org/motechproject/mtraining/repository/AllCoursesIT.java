package org.motechproject.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.UUID;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCoursesIT {

    @Autowired
    private AllCourses allCourses;

    @Test
    public void shouldReturnCourseByContentId() {
        Course cs001 = new Course(true, "cs001", "test course", "externalId", "Author", Collections.<Module>emptyList());
        Course newVersionOfCS001 = new Course(true, "cs001", "edit test course", "externalId", "Author", Collections.<Module>emptyList());
        newVersionOfCS001.setContentId(cs001.getContentId());
        newVersionOfCS001.incrementVersion();


        allCourses.add(cs001);
        allCourses.add(newVersionOfCS001);

        Course courseByContentId = allCourses.findBy(newVersionOfCS001.getContentId(), newVersionOfCS001.getVersion());
        assertThat(courseByContentId, IsNull.notNullValue());
        assertThat(courseByContentId.getName(), Is.is(newVersionOfCS001.getName()));
        assertThat(courseByContentId.getVersion(), Is.is(newVersionOfCS001.getVersion()));
    }

    @Test
    public void shouldReturnLatestPublishedCourseByContentId() {
        UUID courseContentId = UUID.randomUUID();
        Course firstVersionOfCs001Published = new Course(courseContentId, 1, true, "cs001.version1", "test course", "externalId", "Author", Collections.<Module>emptyList());
        Course secondVersionOfCs001Published = new Course(courseContentId, 2, true, "cs001.version2", "edit test course", "externalId", "Author", Collections.<Module>emptyList());
        Course thirdVersionOfCs001Published = new Course(courseContentId, 3, true, "cs001.version3", "edit test course", "externalId", "Author", Collections.<Module>emptyList());
        Course fourthVersionOfCs001PublishedAndInactive = new Course(courseContentId, 4, false, "cs001.version4", "edit test course", "externalId", "Author", Collections.<Module>emptyList());
        Course latestVersionOfCs001NotPublished = new Course(courseContentId, 5, true, "cs001.version5", "edit test course", "externalId", "Author", Collections.<Module>emptyList());

        firstVersionOfCs001Published.publish();
        secondVersionOfCs001Published.publish();
        thirdVersionOfCs001Published.publish();
        fourthVersionOfCs001PublishedAndInactive.publish();

        allCourses.add(firstVersionOfCs001Published);
        allCourses.add(secondVersionOfCs001Published);
        allCourses.add(thirdVersionOfCs001Published);
        allCourses.add(fourthVersionOfCs001PublishedAndInactive);
        allCourses.add(latestVersionOfCs001NotPublished);

        Course latestPublishedCourse = allCourses.findLatestPublishedCourse(secondVersionOfCs001Published.getContentId());
        assertThat(latestPublishedCourse, IsNull.notNullValue());
        assertThat(latestPublishedCourse.getName(), Is.is(thirdVersionOfCs001Published.getName()));
        assertThat(latestPublishedCourse.getVersion(), Is.is(thirdVersionOfCs001Published.getVersion()));
    }

    @Test
    public void shouldReturnNullIfLatestPublishedCourseByContentIdNotFound() {
        UUID courseContentId = UUID.randomUUID();

        Course latestPublishedCourse = allCourses.findLatestPublishedCourse(courseContentId);

        assertNull(latestPublishedCourse);
    }

    @After
    public void after() {
        allCourses.bulkDelete(allCourses.getAll());
    }
}
