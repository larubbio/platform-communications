package org.motechproject.mtraining.service.impl;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class MessageNodeHandlerTest {
    public static final Integer DEFAULT_VERSION = 1;
    @InjectMocks
    private MessageNodeHandler messageNodeHandler = new MessageNodeHandler();

    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllMessages allMessages;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateGivenMessageDtoAndThrowExceptionIfInvalid() {
        MessageDto messageDto = new MessageDto("name", "fileName", "description");
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateMessage(messageDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid message: some validation error");

        messageNodeHandler.validateNodeData(messageDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenMessageDtoIsValid() {
        MessageDto messageDto = new MessageDto("name", "fileName", "description");
        when(courseStructureValidator.validateMessage(messageDto)).thenReturn(new CourseStructureValidationResponse(true));

        messageNodeHandler.validateNodeData(messageDto);
    }

    @Test
    public void shouldSaveTheGivenMessageDtoAsMessageEntityAndRaiseEvent() {
        MessageDto messageDto = new MessageDto("name", "fileName", "description");
        Node messageNode = new Node(NodeType.MESSAGE, messageDto);

        messageNodeHandler.saveAndRaiseEvent(messageNode);

        InOrder inOrder = inOrder(allMessages, eventRelay);
        ArgumentCaptor<Message> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);
        inOrder.verify(allMessages).add(messageArgumentCaptor.capture());
        Message savedMessage = messageArgumentCaptor.getValue();
        assertMessageDetails(messageDto, savedMessage);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedMessage, raisedEvent);
    }

    private void assertMessageDetails(MessageDto messageDto, Message savedMessage) {
        assertEquals(savedMessage.getName(), messageDto.getName());
        assertEquals(savedMessage.getDescription(), messageDto.getDescription());
        assertEquals(savedMessage.getExternalId(), messageDto.getExternalId());
        assertNotNull(savedMessage.getContentId());
        assertEquals(DEFAULT_VERSION, savedMessage.getVersion());
    }

    private void assertEventDetails(Message savedMessage, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.MESSAGE_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(1, raisedEvent.getParameters().size());
        assertEquals(savedMessage.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.NODE_ID));
    }
}
