package org.motechproject.sms.service;

import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.audit.AllSmsRecords;
import org.motechproject.sms.audit.SmsRecords;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsAuditServiceImpl implements SmsAuditService {

    private AllSmsRecords allSmsRecords;

    @Autowired
    public SmsAuditServiceImpl(AllSmsRecords allSmsRecords) {
        this.allSmsRecords = allSmsRecords;
    }

    @Override
    public void log(SmsRecord smsRecord) {
        allSmsRecords.addOrReplace(smsRecord);
    }

    public List<SmsRecord> findAllSmsRecords() {
        return allSmsRecords.getAll();
    }

    @Override
    public SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria) {
        return allSmsRecords.findAllBy(criteria);
    }
}
