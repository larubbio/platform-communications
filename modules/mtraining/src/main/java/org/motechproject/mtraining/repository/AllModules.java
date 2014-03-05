package org.motechproject.mtraining.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.View;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Module;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.UUID;

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

    @View(name = "by_contentId_and_version", map = "function(doc) { if (doc.type ==='Module') { emit([doc.contentId,doc.version], doc._id); }}")
    public Module findBy(UUID contentId, Integer version) {
        if (contentId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery("by_contentId_and_version").key(ComplexKey.of(contentId, version)).includeDocs(true);
        return singleResult(db.queryView(viewQuery, Module.class));
    }

}
