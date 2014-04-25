package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.dto.MessageDto;

import java.util.UUID;

public class MessageContentBuilder {

    private String name = "Default Message Name";
    private String description = "Default Message Description";
    private boolean isActive = true;
    private String audioFileName = "hello.wav";
    private String createBy = "Message Author";
    private UUID contentId = null;
    private Integer version = -1;


    public MessageContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public MessageContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }


    public MessageContentBuilder withAudioFile(String audioFileName) {
        this.audioFileName = audioFileName;
        return this;
    }

    public MessageContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public MessageContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public MessageContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public MessageContentBuilder createBy(String createdBy) {
        this.createBy = createdBy;
        return this;
    }

    public Message buildMessage() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new Message(isActive, name, description, audioFileName, createBy);
        }
        return new Message(contentId, version, isActive, name, description, audioFileName, createBy);
    }

    public MessageDto buildMessageDTO() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new MessageDto(isActive, name, description, audioFileName, createBy);
        }
        return new MessageDto(contentId, version, isActive, name, description, audioFileName, createBy);
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
