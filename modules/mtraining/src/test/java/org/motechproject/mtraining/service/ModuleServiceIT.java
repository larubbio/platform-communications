package org.motechproject.mtraining.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
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
        MessageContentBuilder messageContentBuilder = new MessageContentBuilder();
        ChapterContentBuilder chapterContentBuilder = new ChapterContentBuilder();
        MessageDto messageDto1 = messageContentBuilder.withName("messageName1").withAudioFile("audio1").buildMessageDTO();
        MessageDto messageDto2 = messageContentBuilder.withName("messageName2").withAudioFile("audio2").buildMessageDTO();
        ChapterDto chapterDto1 = chapterContentBuilder.withName("chapter01").withMessageDTOs(asList(messageDto1, messageDto2)).buildChapterDTO();
        ChapterDto chapterDto2 = chapterContentBuilder.withName("chapter02").withMessageDTOs(asList(messageDto1, messageDto2)).buildChapterDTO();
        ModuleDto moduleDto = new ModuleContentBuilder().withName("mod01").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();

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
