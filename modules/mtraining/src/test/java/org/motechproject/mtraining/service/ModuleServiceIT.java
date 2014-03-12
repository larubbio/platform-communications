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

import static java.util.Arrays.asList;
import static org.motechproject.mtraining.service.AssertCourseContents.assertChapter;
import static org.motechproject.mtraining.service.AssertCourseContents.assertContentIdentifier;
import static org.motechproject.mtraining.service.AssertCourseContents.assertMessage;
import static org.motechproject.mtraining.service.AssertCourseContents.assertModule;

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

    @Test
    public void shouldAddModuleWithChaptersAndMessages() throws InterruptedException {
        MessageDto messageDto1 = new MessageDto(true, "messageName1", "messageFileName1", null);
        MessageDto messageDto2 = new MessageDto(true, "messageName2", "messageFileName2", "description2");
        ChapterDto chapterDto1 = new ChapterDto(true, "chapterName1", "chapterDescription1", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto(true, "chapterName2", "chapterDescription2", asList(messageDto1, messageDto2));
        ModuleDto moduleDto = new ModuleDto(true, "moduleName", null, asList(chapterDto1, chapterDto2));

        ContentIdentifierDto savedModuleIdentifier = moduleService.addOrUpdateModule(moduleDto);

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
