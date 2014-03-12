package org.motechproject.mtraining.repository;

import org.ektorp.ComplexKey;
import org.ektorp.CouchDbConnector;
import org.ektorp.ViewQuery;
import org.ektorp.support.GenerateView;
import org.motechproject.commons.couchdb.dao.MotechBaseRepository;
import org.motechproject.mtraining.domain.Content;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

public abstract class AllContents<T extends Content> extends MotechBaseRepository<T> {

    private Class<T> type;

    public AllContents(Class<T> type, CouchDbConnector db) {
        super(type, db);
        this.type = type;
        initStandardDesignDocument();
    }

    protected T queryContentView(String viewName, UUID contentId, Integer version) {
        if (contentId == null) {
            return null;
        }
        ViewQuery viewQuery = createQuery(viewName).key(ComplexKey.of(contentId, version)).includeDocs(true);
        return singleResult(db.queryView(viewQuery, type));
    }

    @GenerateView
    public List<T> findByContentId(UUID contentId) {
        if (contentId == null) {
            return Collections.EMPTY_LIST;
        }
        return queryView("by_contentId", contentId.toString());
    }
}
