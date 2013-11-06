package org.motechproject.sms.http;

import org.motechproject.event.MotechEvent;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.templates.Response;
import org.motechproject.sms.templates.Template;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.httpclient.Header;

import java.util.ArrayList;
import java.util.List;

/**
 * figures out success or failure from an sms provider response
 */
public abstract class ResponseHandler {
    protected Template template;
    protected Config config;
    protected Response templateOutgoingResponse;
    protected List<MotechEvent> events = new ArrayList<MotechEvent>();
    protected List<SmsRecord> auditRecords = new ArrayList<SmsRecord>();
    protected Logger logger = LoggerFactory.getLogger(ResponseHandler.class);

    ResponseHandler() {
    }

    ResponseHandler(Template template, Config config) {
        this.template = template;
        this.config = config;
        templateOutgoingResponse = template.getOutgoing().getResponse();
    }

    protected String messageForLog(OutgoingSms sms) {
        return sms.getMessage().replace("\n", "\\n");
    }
    public abstract  void handle(OutgoingSms sms, String response, Header[] headers);

    public List<MotechEvent> getEvents() {
        return events;
    }

    public List<SmsRecord> getAuditRecords() {
        return auditRecords;
    }
}
