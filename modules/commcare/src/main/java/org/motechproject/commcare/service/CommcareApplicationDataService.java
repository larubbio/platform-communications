package org.motechproject.commcare.service;

import org.motechproject.commcare.domain.CommcareApplication;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

public interface CommcareApplicationDataService extends MotechDataService<CommcareApplication> {

    @Lookup
    CommcareApplication byApplicationName(@LookupField(name = "applicationName") String applicationName);
}
