package org.motechproject.ivr.service;

import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.service.contract.CallDetailRecordDataService;
import org.motechproject.ivr.service.contract.CallRecordsDataService;
import org.motechproject.mds.util.Order;
import org.motechproject.mds.util.QueryParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO
 */
@Service("callRecordsDataService")
public class CallRecordsDataServiceImpl implements CallRecordsDataService {
    private CallDetailRecordDataService callDetailRecordDataService;

    @Autowired
    public CallRecordsDataServiceImpl(CallDetailRecordDataService callDetailRecordDataService) {
        this.callDetailRecordDataService = callDetailRecordDataService;
    }

    public List<CallDetailRecord> search(CallRecordSearchParameters callLogSearchParameters) {
        return this.callDetailRecordDataService.findByCriteria(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getStartTimeRange(), callLogSearchParameters.getAnswerTimeRange(),
                callLogSearchParameters.getEndTimeRange(), callLogSearchParameters.getDurationRange(),
                callLogSearchParameters.getDispositions(), callLogSearchParameters.getDirections());
    }

    public long count(CallRecordSearchParameters callLogSearchParameters) {
        return this.callDetailRecordDataService.countByCriteria(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getStartTimeRange(), callLogSearchParameters.getAnswerTimeRange(),
                callLogSearchParameters.getEndTimeRange(), callLogSearchParameters.getDurationRange(),
                callLogSearchParameters.getDispositions(), callLogSearchParameters.getDirections());
    }

    //TODO: this is horrible code, it must _never_ make it to production. Eew. Gross.
    public List<String> getAllPhoneNumbers() {
        List<CallDetailRecord> allCallDetailRecords = this.callDetailRecordDataService.retrieveAll();
        List<String> allPhoneNumbers = new ArrayList<>();
        for (CallDetailRecord callDetailRecord : allCallDetailRecords) {
            allPhoneNumbers.add(callDetailRecord.getPhoneNumber());
        }
        return allPhoneNumbers;
    }

    //TODO: this is slightly less horrible code, but it still must _never_ make it to production
    public int findMaxCallDuration() {
        int maxCallDuration = 0;
        Order order = new Order("duration", Order.Direction.DESC);
        QueryParams queryParams = new QueryParams(0, 1, order);
        List<CallDetailRecord> allCallDetailRecords = this.callDetailRecordDataService.retrieveAll(queryParams);
        if (allCallDetailRecords.size() > 0) {
            maxCallDuration = allCallDetailRecords.get(0).getDuration();
        }
        return maxCallDuration;
    }
}
