package org.motechproject.ivr.service;

import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallDirection;
import org.motechproject.ivr.domain.CallDisposition;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.service.contract.CallRecordsDataService;
import org.motechproject.ivr.service.contract.CallRecordsSearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides convenient methods for searching call records.
 */

@Service
public class CallRecordsSearchServiceImpl implements CallRecordsSearchService {
    private CallRecordsDataService callRecordsDataService;

    @Autowired
    public CallRecordsSearchServiceImpl(CallRecordsDataService callRecordsDataService) {
        this.callRecordsDataService = callRecordsDataService;
    }

    @Override
    public List<CallDetailRecord> search(CallRecordSearchParameters callLogSearchParameters) {
        QueryParam queryParam = callLogSearchParameters.getQueryParams();
        return callRecordsDataService.findByCriteria(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getStartFromDateAsDateTime(),
                callLogSearchParameters.getStartToDateAsDateTime(),
                callLogSearchParameters.getAnswerFromDateAsDateTime(),
                callLogSearchParameters.getAnswerToDateAsDateTime(),
                callLogSearchParameters.getEndFromDateAsDateTime(),
                callLogSearchParameters.getEndToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(),
                mapToDispositions(callLogSearchParameters),
                mapToDirections(callLogSearchParameters),
                callLogSearchParameters.getQueryParams());
    }

    /**
     *
     * @param callLogSearchParameters
     * @return the number of pages which fit the given call record search parameters
     */
    @Override
    public long count(CallRecordSearchParameters callLogSearchParameters) {
        double numOfPages = allCallDetailRecords.countRecords(callLogSearchParameters.getPhoneNumber(),
                callLogSearchParameters.getStartFromDateAsDateTime(),
                callLogSearchParameters.getStartToDateAsDateTime(),
                callLogSearchParameters.getAnswerFromDateAsDateTime(),
                callLogSearchParameters.getAnswerToDateAsDateTime(),
                callLogSearchParameters.getEndFromDateAsDateTime(),
                callLogSearchParameters.getEndToDateAsDateTime(),
                callLogSearchParameters.getMinDuration(),
                callLogSearchParameters.getMaxDuration(), mapToDispositions(callLogSearchParameters),
                mapToDirections(callLogSearchParameters)) /
                (callLogSearchParameters.getQueryParams().getRecordsPerPage() * 1.0);
        return Math.round(Math.ceil(numOfPages));
    }

    @Override
    public List<String> getAllPhoneNumbers() {
        return allCallDetailRecords.getAllPhoneNumbers();
    }

    @Override
    public long findMaxCallDuration() {
        return allCallDetailRecords.findMaxCallDuration();
    }

    //Takes the given Call Record Search Parameters and returns a list of all dispositions
    //in the parameters
    private Set<CallDisposition> mapToDispositions(CallRecordSearchParameters callLogSearchParameters) {
        Set<CallDisposition> dispositions = new HashSet<>();

        if (callLogSearchParameters.getAnswered()) {
            dispositions.add(CallDisposition.ANSWERED);
        }
        if (callLogSearchParameters.getBusy()) {
            dispositions.add(CallDisposition.BUSY);
        }
        if (callLogSearchParameters.getFailed()) {
            dispositions.add(CallDisposition.FAILED);
        }
        if (callLogSearchParameters.getNoAnswer()) {
            dispositions.add(CallDisposition.NO_ANSWER);
        }
        if (callLogSearchParameters.getUnknown()) {
            dispositions.add(CallDisposition.UNKNOWN);
        }
        return dispositions;
    }

    //Takes the given Call Record Search Parameters and returns a list of all directions
    //in the parameters
    private Set<CallDirection> mapToDirections(CallRecordSearchParameters callLogSearchParameters) {
        Set<CallDirection> directions = new HashSet<>();
        if (callLogSearchParameters.isInbound()) {
            directions.add(CallDirection.INBOUND);
        }
        if (callLogSearchParameters.isOutbound()) {
            directions.add(CallDirection.OUTBOUND);
        }
        return directions;
    }
}
