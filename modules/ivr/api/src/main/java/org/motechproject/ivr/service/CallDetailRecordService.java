package org.motechproject.ivr.service;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;
import java.util.Set;

/**
 * TODO
 */
public interface CallDetailRecordService extends MotechDataService<CallDetailRecord> {

    @Lookup(name = "Find by Criteria")
    List<CallDetailRecord> findByCriteria(
            @LookupField(name = "phoneNumber") String phoneNumber, //NO CHECKSTYLE ParameterNumber
            @LookupField(name = "startDate") Range<DateTime> startDate,
            @LookupField(name = "answerDate") Range<DateTime> answerDate,
            @LookupField(name = "endDate") Range<DateTime> endDate,
            @LookupField(name = "duration") Range<Integer> duration,
            @LookupField(name = "disposition") Set<CallDisposition> disposition,
            @LookupField(name = "callDirection") Set<CallDirection> callDirection
    );

    @Lookup(name = "Count by Criteria")
    long countByCriteria(
            @LookupField(name = "phoneNumber") String phoneNumber, //NO CHECKSTYLE ParameterNumber
            @LookupField(name = "startDate") Range<DateTime> startDate,
            @LookupField(name = "answerDate") Range<DateTime> answerDate,
            @LookupField(name = "endDate") Range<DateTime> endDate,
            @LookupField(name = "duration") Range<Integer> duration,
            @LookupField(name = "disposition") Set<CallDisposition> disposition,
            @LookupField(name = "callDirection") Set<CallDirection> callDirection
    );

    @Lookup(name = "Find by Call ID")
    List<CallDetailRecord> findByCallId(@LookupField(name = "callId") String callId);

    @Lookup(name = "Count by Call ID")
    long countByCallId(@LookupField(name = "callId") String callId);
}
