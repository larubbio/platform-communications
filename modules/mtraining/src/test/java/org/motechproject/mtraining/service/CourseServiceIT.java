package org.motechproject.mtraining.service;

import org.ektorp.CouchDbConnector;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.repository.AllModules;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertThat;
import static org.motechproject.mtraining.service.AssertCourseContents.assertChapter;
import static org.motechproject.mtraining.service.AssertCourseContents.assertChapterWithExistingChapter;
import static org.motechproject.mtraining.service.AssertCourseContents.assertContentIdUpdate;
import static org.motechproject.mtraining.service.AssertCourseContents.assertContentIdentifier;
import static org.motechproject.mtraining.service.AssertCourseContents.assertCourse;
import static org.motechproject.mtraining.service.AssertCourseContents.assertMessage;
import static org.motechproject.mtraining.service.AssertCourseContents.assertMessageWithExistingMessage;
import static org.motechproject.mtraining.service.AssertCourseContents.assertModule;
import static org.motechproject.mtraining.service.AssertCourseContents.assertModuleWithExistingModule;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class CourseServiceIT extends SpringIntegrationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    @Qualifier("mtrainingDbConnector")
    private CouchDbConnector couchDbConnector;
    @Autowired
    private AllMessages allMessages;
    @Autowired
    private AllChapters allChapters;
    @Autowired
    private AllModules allModules;
    @Autowired
    private AllCourses allCourses;

    @Before
    @After
    public void setUp() throws Exception {
        allCourses.removeAll();
        allModules.removeAll();
        allChapters.removeAll();
        allMessages.removeAll();
    }

    @Test
    public void shouldAddACourseWithModulesAndChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto(true, "messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto(true, "messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto1 = new ChapterDto(true, "chapterName1", "chapterDescription1", asList(messageDto1));
        ChapterDto chapterDto2 = new ChapterDto(true, "chapterName2", "chapterDescription2", asList(messageDto2));
        ModuleDto moduleDto1 = new ModuleDto(true, "moduleName1", null, asList(chapterDto1, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto(true, "moduleName2", null, asList(chapterDto1, chapterDto2));
        CourseDto courseDto = new CourseDto(true, "courseName", "courseDescription", asList(moduleDto1, moduleDto2));

        ContentIdentifierDto savedCourse = courseService.addOrUpdateCourse(courseDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        List<Module> modulesInDb = allModules.getAll();
        List<Course> coursesInDb = allCourses.getAll();
        assertMessage(asList(messageDto1, messageDto2, messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto1, chapterDto2, chapterDto1, chapterDto2), chaptersInDb, messagesInDb);
        assertModule(asList(moduleDto1, moduleDto2), modulesInDb, chaptersInDb);
        assertCourse(courseDto, coursesInDb, modulesInDb);
        assertContentIdentifier(savedCourse, coursesInDb.get(0));
    }

    @Test
    public void shouldMapTheContentIdOfExistingContentsToGivenContentsAndAddCourseWithModulesAndChaptersAndMessages() throws InterruptedException {
        Message existingMessage = new Message(UUID.randomUUID(), 1, true, "messageName1", "messageFileName1", null);
        Chapter existingChapter = new Chapter(UUID.randomUUID(), 1, true, "chapterName1", "oldChapter1Description", Collections.EMPTY_LIST);
        Module existingModule = new Module(UUID.randomUUID(), 1, true, "moduleName1", "oldModule2Description", Collections.EMPTY_LIST);
        allMessages.add(existingMessage);
        allChapters.add(existingChapter);
        allModules.add(existingModule);

        MessageDto messageDto1 = new MessageDto(existingMessage.getContentId(), true, "messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto(true, "messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto1 = new ChapterDto(existingChapter.getContentId(), true, "chapterName1", "chapterDescription1", asList(messageDto1));
        ChapterDto chapterDto2 = new ChapterDto(true, "chapterName2", "chapterDescription2", asList(messageDto2));
        ModuleDto moduleDto1 = new ModuleDto(existingModule.getContentId(), true, "moduleName1", null, asList(chapterDto1, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto(true, "moduleName2", null, asList(chapterDto1, chapterDto2));
        CourseDto courseDto = new CourseDto(true, "courseName", "courseDescription", asList(moduleDto1, moduleDto2));

        ContentIdentifierDto savedCourse = courseService.addOrUpdateCourse(courseDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        List<Module> modulesInDb = allModules.getAll();
        List<Course> coursesInDb = allCourses.getAll();
        assertMessageWithExistingMessage(asList(messageDto1, messageDto2, messageDto1, messageDto2), messagesInDb, 1);
        assertContentIdUpdate(existingMessage, messagesInDb.get(1));
        assertChapterWithExistingChapter(asList(chapterDto1, chapterDto2, chapterDto1, chapterDto2), chaptersInDb, messagesInDb, 1);
        assertContentIdUpdate(existingChapter, chaptersInDb.get(1));
        assertModuleWithExistingModule(asList(moduleDto1, moduleDto2), modulesInDb, chaptersInDb, 1);
        assertContentIdUpdate(existingModule, modulesInDb.get(1));
        assertCourse(courseDto, coursesInDb, modulesInDb);
        assertContentIdentifier(savedCourse, coursesInDb.get(0));
    }

    @Test
    public void shouldRetrieveACourseGivenItsId() {
        MessageDto messageDto1 = new MessageDto(true, "messageName1", "messageFileName1", null);
        ChapterDto chapterDto1 = new ChapterDto(true, "chapterName1", "chapterDescription1", asList(messageDto1));
        ModuleDto moduleDto1 = new ModuleDto(true, "moduleName1", null, asList(chapterDto1));
        CourseDto savedCourse = new CourseDto(true, "courseName", "courseDescription", asList(moduleDto1));

        ContentIdentifierDto savedCourseIdentifier = courseService.addOrUpdateCourse(savedCourse);

        CourseDto course = courseService.getCourse(savedCourseIdentifier);
        assertThat(course.getName(), Is.is(savedCourse.getName()));

        List<ModuleDto> modules = course.getModules();
        assertThat(modules.size(), Is.is(1));

        ModuleDto moduleDto = modules.get(0);

        assertThat(moduleDto.getName(), Is.is("moduleName1"));

        List<ChapterDto> chapters = moduleDto.getChapters();

        assertThat(chapters.size(), Is.is(1));

        ChapterDto chapterDto = chapters.get(0);
        assertThat(chapterDto.getName(), Is.is("chapterName1"));

        List<MessageDto> messages = chapterDto.getMessages();
        assertThat(messages.size(), Is.is(1));
        assertThat(messages.get(0).getName(), Is.is("messageName1"));
    }


    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
