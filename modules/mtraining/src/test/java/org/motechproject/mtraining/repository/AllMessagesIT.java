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
    private static final String COURSE_AUTHOR = "Course Admin";

    @Before
    @After
    public void after() {
        allMessages.removeAll();
    }

    @Test
    public void shouldGetMessagesByContentId() {
        UUID contentId = UUID.randomUUID();
        Message existingMessage1 = new Message(contentId, 1, true, "messageName", "desc1", "externalId1", COURSE_AUTHOR);
        Message existingMessage2 = new Message(contentId, 2, true, "messageName", "desc2", "externalId2", COURSE_AUTHOR);
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

    @Test
    public void shouldGetLatestVersionByContentId() {
        UUID contentId = UUID.randomUUID();
        Message messageWithOldVersion = new Message(contentId, 1, true, "name", "description", "fileName", COURSE_AUTHOR);
        Message messageWithLatestVersion = new Message(contentId, 2, true, "name", "description", "fileName", COURSE_AUTHOR);
        allMessages.add(messageWithOldVersion);
        allMessages.add(messageWithLatestVersion);

        Message latestVersionFromDb = allMessages.getLatestVersionByContentId(contentId);

        assertEquals(contentId, latestVersionFromDb.getContentId());
        assertEquals(messageWithLatestVersion.getVersion(), latestVersionFromDb.getVersion());
    }

    private void assertMessageDetails(Message existingMessage, Message actualMessage) {
        assertEquals(existingMessage.getContentId(), actualMessage.getContentId());
        assertEquals(existingMessage.getVersion(), actualMessage.getVersion());
        assertEquals(existingMessage.getName(), actualMessage.getName());
        assertEquals(existingMessage.getExternalContentId(), actualMessage.getExternalContentId());
        assertEquals(existingMessage.getDescription(), actualMessage.getDescription());
    }
}
