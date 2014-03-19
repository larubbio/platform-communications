package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.QuizAnswerSheetDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.dto.QuizResultSheetDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to quizzes
 */
public interface QuizService {

    ContentIdentifierDto addOrUpdateQuiz(QuizDto quizDto);

    QuizDto getQuiz(ContentIdentifierDto quizIdentifier);

    List<QuizDto> getAllQuizes();

    List<ContentIdentifierDto> getQuestionsForQuiz(ContentIdentifierDto quizIdentifier);

    QuizResultSheetDto gradeQuiz(QuizAnswerSheetDto quizAnswerSheetDto);
}

