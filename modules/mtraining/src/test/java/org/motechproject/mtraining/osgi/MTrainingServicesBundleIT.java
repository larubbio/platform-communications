package org.motechproject.mtraining.osgi;

import org.motechproject.mtraining.service.BookmarkService;
import org.motechproject.mtraining.service.ChapterService;
import org.motechproject.mtraining.service.CourseConfigurationService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.service.MessageService;
import org.motechproject.mtraining.service.ModuleService;
import org.motechproject.mtraining.service.QuestionService;
import org.motechproject.mtraining.service.QuizService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

public class MTrainingServicesBundleIT extends BaseOsgiIT {


    private ConfigurableApplicationContext applicationContext;

    @Override
    protected void onSetUp() throws Exception {
        applicationContext = getApplicationContext();
    }

    public void testThatServicesExposedByMTrainingBundleAreAvailable() {
        BookmarkService bookmarkService = (BookmarkService) applicationContext.getBean("bookmarkService");
        assertNotNull(bookmarkService);
        CourseService courseService = (CourseService) applicationContext.getBean("courseService");
        assertNotNull(courseService);
        ModuleService moduleService = (ModuleService) applicationContext.getBean("moduleService");
        assertNotNull(moduleService);
        ChapterService chapterService = (ChapterService) applicationContext.getBean("chapterService");
        assertNotNull(chapterService);
        MessageService messageService = (MessageService) applicationContext.getBean("messageService");
        assertNotNull(messageService);
        QuizService quizService = (QuizService) applicationContext.getBean("quizService");
        assertNotNull(quizService);
        QuestionService questionService = (QuestionService) applicationContext.getBean("questionService");
        assertNotNull(questionService);
        CourseConfigurationService courseCourseConfigurationService = (CourseConfigurationService) applicationContext.getBean("courseConfigurationService");
        assertNotNull(courseCourseConfigurationService);
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
