package org.motechproject.sms.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.templates.Template;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.DeliveryStatus.DISPATCHED;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.OUTBOUND_SMS_DISPATCHED;
import static org.motechproject.sms.event.SmsEvents.outboundEvent;

/**
 * Deals with providers who return multi-line responses, but return a different response when sending only one message,
 * like Clickatell does
 */
public class MultilineSingleResponseHandler extends ResponseHandler {

    MultilineSingleResponseHandler(Template template, Config config) {
        super(template, config);
    }

    public void handle(OutgoingSms sms, String response, Header[] headers) {

        String messageId = templateOutgoingResponse.extractSingleSuccessMessageId(response);

        if (messageId == null) {
            Integer failureCount = sms.getFailureCount() + 1;

            String failureMessage = templateOutgoingResponse.extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            events.add(outboundEvent(config.RetryOrAbortSubject(failureCount), config.getName(), sms.getRecipients(),
                    sms.getMessage(), sms.getMotechId(), null, failureCount, null, null));
            logger.info("Failed to sent SMS: %s", failureMessage);
            auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, sms.getRecipients().get(0), sms.getMessage(),
                    now(), config.RetryOrAbortStatus(failureCount), null, sms.getMotechId(), null, failureMessage));
        }
        else {
            //todo: HIPAA concerns?
            logger.info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(sms),
                    sms.getRecipients().get(0)));
            auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, sms.getRecipients().get(0), sms.getMessage(),
                    now(), DISPATCHED, null, sms.getMotechId(), messageId, null));
            events.add(outboundEvent(OUTBOUND_SMS_DISPATCHED, config.getName(), sms.getRecipients(), sms.getMessage(),
                    sms.getMotechId(), messageId, null, null, null));
        }
    }
}
