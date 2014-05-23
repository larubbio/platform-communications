package org.motechproject.ivr.domain;

import org.junit.Test;

import java.util.*;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CallDetailRecordTest {
    @Test
    public void addEventLog() {

        CallDetailRecord callDetailRecord = new CallDetailRecord("callID", "phoneNumber");
        callDetailRecord.addEventLog("foo");
        callDetailRecord.addEventLog("bar");
        callDetailRecord.addEventLog("baz");

        List<String> eventLog = callDetailRecord.getEventLog();

        assertEquals(3, eventLog.size());

        assertTrue(eventLog.get(0).endsWith("foo"));
        assertTrue(eventLog.get(1).endsWith("bar"));
        assertTrue(eventLog.get(2).endsWith("baz"));
    }
}
