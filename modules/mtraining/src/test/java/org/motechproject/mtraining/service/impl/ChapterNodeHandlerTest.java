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
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ChapterNodeHandlerTest {
    private static final Integer DEFAULT_VERSION = 1;

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

    @Test
    public void shouldValidateGivenChapterDtoAndThrowExceptionIfInvalid() {
        ChapterDto chapterDto = new ChapterDto(true, "name", "description", Collections.EMPTY_LIST);
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateChapter(chapterDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid chapter: some validation error");

        chapterNodeHandler.validateNodeData(chapterDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenChapterDtoIsValid() {
        ChapterDto chapterDto = new ChapterDto(true, "name", "description", asList(new MessageDto()));
        when(courseStructureValidator.validateChapter(chapterDto)).thenReturn(new CourseStructureValidationResponse(true));

        chapterNodeHandler.validateNodeData(chapterDto);
    }

    @Test
    public void shouldSaveTheGivenChapterDtoAsChapterEntityWithMessagesAndRaiseEvent() {
        ChapterDto chapterDto = new ChapterDto(true, "name", "description", asList(new MessageDto()));
        Node messageNode1 = new Node(NodeType.MESSAGE, new MessageDto());
        Message expectedMessageForTheChapter = new Message(true, "", "", "");
        messageNode1.setPersistentEntity(expectedMessageForTheChapter);
        Node messageNode2 = new Node(NodeType.MESSAGE, new MessageDto());
        Node chapterNode = new Node(NodeType.CHAPTER, chapterDto, asList(messageNode1, messageNode2));

        chapterNodeHandler.saveAndRaiseEvent(chapterNode);

        InOrder inOrder = inOrder(allChapters, eventRelay);
        ArgumentCaptor<Chapter> chapterArgumentCaptor = ArgumentCaptor.forClass(Chapter.class);
        inOrder.verify(allChapters).add(chapterArgumentCaptor.capture());
        Chapter savedChapter = chapterArgumentCaptor.getValue();
        assertChapterDetails(chapterDto, expectedMessageForTheChapter, savedChapter);
        assertDefaultChapterIdentifierDetails(savedChapter);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedChapter, raisedEvent);
    }

    @Test
    public void shouldGetLatestVersionOfExistingChapterAndSaveTheNewChapterWithSameContentId_WhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        ChapterDto chapterDto = new ChapterDto(contentId, true, "name", "description", asList(new MessageDto()));
        Node messageNode1 = new Node(NodeType.MESSAGE, new MessageDto());
        Message expectedMessageForTheChapter = new Message(true, "", "", "");
        messageNode1.setPersistentEntity(expectedMessageForTheChapter);
        Node messageNode2 = new Node(NodeType.MESSAGE, new MessageDto());
        Node chapterNode = new Node(NodeType.CHAPTER, chapterDto, asList(messageNode1, messageNode2));
        Chapter existingChapterWithOldVersion = new Chapter(contentId, 1, true, "name", "description", Collections.EMPTY_LIST);
        Chapter existingChapterWithLatestVersion = new Chapter(contentId, 2, true, "name", "description", Collections.EMPTY_LIST);
        when(allChapters.findByContentId(contentId)).thenReturn(asList(existingChapterWithLatestVersion, existingChapterWithOldVersion));

        chapterNodeHandler.saveAndRaiseEvent(chapterNode);

        ArgumentCaptor<Chapter> chapterArgumentCaptor = ArgumentCaptor.forClass(Chapter.class);
        verify(allChapters).add(chapterArgumentCaptor.capture());
        Chapter savedChapter = chapterArgumentCaptor.getValue();
        assertChapterDetails(chapterDto, expectedMessageForTheChapter, savedChapter);
        assertChapterIdentifierUpdateDetails(existingChapterWithLatestVersion, savedChapter);
    }

    private void assertChapterDetails(ChapterDto chapterDto, Message expectedMessage, Chapter savedChapter) {
        assertEquals(chapterDto.getName(), savedChapter.getName());
        assertEquals(chapterDto.getDescription(), savedChapter.getDescription());
        assertEquals(chapterDto.isActive(), savedChapter.isActive());
        assertEquals(1, savedChapter.getMessages().size());
        assertEquals(expectedMessage.getContentId(), savedChapter.getMessages().get(0).getContentId());
        assertEquals(expectedMessage.getVersion(), savedChapter.getMessages().get(0).getVersion());
    }

    private void assertDefaultChapterIdentifierDetails(Chapter savedChapter) {
        assertNotNull(savedChapter.getContentId());
        assertEquals(DEFAULT_VERSION, savedChapter.getVersion());
    }

    private void assertChapterIdentifierUpdateDetails(Chapter existingChapter, Chapter savedChapter) {
        assertEquals(existingChapter.getContentId(), savedChapter.getContentId());
        assertEquals(existingChapter.getVersion() + 1, savedChapter.getVersion().intValue());
    }

    private void assertEventDetails(Chapter savedChapter, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.CHAPTER_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(2, raisedEvent.getParameters().size());
        assertEquals(savedChapter.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.CONTENT_ID));
        assertEquals(savedChapter.getVersion(), raisedEvent.getParameters().get(MTrainingEventConstants.VERSION));
    }
}
