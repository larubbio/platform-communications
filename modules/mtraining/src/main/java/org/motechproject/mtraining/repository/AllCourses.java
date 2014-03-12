package org.motechproject.mtraining.repository;

import org.ektorp.CouchDbConnector;
import org.ektorp.support.View;
import org.motechproject.mtraining.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * Repository for {@link Course} couch document
 */

@Repository
public class AllCourses extends AllContents<Course> {

    @Autowired
    public AllCourses(@Qualifier("mtrainingDbConnector") CouchDbConnector db) {
        super(Course.class, db);
        initStandardDesignDocument();
    }

    @View(name = "by_contentId_and_version", map = "function(doc) { if (doc.type ==='Course') { emit([doc.contentId,doc.version], doc._id); }}")
    public Course findBy(UUID contentId, Integer version) {
        return queryContentView("by_contentId_and_version", contentId, version);
    }

}
