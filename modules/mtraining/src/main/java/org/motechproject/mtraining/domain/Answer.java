package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Couch document object representing an Answer content.
 * This will be part of the question document and not reside as a separate entity.
 * + externalContentId : Id that points to an external file or resource that is associated with the answer.For eg. an audio file that is played to the enrollee
 * + correctOption : correct option for the question asked
 */
public class Answer {

    @JsonProperty
    private String externalContentId;

    @JsonProperty
    private String correctOption;

    public Answer() {
    }

    public Answer(String externalContentId, String correctOption) {
        this.externalContentId = externalContentId;
        this.correctOption = correctOption;
    }

    public String getExternalContentId() {
        return externalContentId;
    }

    public String getCorrectOption() {
        return correctOption;
    }
}
