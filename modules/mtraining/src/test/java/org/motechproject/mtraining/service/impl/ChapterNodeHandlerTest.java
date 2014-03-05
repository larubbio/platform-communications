package org.motechproject.mtraining.service.impl;

import org.junit.Before;
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
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import java.util.Collections;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChapterNodeHandlerTest {
    @InjectMocks
    private ChapterNodeHandler chapterNodeHandler = new ChapterNodeHandler();

    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllChapters allChapters;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private ContentIdentifierDto chapterIdentifier;

    @Before
    public void setUp() throws Exception {
        chapterIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldValidateGivenChapterDtoAndThrowExceptionIfInvalid() {
        ChapterDto chapterDto = new ChapterDto("name", "description", chapterIdentifier, Collections.EMPTY_LIST);
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateChapter(chapterDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid chapter: some validation error");

        chapterNodeHandler.validateNodeData(chapterDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenChapterDtoIsValid() {
        ChapterDto chapterDto = new ChapterDto("name", "description", chapterIdentifier, asList(new MessageDto()));
        when(courseStructureValidator.validateChapter(chapterDto)).thenReturn(new CourseStructureValidationResponse(true));

        chapterNodeHandler.validateNodeData(chapterDto);
    }

    @Test
    public void shouldSaveTheGivenChapterDtoAsChapterEntityWithMessagesAndRaiseEvent() {
        ChapterDto chapterDto = new ChapterDto("name", "description", chapterIdentifier, asList(new MessageDto()));
        Node messageNode1 = new Node(NodeType.MESSAGE, new MessageDto());
        Message expectedMessageForTheChapter = new Message("", "", "");
        messageNode1.setPersistentEntity(expectedMessageForTheChapter);
        Node messageNode2 = new Node(NodeType.MESSAGE, new MessageDto());
        Node chapterNode = new Node(NodeType.CHAPTER, chapterDto, asList(messageNode1, messageNode2));

        chapterNodeHandler.saveAndRaiseEvent(chapterNode);

        InOrder inOrder = inOrder(allChapters, eventRelay);
        ArgumentCaptor<Chapter> chapterArgumentCaptor = ArgumentCaptor.forClass(Chapter.class);
        inOrder.verify(allChapters).add(chapterArgumentCaptor.capture());
        Chapter savedChapter = chapterArgumentCaptor.getValue();
        assertChapterDetails(chapterDto, expectedMessageForTheChapter, savedChapter);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedChapter, raisedEvent);
    }

    private void assertChapterDetails(ChapterDto chapterDto, Message expectedMessage, Chapter savedChapter) {
        assertEquals(savedChapter.getName(), chapterDto.getName());
        assertEquals(savedChapter.getDescription(), chapterDto.getDescription());
        assertEquals(1, savedChapter.getMessages().size());
        assertEquals(expectedMessage.getContentId(), savedChapter.getMessages().get(0).getContentId());
        assertEquals(expectedMessage.getVersion(), savedChapter.getMessages().get(0).getVersion());
        assertNotNull(savedChapter.getContentId());
        assertEquals(chapterIdentifier.getVersion(), savedChapter.getVersion());
    }

    private void assertEventDetails(Chapter savedChapter, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.CHAPTER_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(2, raisedEvent.getParameters().size());
        assertEquals(savedChapter.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.CONTENT_ID));
        assertEquals(savedChapter.getVersion(), raisedEvent.getParameters().get(MTrainingEventConstants.VERSION));
    }
}
