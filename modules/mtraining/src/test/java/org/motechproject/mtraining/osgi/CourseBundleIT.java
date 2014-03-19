package org.motechproject.mtraining.osgi;

import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.ChapterService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.service.MessageService;
import org.motechproject.mtraining.service.ModuleService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CourseBundleIT extends BaseOsgiIT {

    public void testThatAllContentServicesAreAvailable() throws Exception {
        assertServiceAvailability(CourseService.class);
        assertServiceAvailability(ModuleService.class);
        assertServiceAvailability(ChapterService.class);
        assertServiceAvailability(MessageService.class);
    }

    private <T> void assertServiceAvailability(Class<T> serviceName) {
        ServiceReference serviceReference = bundleContext.getServiceReference(serviceName.getName());
        assertNotNull(serviceReference);
        T service = (T) bundleContext.getService(serviceReference);
        assertNotNull(service);
    }

    //TODO:FIX This Randomly failing test
    public void shouldRaiseEventWhenCourseIsCreated() throws InterruptedException {
        final TestEventHandler testEventHandler = (TestEventHandler) getApplicationContext().getBean("testEventHandler");
        CourseService courseService = (CourseService) getApplicationContext().getBean("courseService");
        CourseDto courseDto = new CourseDto(true, "messageName", "description", Collections.<ModuleDto>emptyList());

        courseService.addOrUpdateCourse(courseDto);

        new Wait(new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return !testEventHandler.isEventRaised();
            }
        }, 60000).start();

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
