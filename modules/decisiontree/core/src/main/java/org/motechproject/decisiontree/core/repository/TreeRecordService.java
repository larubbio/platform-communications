package org.motechproject.decisiontree.core.repository;

import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;


public interface TreeRecordService extends MotechDataService<TreeRecord> {

    @Lookup(name = "Find by Name")
    TreeRecord findByName(@LookupField(name = "name") String name);

}
