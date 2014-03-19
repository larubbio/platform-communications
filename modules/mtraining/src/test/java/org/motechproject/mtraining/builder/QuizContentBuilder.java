package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuizContentBuilder {

    private Double passPercentage = 100.0;
    private String name;
    private boolean isActive = true;
    private String externalId = "Quiz external Id";
    private String createdBy = "Quiz Author";
    private UUID contentId = null;
    private Integer version = -1;
    private Integer numberOfQuizQuestions = 0;
    private List<Question> questions = Collections.emptyList();
    private List<QuestionDto> questionDtos = Collections.emptyList();

    public QuizContentBuilder() {
    }


    public QuizContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public QuizContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public QuizContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public QuizContentBuilder withNoOfQuizQuestions(Integer numberOfQuizQuestions) {
        this.numberOfQuizQuestions = numberOfQuizQuestions;
        return this;
    }

    public QuizContentBuilder withPassPercentage(Double passPercentage) {
        this.passPercentage = passPercentage;
        return this;
    }

    public QuizContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public QuizContentBuilder createBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public QuizContentBuilder withQuestions(List<Question> questions) {
        this.questions = questions;
        return this;
    }

    public QuizContentBuilder withQuestionDTOs(List<QuestionDto> questionDTOList) {
        this.questionDtos = questionDTOList;
        return this;
    }

    public Quiz buildQuiz() {
        checkContentIdAndVersion();
        numberOfQuizQuestions = numberOfQuizQuestions == 0 ? getNumberOfQuestions() : numberOfQuizQuestions;
        if (contentId == null || version < 0) {
            return new Quiz(isActive, name, externalId, questions, numberOfQuizQuestions, passPercentage, createdBy);
        }
        return new Quiz(contentId, version, isActive, name, externalId, questions, numberOfQuizQuestions, passPercentage, createdBy);
    }

    public QuizDto buildQuizDTO() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new QuizDto(isActive, name, externalId, questionDtos, getNumberOfQuestionDTOs(), passPercentage, createdBy);
        }
        return new QuizDto(contentId, version, isActive, name, externalId, questionDtos, getNumberOfQuestionDTOs(), passPercentage, createdBy);
    }


    private Integer getNumberOfQuestionDTOs() {
        return questionDtos.size();
    }

    private Integer getNumberOfQuestions() {
        return questions.size();
    }


    private void checkContentIdAndVersion() {
        boolean illegalState = false;
        if (version < 0 && contentId != null) {
            illegalState = true;
        } else if (version >= 0 && contentId == null) {
            illegalState = true;
        }
        if (illegalState) {
            throw new IllegalStateException(String.format("Both version and contentId id need to be supplied or both should be left null. As of now contentId %s and version %s", contentId, version));
        }
    }
}
