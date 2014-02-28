package org.motechproject.mtraining.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Chapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Chapter} couch document
 */

@Repository
public class AllChapters extends MotechBaseRepository<Chapter> {

    @Autowired
    public AllChapters(@Qualifier("mtrainingDbConnector") CouchDbConnector db) {
        super(Chapter.class, db);
        initStandardDesignDocument();
    }
}
