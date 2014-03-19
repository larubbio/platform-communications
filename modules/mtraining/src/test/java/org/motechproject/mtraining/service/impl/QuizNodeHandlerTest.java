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
import org.motechproject.mtraining.builder.QuestionContentBuilder;
import org.motechproject.mtraining.builder.QuizContentBuilder;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllQuizes;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import java.util.Arrays;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuizNodeHandlerTest {
    private static final Integer DEFAULT_VERSION = 1;

    @InjectMocks
    private QuizNodeHandler quizNodeHandler = new QuizNodeHandler();

    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllQuizes allQuizes;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateGivenQuizDtoAndThrowExceptionIfInvalid() {
        QuizDto quizDto = new QuizContentBuilder().withPassPercentage(100.0).buildQuizDTO();
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateQuiz(quizDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid quiz: some validation error");

        quizNodeHandler.validateNodeData(quizDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenQuizDtoIsValid() {
        QuestionDto questionDto = new QuestionContentBuilder().buildQuestionDTO();
        QuizDto quizDto = new QuizContentBuilder().withPassPercentage(100.0).withQuestionDTOs(asList(questionDto)).buildQuizDTO();
        when(courseStructureValidator.validateQuiz(quizDto)).thenReturn(new CourseStructureValidationResponse(true));

        quizNodeHandler.validateNodeData(quizDto);
    }

    @Test
    public void shouldSaveTheGivenQuizDtoAsQuizEntityWithMessagesAndRaiseEvent() {
        QuestionDto questionDto = new QuestionContentBuilder().buildQuestionDTO();
        QuizDto quizDto = new QuizContentBuilder().withPassPercentage(100.0).withQuestionDTOs(asList(questionDto)).buildQuizDTO();

        Node question1 = new Node(NodeType.QUESTION, new QuestionDto());
        Question expectedQuestionForTheQuiz = new QuestionContentBuilder().withOptions(Arrays.asList("1", "2")).buildQuestion();
        question1.setPersistentEntity(expectedQuestionForTheQuiz);
        Node question2 = new Node(NodeType.QUESTION, new QuestionDto());
        Node quizNode = new Node(NodeType.CHAPTER, quizDto, asList(question1, question2));

        quizNodeHandler.saveAndRaiseEvent(quizNode);

        InOrder inOrder = inOrder(allQuizes, eventRelay);
        ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);
        inOrder.verify(allQuizes).add(quizArgumentCaptor.capture());
        Quiz savedQuiz = quizArgumentCaptor.getValue();
        assertQuizDetails(quizDto, expectedQuestionForTheQuiz, savedQuiz);
        assertDefaultQuizIdentifierDetails(savedQuiz);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedQuiz, raisedEvent);
    }

    @Test
    public void shouldGetLatestVersionOfExistingQuizAndSaveTheNewQuizWithSameContentId_WhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        QuestionDto questionDto = new QuestionContentBuilder().buildQuestionDTO();

        QuizDto quizDto = new QuizContentBuilder().withContentId(contentId).withVersion(1).withPassPercentage(100.0).withQuestionDTOs(asList(questionDto)).buildQuizDTO();
        Node question1 = new Node(NodeType.QUESTION, new QuestionDto());
        Question expectedQuestionForTheQuiz = new QuestionContentBuilder().withOptions(Arrays.asList("1", "2")).buildQuestion();
        question1.setPersistentEntity(expectedQuestionForTheQuiz);
        Node question2 = new Node(NodeType.QUESTION, new QuestionDto());
        Node quizNode = new Node(NodeType.CHAPTER, quizDto, asList(question1, question2));
        Quiz existingQuizWithLatestVersion = new QuizContentBuilder().withContentId(contentId).withVersion(2).buildQuiz();
        when(allQuizes.getLatestVersionByContentId(contentId)).thenReturn(existingQuizWithLatestVersion);

        quizNodeHandler.saveAndRaiseEvent(quizNode);

        ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);
        verify(allQuizes).add(quizArgumentCaptor.capture());
        Quiz savedQuiz = quizArgumentCaptor.getValue();
        assertQuizDetails(quizDto, expectedQuestionForTheQuiz, savedQuiz);
        assertQuizIdentifierUpdateDetails(existingQuizWithLatestVersion, savedQuiz);
    }

    private void assertQuizDetails(QuizDto quizDto, Question expectedQuestion, Quiz savedQuiz) {
        assertEquals(quizDto.getNoOfQuestionsToBePlayed(), savedQuiz.getNumberOfQuizQuestionsToBePlayed());
        assertEquals(quizDto.isActive(), savedQuiz.isActive());
        assertEquals(1, savedQuiz.getQuestions().size());
        assertEquals(expectedQuestion.getContentId(), savedQuiz.getQuestions().get(0).getContentId());
        assertEquals(expectedQuestion.getVersion(), savedQuiz.getQuestions().get(0).getVersion());
    }

    private void assertDefaultQuizIdentifierDetails(Quiz savedQuiz) {
        assertNotNull(savedQuiz.getContentId());
        assertEquals(DEFAULT_VERSION, savedQuiz.getVersion());
    }

    private void assertQuizIdentifierUpdateDetails(Quiz existingQuiz, Quiz savedQuiz) {
        assertEquals(existingQuiz.getContentId(), savedQuiz.getContentId());
        assertEquals(existingQuiz.getVersion() + 1, savedQuiz.getVersion().intValue());
    }

    private void assertEventDetails(Quiz savedQuiz, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.QUIZ_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(2, raisedEvent.getParameters().size());
        assertEquals(savedQuiz.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.CONTENT_ID));
        assertEquals(savedQuiz.getVersion(), raisedEvent.getParameters().get(MTrainingEventConstants.VERSION));
    }
}
