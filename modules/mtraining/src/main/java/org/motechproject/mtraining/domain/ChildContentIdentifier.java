package org.motechproject.mtraining.domain;

import java.util.UUID;

/**
 * Object identifying a child element of a content using {@link ChildContentIdentifier#contentId} and {@link ChildContentIdentifier#version} fields.
 * Any content object which has children will have a list of this object.
 * For e.g., a {@link Course} will have a list, each representing a {@link Module}, by fields having values as contentId and version of the module.
 */

public class ChildContentIdentifier {
    private UUID contentId;
    private Integer version;

    ChildContentIdentifier() {
    }

    public ChildContentIdentifier(UUID contentId, Integer version) {
        this.contentId = contentId;
        this.version = version;
    }

    public UUID getContentId() {
        return contentId;
    }

    public Integer getVersion() {
        return version;
    }
}
