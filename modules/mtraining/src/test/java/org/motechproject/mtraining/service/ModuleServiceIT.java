package org.motechproject.mtraining.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.repository.AllModules;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.motechproject.mtraining.service.AssertCourseContents.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class ModuleServiceIT {

    @Autowired
    private ModuleService moduleService;
    @Autowired
    private AllMessages allMessages;
    @Autowired
    private AllChapters allChapters;
    @Autowired
    private AllModules allModules;
    private ContentIdentifierDto contentIdentifier;

    @Before
    public void setUp() throws Exception {
        contentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

    }

    @Test
    public void shouldAddModuleWithChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto("messageName1", "messageFileName1", null, contentIdentifier);
        MessageDto messageDto2 = new MessageDto("messageName2", "messageFileName2", "description2", contentIdentifier);
        ChapterDto chapterDto1 = new ChapterDto("chapterName1", "chapterDescription1", contentIdentifier, asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto("chapterName2", "chapterDescription2", contentIdentifier, asList(messageDto1, messageDto2));
        ModuleDto moduleDto = new ModuleDto("moduleName", null, contentIdentifier, asList(chapterDto1, chapterDto2));

        ContentIdentifierDto savedModuleIdentifier = moduleService.addModule(moduleDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        List<Module> modulesInDb = allModules.getAll();
        assertMessage(asList(messageDto1, messageDto2, messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto1, chapterDto2), chaptersInDb, messagesInDb);
        assertModule(asList(moduleDto), modulesInDb, chaptersInDb);
        assertContentIdentifier(savedModuleIdentifier, modulesInDb.get(0));
    }

    @After
    @Before
    public void after() {
        allMessages.removeAll();
        allChapters.removeAll();
        allModules.removeAll();
    }


}
