package org.motechproject.mtraining.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.ektorp.CouchDbConnector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.ChildContentIdentifier;
import org.motechproject.mtraining.domain.Content;
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

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

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
    public void shouldAddAMessage() throws InterruptedException {
        MessageDto messageDto = new MessageDto("messageName", "fileName", "description");

        ContentIdentifierDto savedMessageIdentifier = courseService.addMessage(messageDto);

        List<Message> messagedInDb = allMessages.getAll();
        assertMessage(asList(messageDto), messagedInDb);
        assertContentIdentifier(savedMessageIdentifier, messagedInDb.get(0));
    }

    @Test
    public void shouldAddChapterWithMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto("messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto("messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto = new ChapterDto("chapterName", "chapterDescription", asList(messageDto1, messageDto2));

        ContentIdentifierDto savedChapterIdentifier = courseService.addChapter(chapterDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        assertMessage(asList(messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto), chaptersInDb, messagesInDb);
        assertContentIdentifier(savedChapterIdentifier, chaptersInDb.get(0));
    }

    @Test
    public void shouldAddModuleWithChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto("messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto("messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto1 = new ChapterDto("chapterName1", "chapterDescription1", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto("chapterName2", "chapterDescription2", asList(messageDto1, messageDto2));
        ModuleDto moduleDto = new ModuleDto("moduleName", null, asList(chapterDto1, chapterDto2));

        ContentIdentifierDto savedModuleIdentifier = courseService.addModule(moduleDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        List<Module> modulesInDb = allModules.getAll();
        assertMessage(asList(messageDto1, messageDto2, messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto1, chapterDto2), chaptersInDb, messagesInDb);
        assertModule(asList(moduleDto), modulesInDb, chaptersInDb);
        assertContentIdentifier(savedModuleIdentifier, modulesInDb.get(0));
    }

    @Test
    public void shouldAddACourseWithModulesAndChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto("messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto("messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto1 = new ChapterDto("chapterName1", "chapterDescription1", asList(messageDto1));
        ChapterDto chapterDto2 = new ChapterDto("chapterName2", "chapterDescription2", asList(messageDto2));
        ModuleDto moduleDto1 = new ModuleDto("moduleName1", null, asList(chapterDto1, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto("moduleName2", null, asList(chapterDto1, chapterDto2));
        CourseDto courseDto = new CourseDto("courseName", "courseDescription", asList(moduleDto1, moduleDto2));

        ContentIdentifierDto savedCourseIdentifier = courseService.addCourse(courseDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        List<Module> modulesInDb = allModules.getAll();
        List<Course> coursesInDb = allCourses.getAll();
        assertMessage(asList(messageDto1, messageDto2, messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto1, chapterDto2, chapterDto1, chapterDto2), chaptersInDb, messagesInDb);
        assertModule(asList(moduleDto1, moduleDto2), modulesInDb, chaptersInDb);
        assertCourse(courseDto, coursesInDb, modulesInDb);
        assertContentIdentifier(savedCourseIdentifier, coursesInDb.get(0));
    }

    private void assertMessage(List<MessageDto> messageDtos, List<Message> messagesInDb) {
        assertEquals(messageDtos.size(), messagesInDb.size());

        for (int i = 0; i < messageDtos.size(); i++) {
            Message messageInDb = messagesInDb.get(i);
            assertEquals(messageDtos.get(i).getName(), messageInDb.getName());
        }
    }

    private void assertChapter(List<ChapterDto> chapterDtos, List<Chapter> chaptersInDb, List<Message> messagesInDb) {
        assertEquals(chapterDtos.size(), chaptersInDb.size());

        for (int i = 0; i < chapterDtos.size(); i++) {
            Chapter chapterInDb = chaptersInDb.get(i);
            assertEquals(chapterDtos.get(i).getName(), chapterInDb.getName());
            assertChildNodes(chapterInDb.getMessages(), messagesInDb);
        }
    }

    private void assertModule(List<ModuleDto> moduleDtos, List<Module> modulesInDb, List<Chapter> chaptersInDb) {
        assertEquals(moduleDtos.size(), modulesInDb.size());

        for (int i = 0; i < moduleDtos.size(); i++) {
            Module moduleInDb = modulesInDb.get(i);
            assertEquals(moduleDtos.get(i).getName(), moduleInDb.getName());
            assertChildNodes(moduleInDb.getChapters(), chaptersInDb);
        }
    }

    private void assertCourse(CourseDto courseDto, List<Course> coursesInDb, List<Module> modulesInDb) {
        assertEquals(1, coursesInDb.size());
        Course courseInDb = coursesInDb.get(0);
        assertEquals(courseDto.getName(), courseInDb.getName());
        assertChildNodes(courseInDb.getModules(), modulesInDb);
    }

    private <T extends Content> void assertChildNodes(List<ChildContentIdentifier> childContentIdentifiers, List<T> childNodesInDb) {
        for (final ChildContentIdentifier childContentIdentifier : childContentIdentifiers) {
            boolean childNodeExist = CollectionUtils.exists(childNodesInDb, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    Content content = (Content) object;
                    return content.getContentId().equals(childContentIdentifier.getContentId()) && content.getVersion().equals(childContentIdentifier.getVersion());
                }
            });
            assertTrue(childNodeExist);
        }
    }

    private void assertContentIdentifier(ContentIdentifierDto returnedContentIdentifier, Content contentInDb) {
        assertEquals(contentInDb.getContentId(), returnedContentIdentifier.getContentId());
        assertEquals(contentInDb.getVersion(), returnedContentIdentifier.getVersion());
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return couchDbConnector;
    }
}
