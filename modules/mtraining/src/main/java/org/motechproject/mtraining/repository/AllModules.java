package org.motechproject.mtraining.repository;

import org.ektorp.CouchDbConnector;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * Repository for {@link Module} couch document
 */

@Repository
public class AllModules extends MotechBaseRepository<Module> {

    @Autowired
    public AllModules(@Qualifier("mtrainingDbConnector") CouchDbConnector db) {
        super(Module.class, db);
        initStandardDesignDocument();
    }
}
