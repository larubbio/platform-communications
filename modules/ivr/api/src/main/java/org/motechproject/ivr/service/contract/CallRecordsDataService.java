package org.motechproject.ivr.service.contract;

import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallRecordSearchParameters;

import java.util.List;

public interface CallRecordsDataService {
    List<CallDetailRecord> search(CallRecordSearchParameters callLogSearchParameters);

    long count(CallRecordSearchParameters callLogSearchParameters);

    List<String> getAllPhoneNumbers();

    int findMaxCallDuration();
}
