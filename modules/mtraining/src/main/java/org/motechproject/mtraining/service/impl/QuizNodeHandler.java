package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.QuizDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllQuizes;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of abstract class {@link org.motechproject.mtraining.service.impl.NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#QUIZ}
 */

@Component
public class QuizNodeHandler extends NodeHandler {

    private Logger logger = LoggerFactory.getLogger(QuizNodeHandler.class);

    @Autowired
    private AllQuizes allQuizes;

    @Override
    protected void validateNodeData(ContentDto nodeData) {
        QuizDto quizDto = (QuizDto) nodeData;
        CourseStructureValidationResponse validationResponse = validator().validateQuiz(quizDto);
        if (!validationResponse.isValid()) {
            String message = String.format("Invalid quiz: %s", validationResponse.getErrorMessage());
            logger.error(message);
            throw new CourseStructureValidationException(message);
        }
    }

    @Override
    protected Quiz saveAndRaiseEvent(Node node) {
        QuizDto quizDto = (QuizDto) node.getNodeData();
        Quiz quiz = quizWithQuestions(quizDto, getQuestions(node));
        allQuizes.add(quiz);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved quiz: %s", quiz.getContentId()));
        }
        sendEvent(MTrainingEventConstants.QUIZ_CREATION_EVENT, quiz.getContentId(), quiz.getVersion());
        return quiz;
    }

    private List<Question> getQuestions(Node node) {
        return getChildContentNodes(node, NodeType.QUESTION);
    }

    private Quiz quizWithQuestions(QuizDto quizDto, List<Question> questions) {
        UUID contentId = quizDto.getContentId();
        if (contentId == null) {
            return new Quiz(quizDto.isActive(), quizDto.getName(), quizDto.getExternalContentId(), questions, quizDto.getNoOfQuestionsToBePlayed(), quizDto.getPassPercentage(), quizDto.getCreatedBy());
        }
        Quiz existingQuiz = allQuizes.getLatestVersionByContentId(contentId);
        Quiz quizToSave = new Quiz(existingQuiz.getContentId(), existingQuiz.getVersion(), quizDto.isActive(), quizDto.getName(), quizDto.getExternalContentId(), questions, quizDto.getNoOfQuestionsToBePlayed(), quizDto.getPassPercentage(), quizDto.getCreatedBy());
        quizToSave.incrementVersion();
        return quizToSave;
    }
}
