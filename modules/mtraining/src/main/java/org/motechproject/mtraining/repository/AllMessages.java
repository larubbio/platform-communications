package org.motechproject.mtraining.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Message} couch document
 */

@Repository
public class AllMessages extends MotechBaseRepository<Message> {

    @Autowired
    public AllMessages(@Qualifier("mtrainingDbConnector") CouchDbConnector db) {
        super(Message.class, db);
        initStandardDesignDocument();
    }
}
