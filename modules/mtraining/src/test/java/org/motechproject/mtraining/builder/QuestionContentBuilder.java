package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Answer;
import org.motechproject.mtraining.domain.Question;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.QuestionDto;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class QuestionContentBuilder {

    private String name = "Default Question Name";
    private String description = "Default Question Name";
    private boolean isActive = true;
    private String externalId = "qaud02";
    private String createdBy = "Question Author";
    private UUID contentId = null;
    private Integer version = -1;
    private Answer answer = new Answer("exId", "1");
    private AnswerDto answerDTO = new AnswerDto("1", "exId");
    private List<String> options = Collections.emptyList();


    public QuestionContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public QuestionContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }


    public QuestionContentBuilder withExternalId(String externalId) {
        this.externalId = externalId;
        return this;
    }

    public QuestionContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public QuestionContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public QuestionContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public QuestionContentBuilder createBy(String createdBy) {
        this.createdBy = createdBy;
        return this;
    }

    public QuestionContentBuilder withOptions(List<String> options) {
        this.options = options;
        return this;
    }

    public Question buildQuestion() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new Question(isActive, name, description, externalId, answer, options, createdBy);
        }
        return new Question(contentId, version, isActive, name, description, description, answer, options, createdBy);
    }

    public QuestionDto buildQuestionDTO() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new QuestionDto(isActive, name, description, externalId, answerDTO, options, createdBy);
        }
        return new QuestionDto(contentId, version, isActive, name, description, description, answerDTO, options, createdBy);
    }

    public QuestionDto buildQuestionDTO(Question question) {
        return new QuestionDto(question.getContentId(), question.getVersion(), question.isActive(),
                question.getName(), question.getDescription(), question.getExternalContentId(),
                new AnswerDto(question.getAnswer().getCorrectOption(), question.getAnswer().getExternalContentId()),
                question.getOptions(), question.getCreatedBy());
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
