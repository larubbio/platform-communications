package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.QuestionContentBuilder;
import org.motechproject.mtraining.builder.QuizContentBuilder;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.dto.AnswerSheetDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuizAnswerSheetDto;
import org.motechproject.mtraining.dto.QuizResultSheetDto;
import org.motechproject.mtraining.exception.InactiveContentException;
import org.motechproject.mtraining.exception.InvalidQuestionException;
import org.motechproject.mtraining.exception.InvalidQuizException;
import org.motechproject.mtraining.repository.AllQuizes;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class QuizServiceImplTest {

    private QuestionServiceImpl questionService;
    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private AllQuizes allQuizes;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private QuizServiceImpl quizService;

    @Before
    public void setUp() throws Exception {
        quizService = new QuizServiceImpl(nodeHandlerOrchestrator, allQuizes, questionService);

    }

    @Test
    public void getRandomisedQuestionsForAQuiz() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        Question question1 = new QuestionContentBuilder().withName("Question1").buildQuestion();
        Question question2 = new QuestionContentBuilder().withName("Question2").asInactive().buildQuestion();
        Question question3 = new QuestionContentBuilder().withName("Question3").buildQuestion();
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(new QuizContentBuilder().
                withContentId(quizIdentifier.getContentId())
                .withVersion(quizIdentifier.getVersion())
                .withQuestions(newArrayList(question1,
                        question2,
                        question3))
                .withNoOfQuizQuestions(2)
                .buildQuiz());

        List<ContentIdentifierDto> questionsForQuiz = quizService.getQuestionsForQuiz(quizIdentifier);

        assertEquals(2, questionsForQuiz.size());
        assertTrue(questionsForQuiz.contains(new ContentIdentifierDto(question1.getContentId(), question1.getVersion())));
        assertTrue(questionsForQuiz.contains(new ContentIdentifierDto(question3.getContentId(), question3.getVersion())));
    }

    @Test
    public void shouldThrowInvalidQuizExceptionIfQuizNotAvailable() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        UUID quizId = quizIdentifier.getContentId();
        Integer quizVersion = quizIdentifier.getVersion();
        when(allQuizes.findBy(quizId, quizVersion)).thenReturn(null);

        expectedException.expect(InvalidQuizException.class);
        quizService.getQuestionsForQuiz(quizIdentifier);
    }

    @Test
    public void shouldThrowIllegalStateExceptionIfQuestionsNotAvailableForQuiz() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        UUID quizId = quizIdentifier.getContentId();
        Integer quizVersion = quizIdentifier.getVersion();
        Question question2 = new QuestionContentBuilder().withName("Question1").asInactive().buildQuestion();
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(new QuizContentBuilder().
                withContentId(quizIdentifier.getContentId())
                .withVersion(quizIdentifier.getVersion())
                .withQuestions(newArrayList(question2))
                .buildQuiz());

        expectedException.expect(IllegalStateException.class);
        expectedException.expectMessage("The quiz with id: " + quizId + " and version: " + quizVersion + " does not contain required no of active questions");

        quizService.getQuestionsForQuiz(quizIdentifier);
    }

    @Test
    public void shouldThrowInactiveContentExceptionIfAttemptIsMadeToGetQuestionsFromAQuizIsNotActive() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        Quiz inactiveQuiz = new Quiz(false, "quiz", "aud01", Collections.<Question>emptyList(), 1, 100d, "Aut");

        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(inactiveQuiz);

        expectedException.expect(InactiveContentException.class);

        quizService.getQuestionsForQuiz(quizIdentifier);
    }

    @Test
    public void shouldReturnScoreSheetResultWithPassedStatusIfScoreIsMoreThanPassPercentage() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        Question question1 = new QuestionContentBuilder().withName("Question1").buildQuestion();
        Question question2 = new QuestionContentBuilder().withName("Question2").buildQuestion();
        Question question3 = new QuestionContentBuilder().withName("Question3").buildQuestion();
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(new QuizContentBuilder().
                withContentId(quizIdentifier.getContentId())
                .withVersion(quizIdentifier.getVersion())
                .withQuestions(newArrayList(question1,
                        question2,
                        question3))
                .withNoOfQuizQuestions(2)
                .buildQuiz());
        AnswerSheetDto answerSheetDto1 = new AnswerSheetDto(question1.getContentId(), 1, "1");
        AnswerSheetDto answerSheetDto2 = new AnswerSheetDto(question2.getContentId(), 1, "1");
        QuizAnswerSheetDto quizAnswerSheetDto = new QuizAnswerSheetDto("externalId", quizIdentifier, newArrayList(answerSheetDto1, answerSheetDto2));
        QuizResultSheetDto result = quizService.gradeQuiz(quizAnswerSheetDto);

        assertTrue(result.isPassed());
        assertEquals(Double.valueOf(100.00), result.getScore());
        assertTrue(result.getQuestionResultDtos().get(0).isCorrect());
        assertTrue(result.getQuestionResultDtos().get(1).isCorrect());

    }

    @Test
    public void shouldReturnScoreSheetResultWithPassedAsFalseIfScoreIsLessThanPassPercentage() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        Question question1 = new QuestionContentBuilder().withName("Question1").buildQuestion();
        Question question2 = new QuestionContentBuilder().withName("Question2").buildQuestion();
        Question question3 = new QuestionContentBuilder().withName("Question3").buildQuestion();
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(new QuizContentBuilder().
                withContentId(quizIdentifier.getContentId())
                .withVersion(quizIdentifier.getVersion())
                .withQuestions(newArrayList(question1,
                        question2,
                        question3))
                .withNoOfQuizQuestions(3)
                .buildQuiz());
        AnswerSheetDto answerSheetDto1 = new AnswerSheetDto(question1.getContentId(), 1, "1");
        AnswerSheetDto answerSheetDto2 = new AnswerSheetDto(question2.getContentId(), 1, "2");
        AnswerSheetDto answerSheetDto3 = new AnswerSheetDto(question3.getContentId(), 1, "2");
        QuizAnswerSheetDto quizAnswerSheetDto = new QuizAnswerSheetDto("externalId", quizIdentifier, newArrayList(answerSheetDto1, answerSheetDto2, answerSheetDto3));
        QuizResultSheetDto result = quizService.gradeQuiz(quizAnswerSheetDto);

        assertFalse(result.isPassed());
        assertEquals(Double.valueOf(33.33), result.getScore());
        assertTrue(result.getQuestionResultDtos().get(0).isCorrect());
        assertFalse(result.getQuestionResultDtos().get(1).isCorrect());
        assertFalse(result.getQuestionResultDtos().get(2).isCorrect());

    }

    @Test(expected = InvalidQuizException.class)
    public void shouldThrowInvalidQuizExceptionIfQuizIdNotFound() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(null);

        QuizAnswerSheetDto quizAnswerSheetDto = new QuizAnswerSheetDto("externalId", quizIdentifier, Collections.EMPTY_LIST);
        quizService.gradeQuiz(quizAnswerSheetDto);
    }


    @Test(expected = InvalidQuestionException.class)
    public void shouldThrowExceptionIfQuestionIdNotFound() {
        ContentIdentifierDto quizIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
        Question question1 = new QuestionContentBuilder().withName("Question1").buildQuestion();
        Question question2 = new QuestionContentBuilder().withName("Question2").buildQuestion();
        Question question3 = new QuestionContentBuilder().withName("Question3").buildQuestion();
        when(allQuizes.findBy(quizIdentifier.getContentId(), quizIdentifier.getVersion())).thenReturn(new QuizContentBuilder().
                withContentId(quizIdentifier.getContentId())
                .withVersion(quizIdentifier.getVersion())
                .withQuestions(newArrayList(question1,
                        question2))
                .withNoOfQuizQuestions(3)
                .buildQuiz());
        AnswerSheetDto answerSheetDto1 = new AnswerSheetDto(question1.getContentId(), 1, "1");
        AnswerSheetDto answerSheetDto2 = new AnswerSheetDto(question2.getContentId(), 1, "2");
        AnswerSheetDto answerSheetDto3 = new AnswerSheetDto(question3.getContentId(), 1, "2");
        QuizAnswerSheetDto quizAnswerSheetDto = new QuizAnswerSheetDto("externalId", quizIdentifier, newArrayList(answerSheetDto1, answerSheetDto2, answerSheetDto3));
        quizService.gradeQuiz(quizAnswerSheetDto);
    }
}

