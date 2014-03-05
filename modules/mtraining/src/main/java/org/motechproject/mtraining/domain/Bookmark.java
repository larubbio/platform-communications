package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

/**
 * Couch document object representing a Bookmark content.
 */

@TypeDiscriminator("doc.type === 'Bookmark'")
public class Bookmark extends Content {

    @JsonProperty
    private String externalId;

    @JsonProperty
    private ContentIdentifier course;

    @JsonProperty
    private ContentIdentifier module;

    @JsonProperty
    private ContentIdentifier chapter;

    @JsonProperty
    private ContentIdentifier message;

    Bookmark() {
    }

    public Bookmark(String externalId, ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
    }

    public String getExternalId() {
        return externalId;
    }

    public ContentIdentifier getCourse() {
        return course;
    }

    public ContentIdentifier getModule() {
        return module;
    }

    public ContentIdentifier getChapter() {
        return chapter;
    }

    public ContentIdentifier getMessage() {
        return message;
    }

    public void setCourse(ContentIdentifier course) {
        this.course = course;
    }

    public void update(ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message) {
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
    }
}
