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

    //TODO: use Range instead of separate From/To fields

    @Lookup
    List<CallDetailRecord> findByCriteria(
            @LookupField(name = "phoneNumber") String phoneNumber, //NO CHECKSTYLE ParameterNumber
            @LookupField(name = "startTime") Range<DateTime> startTime,
            @LookupField(name = "answerTime") Range<DateTime> answerTime,
            @LookupField(name = "endTime") Range<DateTime> endTime,
            @LookupField(name = "durationInSeconds") Range<Integer> durationInSeconds,
            @LookupField(name = "dispositions") Set<CallDisposition> dispositions,
            @LookupField(name = "directions") Set<CallDirection> directions
    );

    @Lookup
    List<CallDetailRecord> findByCallId(@LookupField(name = "callId") String callId);

    @Lookup
    long countByCriteria(
            @LookupField(name = "phoneNumber") String phoneNumber, //NO CHECKSTYLE ParameterNumber
            @LookupField(name = "startTime") Range<DateTime> startTime,
            @LookupField(name = "answerTime") Range<DateTime> answerTime,
            @LookupField(name = "endTime") Range<DateTime> endTime,
            @LookupField(name = "durationInSeconds") Range<Integer> durationInSeconds,
            @LookupField(name = "dispositions") Set<CallDisposition> dispositions,
            @LookupField(name = "directions") Set<CallDirection> directions
    );
}
