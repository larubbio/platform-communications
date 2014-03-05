package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;

import java.util.UUID;

/**
 * Base class which should be inherited by any object of the course structure domain and
 * defines the necessary fields that an object in a course structure should have.
 */

public abstract class Content extends MotechBaseDataObject {
    private static final int DEFAULT_VERSION = 1;

    @JsonProperty
    private UUID contentId;

    @JsonProperty
    private Integer version;

    public Content() {
        this.contentId = UUID.randomUUID();
        this.version = DEFAULT_VERSION;
    }

    public UUID getContentId() {
        return contentId;
    }

    public void setContentId(UUID contentId) {
        this.contentId = contentId;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public void incrementVersion() {
        version = version + 1;
    }
}
