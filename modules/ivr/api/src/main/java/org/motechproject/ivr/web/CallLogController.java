package org.motechproject.ivr.web;

import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.ivr.domain.CallDetailRecord;
import org.motechproject.ivr.domain.CallRecordSearchParameters;
import org.motechproject.ivr.service.contract.CallRecordsDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


/**
 *  Service methods for angular ui. Includes searching call logs, finding
 *  count of number of pages of call logs, finding the maximum call duration in
 *  the logs, and finding a list of all phone numbers in the logs.
 */
@Controller
@RequestMapping(value = "/calllog", method = RequestMethod.GET)
public class CallLogController {

    @Autowired
    private CallRecordsDataService callRecordsDataService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    @ResponseBody
    public CallLogRecords search(@ModelAttribute GridSettings settings) throws IOException {
        CallRecordSearchParameters params = settings.toCallRecordSearchParameters();
        List<CallDetailRecord> records = callRecordsDataService.search(params);
        return new CallLogRecords(settings.getPage(), settings.getRows(), records);
    }

    @RequestMapping(value = "/count",  method = RequestMethod.GET)
    @ResponseBody
    public String count(@ModelAttribute GridSettings settings) throws IOException {
        HashMap<String, Long> map = new HashMap<>();
        CallRecordSearchParameters params = settings.toCallRecordSearchParameters();
        map.put("count", callRecordsDataService.count(params));
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping(value = "/maxduration",  method = RequestMethod.GET)
    @ResponseBody
    public String findMaxCallDuration() throws IOException {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("maxDuration", callRecordsDataService.findMaxCallDuration());
        return new ObjectMapper().writeValueAsString(map);
    }

    @RequestMapping(value = "/phone-numbers",  method = RequestMethod.GET)
    @ResponseBody
    public List<String> allPhoneNumbers() throws IOException {
        return callRecordsDataService.getAllPhoneNumbers();
    }
}
