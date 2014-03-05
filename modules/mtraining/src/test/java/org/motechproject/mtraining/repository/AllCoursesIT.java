package org.motechproject.mtraining.repository;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllCoursesIT {


    @Autowired
    private AllCourses allCourses;

    @Test
    public void shouldReturnCourseByContentId() {
        Course cs001 = new Course("cs001", "test course", Collections.<ContentIdentifier>emptyList());
        Course cs001_version2 = new Course("cs001", "edit test course", Collections.<ContentIdentifier>emptyList());
        cs001_version2.setContentId(cs001.getContentId());
        cs001_version2.incrementVersion();

        allCourses.add(cs001);
        allCourses.add(cs001_version2);

        Course courseByContentId = allCourses.findBy(cs001_version2.getContentId(), cs001_version2.getVersion());
        assertThat(courseByContentId, IsNull.notNullValue());
        assertThat(courseByContentId.getName(), Is.is(cs001_version2.getName()));
        assertThat(courseByContentId.getVersion(), Is.is(cs001_version2.getVersion()));
    }

    @After
    public void after() {
        allCourses.bulkDelete(allCourses.getAll());
    }

}
