package org.motechproject.commcare.it;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.motechproject.commcare.osgi.CommcareBundleIT;
import org.motechproject.commcare.service.impl.CommcareApplicationDataServiceIT;

@RunWith(Suite.class)
@Suite.SuiteClasses({CommcareBundleIT.class, CommcareApplicationDataServiceIT.class})
public class CommcareIntegrationTests {
}
