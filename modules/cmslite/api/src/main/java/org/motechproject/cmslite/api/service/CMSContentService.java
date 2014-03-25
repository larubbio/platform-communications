package org.motechproject.cmslite.api.service;
/**
 * \defgroup cmslite CMS Lite
 * CMS Lite is lightweight content management supports multiple languages.
 */

import org.motechproject.cmslite.api.model.CMSContent;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * \ingroup cmslite
 * CMS Lite is lightweight content management based on couchdb storage. It supports storing and retrieving of stream / text along with
 * custom meta data for each language. Implementer can also use to export rest based
 * content retrieval.
 */
public interface CMSContentService extends MotechDataService<CMSContent> {
    @Lookup
    List<CMSContent> byNameAndLanguage(String name, String language);

}
