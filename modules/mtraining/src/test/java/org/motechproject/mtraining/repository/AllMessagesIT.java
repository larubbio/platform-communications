package org.motechproject.mtraining.repository;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;
import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllMessagesIT {

    @Autowired
    private AllContents<Message> allMessages;

    @Before
    @After
    public void after() {
        allMessages.removeAll();
    }

    @Test
    public void shouldGetMessagesByContentId() {
        UUID contentId = UUID.randomUUID();
        Message existingMessage1 = new Message(contentId, 1, true, "messageName", "externalId1", "desc1");
        Message existingMessage2 = new Message(contentId, 2, true, "messageName", "externalId2", "desc2");
        allMessages.add(existingMessage1);
        allMessages.add(existingMessage2);

        List<Message> messagesByContentId = allMessages.findByContentId(contentId);

        assertEquals(2, messagesByContentId.size());
        assertMessageDetails(existingMessage1, messagesByContentId.get(0));
        assertMessageDetails(existingMessage2, messagesByContentId.get(1));
    }

    @Test
    public void shouldReturnEmptyListIfThereAreNoMessagesByContentId() {
        List<Message> messagesByContentId = allMessages.findByContentId(UUID.randomUUID());

        assertTrue(messagesByContentId.isEmpty());
    }

    private void assertMessageDetails(Message existingMessage, Message actualMessage) {
        assertEquals(existingMessage.getContentId(), actualMessage.getContentId());
        assertEquals(existingMessage.getVersion(), actualMessage.getVersion());
        assertEquals(existingMessage.getName(), actualMessage.getName());
        assertEquals(existingMessage.getExternalId(), actualMessage.getExternalId());
        assertEquals(existingMessage.getDescription(), actualMessage.getDescription());
    }
}
