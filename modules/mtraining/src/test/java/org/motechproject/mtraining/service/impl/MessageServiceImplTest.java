package org.motechproject.mtraining.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.repository.AllMessages;

import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageServiceImplTest {

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private AllMessages allMessages;

    @InjectMocks
    private MessageServiceImpl messageServiceImpl = new MessageServiceImpl();

    @Test
    public void shouldConstructMessageNodeAndInvokeHandler() {
        MessageDto messageDto = new MessageDto();

        messageServiceImpl.addOrUpdateMessage(messageDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualMessageNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.MESSAGE, actualMessageNode.getNodeType());
        assertEquals(messageDto, actualMessageNode.getNodeData());
        assertTrue(actualMessageNode.getChildNodes().isEmpty());
        assertNull(actualMessageNode.getPersistentEntity());
    }

    @Test
    public void shouldGetAllMessages() {
        Message message1 = new MessageContentBuilder().withName("messageName1").withAudioFile("fileName1").buildMessage();
        Message message2 = new MessageContentBuilder().withName("messageName2").withAudioFile("fileName2").buildMessage();
        when(allMessages.getAll()).thenReturn(asList(message1, message2));

        List<MessageDto> allMessageDtos = messageServiceImpl.getAllMessages();

        assertEquals(2, allMessageDtos.size());
        assertMessageDetails(message1, allMessageDtos.get(0));
        assertMessageDetails(message2, allMessageDtos.get(1));
    }

    @Test
    public void shouldReturnMessageDtoIfMessageFound() {
        Message message = new MessageContentBuilder().buildMessage();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allMessages.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(message);
        MessageDto messageFromDb = messageServiceImpl.getMessage(contentIdentifierDto);

        assertEquals(message.getContentId(), messageFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfMessageByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allMessages.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        MessageDto messageFromDb = messageServiceImpl.getMessage(contentIdentifierDto);

        assertNull(messageFromDb);
    }

    private void assertMessageDetails(Message message, MessageDto messageDto) {
        assertEquals(message.getContentId(), messageDto.getContentId());
        assertEquals(message.getVersion(), messageDto.getVersion());
        assertEquals(message.getName(), messageDto.getName());
        assertEquals(message.getExternalContentId(), messageDto.getExternalContentId());
        assertEquals(message.getDescription(), messageDto.getDescription());
    }
}