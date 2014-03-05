package org.motechproject.mtraining.osgi;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CourseBundleIT extends BaseOsgiIT {

    public void testThatCourseServiceIsAvailable() throws Exception {
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        assertNotNull(courseService);
    }

    public void testThatAnEventIsRaisedWhenACourseIsCreated() throws InterruptedException {
        final TestEventHandler testEventHandler = (TestEventHandler) getApplicationContext().getBean("testEventHandler");
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        CourseDto courseDto = new CourseDto("messageName", "description", new ContentIdentifierDto(UUID.randomUUID(), 1), Collections.<ModuleDto>emptyList());

        courseService.addCourse(courseDto);

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !testEventHandler.isEventRaised();
            }
        }, 40000).start();

        assertTrue(testEventHandler.isEventRaised());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"META-INF/spring/testTrainingApplicationContext.xml"};
    }

    @Override
    protected List<String> getImports() {
        List<String> imports = new ArrayList<>();
        imports.add("org.motechproject.mtraining.service");
        imports.add("org.motechproject.mtraining.dto");
        imports.add("org.motechproject.event");
        imports.add("org.motechproject.event.listener");
        imports.add("org.motechproject.event.listener.annotations");
        return imports;
    }
}
