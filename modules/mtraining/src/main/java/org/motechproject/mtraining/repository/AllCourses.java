package org.motechproject.mtraining.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Course;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

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
}
