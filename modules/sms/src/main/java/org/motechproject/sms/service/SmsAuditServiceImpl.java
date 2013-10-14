package org.motechproject.sms.service;

import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.audit.AllSmsRecords;
import org.motechproject.sms.audit.SmsRecords;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SmsAuditServiceImpl implements SmsAuditService {

    private AllSmsRecords allSmsRecords;
    private Logger logger = LoggerFactory.getLogger(SmsAuditServiceImpl.class);

    @Autowired
    public SmsAuditServiceImpl(AllSmsRecords allSmsRecords) {
        this.allSmsRecords = allSmsRecords;
    }

    @Override
    public void log(SmsRecord smsRecord) {
        logger.info("AUDIT LOG: {}", smsRecord.toString());
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
