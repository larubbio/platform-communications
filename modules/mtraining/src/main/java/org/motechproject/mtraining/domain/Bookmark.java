package org.motechproject.mtraining.domain;

import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;
import org.joda.time.DateTime;
import org.motechproject.commons.couchdb.model.MotechBaseDataObject;
import org.motechproject.mtraining.util.JSONUtil;

/**
 * Couch document object representing a Bookmark content.
 */

@TypeDiscriminator("doc.type === 'Bookmark'")
public class Bookmark extends MotechBaseDataObject {

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

    @JsonProperty
    private DateTime dateModified;

    Bookmark() {
    }

    public Bookmark(String externalId, ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.dateModified = DateTime.now();
    }

    public Bookmark(String externalId, ContentIdentifier course, ContentIdentifier module, ContentIdentifier chapter, ContentIdentifier message, DateTime dateModified) {
        this.externalId = externalId;
        this.course = course;
        this.module = module;
        this.chapter = chapter;
        this.message = message;
        this.dateModified = dateModified;
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

    public DateTime getDateModified() {
        return dateModified;
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

    @Override
    public String toString() {
        return JSONUtil.toJsonString(this);
    }

    public boolean wasModifiedAfter(DateTime dateTime) {
        return dateModified.isAfter(dateTime);
    }
}
