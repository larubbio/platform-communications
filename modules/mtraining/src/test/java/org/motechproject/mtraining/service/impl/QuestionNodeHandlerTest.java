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
import org.motechproject.mtraining.builder.QuestionContentBuilder;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllQuestions;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import java.util.UUID;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuestionNodeHandlerTest {
    private static final Integer DEFAULT_VERSION = 1;
    @InjectMocks
    private QuestionNodeHandler questionNodeHandler = new QuestionNodeHandler();

    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllQuestions allQuestions;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private QuestionContentBuilder questionContentBuilder;

    @Before
    public void setUp() throws Exception {
        questionContentBuilder = new QuestionContentBuilder();
    }

    @Test
    public void shouldValidateGivenQuestionDtoAndThrowExceptionIfInvalid() {
        QuestionDto questionDto = questionContentBuilder.buildQuestionDTO();
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateQuestion(questionDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid question: some validation error");

        questionNodeHandler.validateNodeData(questionDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenQuestionDtoIsValid() {
        QuestionDto questionDto = questionContentBuilder.buildQuestionDTO();
        when(courseStructureValidator.validateQuestion(questionDto)).thenReturn(new CourseStructureValidationResponse(true));

        questionNodeHandler.validateNodeData(questionDto);
    }

    @Test
    public void shouldSaveTheGivenQuestionDtoAsQuestionEntityAndRaiseEvent() {
        QuestionDto questionDto = questionContentBuilder.buildQuestionDTO();
        Node questionNode = new Node(NodeType.QUESTION, questionDto);

        questionNodeHandler.saveAndRaiseEvent(questionNode);

        InOrder inOrder = inOrder(allQuestions, eventRelay);
        ArgumentCaptor<Question> questionArgumentCaptor = ArgumentCaptor.forClass(Question.class);
        inOrder.verify(allQuestions).add(questionArgumentCaptor.capture());
        Question savedQuestion = questionArgumentCaptor.getValue();
        assertQuestionDetails(questionDto, savedQuestion);
        assertDefaultQuestionIdentifierDetails(savedQuestion);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedQuestion, raisedEvent);
    }

    @Test
    public void shouldGetLatestVersionOfExistingQuestionAndSaveTheNewQuestionWithSameContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        QuestionDto questionDto = questionContentBuilder
                .withContentId(contentId)
                .withVersion(1)
                .buildQuestionDTO();
        Node questionNode = new Node(NodeType.QUESTION, questionDto);
        Question existingQuestionWithLatestVersion = questionContentBuilder
                .withContentId(contentId)
                .withVersion(2)
                .buildQuestion();
        when(allQuestions.getLatestVersionByContentId(contentId)).thenReturn(existingQuestionWithLatestVersion);

        questionNodeHandler.saveAndRaiseEvent(questionNode);

        ArgumentCaptor<Question> questionArgumentCaptor = ArgumentCaptor.forClass(Question.class);
        verify(allQuestions).add(questionArgumentCaptor.capture());
        Question savedQuestion = questionArgumentCaptor.getValue();
        assertQuestionDetails(questionDto, savedQuestion);
        assertQuestionIdentifierUpdateDetails(existingQuestionWithLatestVersion, savedQuestion);
    }

    private void assertQuestionDetails(QuestionDto questionDto, Question savedQuestion) {
        assertEquals(savedQuestion.getName(), questionDto.getName());
        assertEquals(savedQuestion.getDescription(), questionDto.getDescription());
        assertEquals(savedQuestion.getExternalContentId(), questionDto.getExternalContentId());
        assertEquals(questionDto.isActive(), savedQuestion.isActive());
    }

    private void assertDefaultQuestionIdentifierDetails(Question savedQuestion) {
        assertNotNull(savedQuestion.getContentId());
        assertEquals(DEFAULT_VERSION, savedQuestion.getVersion());
    }

    private void assertQuestionIdentifierUpdateDetails(Question existingQuestion, Question savedQuestion) {
        assertEquals(existingQuestion.getContentId(), savedQuestion.getContentId());
        assertEquals(existingQuestion.getVersion() + 1, savedQuestion.getVersion().intValue());
    }

    private void assertEventDetails(Question savedQuestion, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.QUESTION_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(2, raisedEvent.getParameters().size());
        assertEquals(savedQuestion.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.CONTENT_ID));
        assertEquals(savedQuestion.getVersion(), raisedEvent.getParameters().get(MTrainingEventConstants.VERSION));
    }
}
