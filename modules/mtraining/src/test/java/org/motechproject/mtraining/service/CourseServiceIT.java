package org.motechproject.mtraining.service;

import org.ektorp.CouchDbConnector;
import org.hamcrest.core.Is;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.CourseContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
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
    private MessageContentBuilder messageContentBuilder;
    private ChapterContentBuilder chapterContentBuilder;
    private ModuleContentBuilder moduleContentBuilder;
    private CourseContentBuilder courseContentBuilder;

    @Before
    @After
    public void setUp() throws Exception {
        allCourses.removeAll();
        allModules.removeAll();
        allChapters.removeAll();
        allMessages.removeAll();
        messageContentBuilder = new MessageContentBuilder();
        chapterContentBuilder = new ChapterContentBuilder();
        moduleContentBuilder = new ModuleContentBuilder();
        courseContentBuilder = new CourseContentBuilder();
    }

    @Test
    public void shouldAddACourseWithModulesAndChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = messageContentBuilder.withName("ms01").withAudioFile("audio-01").buildMessageDTO();
        MessageDto messageDto2 = messageContentBuilder.withName("ms02").withAudioFile("audio-02").buildMessageDTO();
        ChapterDto chapterDto1 = chapterContentBuilder.withName("ch01").withMessageDTOs(asList(messageDto1)).buildChapterDTO();
        ChapterDto chapterDto2 = chapterContentBuilder.withName("ch02").withMessageDTOs(asList(messageDto2)).buildChapterDTO();
        ModuleDto moduleDto1 = moduleContentBuilder.withName("mod 01").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();
        ModuleDto moduleDto2 = moduleContentBuilder.withName("mod 02").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();
        CourseDto courseDto = courseContentBuilder.withName("mod 01").withModuleDtos(asList(moduleDto1, moduleDto2)).buildCourseDTO();

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
        Message existingMessage = messageContentBuilder.withContentId(UUID.randomUUID()).withVersion(1).withName("messageName1").withAudioFile("messageFileName1").buildMessage();
        Chapter existingChapter = chapterContentBuilder.withContentId(UUID.randomUUID()).withVersion(1).withName("chapterName1").withDescription("oldChapter1Description1").buildChapter();
        Module existingModule = moduleContentBuilder.withContentId(UUID.randomUUID()).withVersion(1).withName("moduleName1").withDescription("oldModule2Description").buildModule();
        allMessages.add(existingMessage);
        allChapters.add(existingChapter);
        allModules.add(existingModule);
        MessageDto messageDto1 = messageContentBuilder
                .withContentId(existingMessage.getContentId())
                .withVersion(1)
                .withName("messageName1")
                .withAudioFile("messageFileName1")
                .buildMessageDTO();
        MessageDto messageDto2 = messageContentBuilder.withContentId(existingMessage.getContentId())
                .withVersion(1)
                .withName("messageName2")
                .withAudioFile("messageFileName2")
                .buildMessageDTO();

        ChapterDto chapterDto1 = chapterContentBuilder
                .withContentId(existingChapter.getContentId())
                .withVersion(1)
                .withName("chapterName1")
                .withDescription("chapterDescription1")
                .withMessageDTOs(asList(messageDto1))
                .buildChapterDTO();

        ChapterDto chapterDto2 = chapterContentBuilder
                .withName("chapterName2")
                .withDescription("chapterDescription2")
                .withMessageDTOs(asList(messageDto2))
                .buildChapterDTO();


        ModuleDto moduleDto1 = moduleContentBuilder
                .withName("mod 01")
                .withContentId(existingModule.getContentId())
                .withVersion(1)
                .withChapterDTOs(asList(chapterDto1, chapterDto2))
                .buildModuleDTO();
        ModuleDto moduleDto2 = moduleContentBuilder
                .withName("mod 02")
                .withChapterDTOs(asList(chapterDto1, chapterDto2))
                .buildModuleDTO();
        CourseDto courseDto = courseContentBuilder
                .withName("CS001")
                .withModuleDtos(asList(moduleDto1, moduleDto2))
                .buildCourseDTO();

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
        MessageDto messageDto1 = messageContentBuilder.withName("messageName1").buildMessageDTO();
        ChapterDto chapterDto1 = chapterContentBuilder.withName("chapterName1").withMessageDTOs(asList(messageDto1)).buildChapterDTO();
        ModuleDto moduleDto1 = moduleContentBuilder.withName("moduleName1").withChapterDTOs(asList(chapterDto1)).buildModuleDTO();
        CourseDto savedCourse = courseContentBuilder.withName("courseName1").withModuleDtos(asList(moduleDto1)).buildCourseDTO();

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
