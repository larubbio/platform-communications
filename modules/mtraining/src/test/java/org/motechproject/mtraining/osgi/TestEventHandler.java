package org.motechproject.mtraining.osgi;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.mtraining.constants.MTrainingEventConstants;

public class TestEventHandler {
    private boolean eventRaised;

    @MotechListener(subjects = MTrainingEventConstants.COURSE_CREATION_EVENT)
    public void handle(MotechEvent event) {
        eventRaised = true;
    }

    public boolean isEventRaised() {
        return eventRaised;
    }
}
