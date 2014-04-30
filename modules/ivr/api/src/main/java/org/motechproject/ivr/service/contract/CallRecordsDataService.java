package org.motechproject.ivr.service.contract;

import org.joda.time.DateTime;
import org.motechproject.commons.api.Range;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.annotations.LookupField;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.util.QueryParams;

import java.util.List;
import java.util.Set;

/**
 * Created by frank on 4/30/14.
 */
public interface CallRecordsDataService extends MotechDataService<CallDetailRecord> {


    /*
    //NO CHECKSTYLE ParameterNumber
    String phoneNumber,
    DateTime startFromTime,
    DateTime startToTime,
    DateTime answerFromTime,
    DateTime answerToTime,
    DateTime endFromTime,
    DateTime endToTime,
    Integer minDurationInSeconds,
    Integer maxDurationInSeconds,
    List<String> dispositions,
    List<String> directions,
    String sortBy,
    boolean reverse)
    */

    //TODO: use Range instead of separate From/To fields

    @Lookup
    List<CallDetailRecord> findByCriteria(
        @LookupField(name = "phoneNumber") String phoneNumber, //NO CHECKSTYLE ParameterNumber
        @LookupField(name = "startFromTime") DateTime startFromTime,
        @LookupField(name = "startToTime") DateTime startToTime,
        @LookupField(name = "answerFromTime") DateTime answerFromTime,
        @LookupField(name = "answerToTime") DateTime answerToTime,
        @LookupField(name = "endFromTime") DateTime endFromTime,
        @LookupField(name = "endToTime") DateTime endToTime,
        @LookupField(name = "minDurationInSeconds") Integer minDurationInSeconds,
        @LookupField(name = "maxDurationInSeconds") Integer maxDurationInSeconds,
        @LookupField(name = "dispositions") Set<CallDisposition> dispositions,
        @LookupField(name = "directions") Set<CallDirection> directions,
        QueryParams queryParams
    );

}
