package org.motechproject.mtraining.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository for {@link Course} couch document
 */

@Repository
public class AllCourses extends MotechBaseRepository<Course> {

    @Autowired
    public AllCourses(@Qualifier("mtrainingDbConnector") CouchDbConnector db) {
        super(Course.class, db);
        initStandardDesignDocument();
    }

    @View(name = "by_contentId_and_version", map = "function(doc) { if (doc.type ==='Course') { emit([doc.contentId,doc.version], doc._id); }}")
    public Course findBy(UUID contentId, Integer version) {
        if (contentId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_contentId_and_version").key(ComplexKey.of(contentId, version)).includeDocs(true);
        List<Course> resultSet = db.queryView(viewQuery, Course.class);
        return singleResult(resultSet);
    }

}
