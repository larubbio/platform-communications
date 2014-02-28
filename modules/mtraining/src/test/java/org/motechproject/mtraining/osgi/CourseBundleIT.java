package org.motechproject.mtraining.osgi;

import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;

import java.util.ArrayList;
import java.util.List;

public class CourseBundleIT extends BaseOsgiIT {

    public void testThatCourseServiceIsAvailable() throws Exception {
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        assertNotNull(courseService);
    }

    public void testThatAnEventIsRaisedWhenAnyNodeIsCreated() throws InterruptedException {
        final TestEventHandler testEventHandler = (TestEventHandler) getApplicationContext().getBean("testEventHandler");
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        MessageDto messageDto = new MessageDto("messageName", "messageFileName", "description");

        courseService.addMessage(messageDto);

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !testEventHandler.isEventRaised();
            }
        }, 20000).start();

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
        imports.add("org.motechproject.event");
        imports.add("org.motechproject.event.listener");
        imports.add("org.motechproject.event.listener.annotations");

        return imports;
    }
}
