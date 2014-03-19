package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Quiz;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.QuizDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChapterContentBuilder {

    private String name = "Default Chapter Name";
    private String description = "Default Chapter Description";
    private String externalId = "Default external Id";
    private boolean isActive = true;
    private String createBy = "Chapter Author";
    private UUID contentId = null;

    private Integer version = -1;

    private QuizDto quizDto;

    private Quiz quiz = null;

    private List<Message> messages = new ArrayList<>();
    private List<MessageDto> messageDtos = new ArrayList<>();


    public ChapterContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public ChapterContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public ChapterContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ChapterContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ChapterContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public ChapterContentBuilder createBy(String createdBy) {
        this.createBy = createdBy;
        return this;
    }

    public ChapterContentBuilder withMessages(List<Message> messages) {
        this.messages.clear();
        this.messages.addAll(messages);
        return this;
    }

    public ChapterContentBuilder withMessageDTOs(List<MessageDto> messageDTOs) {
        this.messageDtos.clear();
        this.messageDtos.addAll(messageDTOs);
        return this;
    }

    public ChapterContentBuilder withQuizDTOs(QuizDto quizDTO) {
        this.quizDto = quizDTO;
        return this;
    }


    public Chapter buildChapter() {
        checkContentIdAndVersion();
        List<Message> messageList = CollectionUtils.copy(this.messages);
        if (contentId == null || version < 0) {
            return new Chapter(isActive, name, description, externalId, createBy, messageList, quiz);
        }
        return new Chapter(contentId, version, isActive, name, description, externalId, createBy, messageList, quiz);
    }

    public ChapterDto buildChapterDTO() {
        checkContentIdAndVersion();
        List<MessageDto> messageDtosList = CollectionUtils.copy(messageDtos);
        if (contentId == null || version < 0) {
            return new ChapterDto(isActive, name, description, externalId, createBy, messageDtosList, quizDto);
        }
        return new ChapterDto(contentId, version, isActive, name, description, externalId, createBy, messageDtosList, quizDto);
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
