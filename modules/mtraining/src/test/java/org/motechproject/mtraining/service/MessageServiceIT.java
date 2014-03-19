package org.motechproject.mtraining.service;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.repository.AllMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.motechproject.mtraining.service.AssertCourseContents.assertContentIdentifier;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class MessageServiceIT {

    @Autowired
    private MessageService messageService;
    @Autowired
    private AllMessages allMessages;

    @Before
    @After
    public void clearMessages() {
        allMessages.removeAll();
    }

    @Test
    public void shouldAddAMessage() throws InterruptedException {
        MessageDto messageDto = new MessageContentBuilder().buildMessageDTO();

        ContentIdentifierDto savedMessageIdentifier = messageService.addOrUpdateMessage(messageDto);

        List<Message> messagedInDb = allMessages.getAll();
        assertMessage(asList(messageDto), messagedInDb);
        assertContentIdentifier(savedMessageIdentifier, messagedInDb.get(0));
    }

    private void assertMessage(List<MessageDto> messageDtos, List<Message> messagesInDb) {
        assertEquals(messageDtos.size(), messagesInDb.size());

        for (int i = 0; i < messageDtos.size(); i++) {
            Message messageInDb = messagesInDb.get(i);
            assertEquals(messageDtos.get(i).getName(), messageInDb.getName());
        }
    }
}
