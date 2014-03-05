package org.motechproject.mtraining.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.motechproject.mtraining.service.AssertCourseContents.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class ChapterServiceIT {

    @Autowired
    private ChapterService chapterService;
    @Autowired
    private AllMessages allMessages;
    @Autowired
    private AllChapters allChapters;

    @After
    @Before
    public void after() {
        allMessages.removeAll();
        allChapters.removeAll();
    }

    @Test
    public void shouldAddChapterWithMessages() throws InterruptedException {
        ContentIdentifierDto contentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        MessageDto messageDto1 = new MessageDto("messageName1", "messageFileName1", null, contentIdentifier);
        MessageDto messageDto2 = new MessageDto("messageName2", "messageFileName2", "description2", contentIdentifier);
        ChapterDto chapterDto = new ChapterDto("chapterName", "chapterDescription", contentIdentifier, asList(messageDto1, messageDto2));

        ContentIdentifierDto savedChapterIdentifier = chapterService.addChapter(chapterDto);

        List<Message> messagesInDb = allMessages.getAll();
        List<Chapter> chaptersInDb = allChapters.getAll();
        assertMessage(asList(messageDto1, messageDto2), messagesInDb);
        assertChapter(asList(chapterDto), chaptersInDb, messagesInDb);
        assertContentIdentifier(savedChapterIdentifier, chaptersInDb.get(0));
    }
}