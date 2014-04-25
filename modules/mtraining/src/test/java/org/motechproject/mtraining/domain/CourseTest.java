package org.motechproject.mtraining.domain;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertThat;

public class CourseTest {

    @Test
    public void shouldIncrementCourseVersion() {
        Course course = new Course(true, "cs001", "test", "externalId", "Author", Collections.<Module>emptyList());
        assertThat(course.getVersion(), Is.is(1));
        course.incrementVersion();
        assertThat(course.getVersion(), Is.is(2));

    }
}
