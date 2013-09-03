package org.motechproject.sms.osgi;

import org.junit.After;
import org.junit.Before;
import org.motechproject.sms.model.Sms;
import org.motechproject.sms.service.SmsSenderService;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class SmsBundleIT extends BaseOsgiIT {



    @Before
    public void onSetUp() {
    }

    public void testSmsService() throws IOException {

        SmsSenderService smsService = (SmsSenderService) applicationContext.getBean("smsSenderService");

        smsService.send(new Sms("from@from.com", "to@to.com", "test", "test"));

        //do test the service, dude!!! assertEquals("test", actualText.trim());

    }

    @After
    public void onTearDown() {
    }


    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.event.listener"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testblueprint.xml"};
    }
}
