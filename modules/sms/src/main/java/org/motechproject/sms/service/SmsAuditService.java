package org.motechproject.sms.service;

import org.motechproject.sms.domain.SmsRecord;
import org.motechproject.sms.domain.SmsRecords;

import java.util.List;

public interface SmsAuditService {

    void log(SmsRecord smsRecord);

    void updateDeliveryStatus(String recipient, String refNo, String name);

    List<SmsRecord> findAllSmsRecords();

    SmsRecords findAllSmsRecords(SmsRecordSearchCriteria criteria);

}
