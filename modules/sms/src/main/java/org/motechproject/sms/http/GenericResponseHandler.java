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
 * Deals with providers who return a generic response in the body or header
 */
public class GenericResponseHandler extends ResponseHandler {

    GenericResponseHandler(Template template, Config config) {
        super(template, config);
    }

    public void handle(OutgoingSms sms, String response, Header[] headers) {

        if (!templateOutgoingResponse.hasSuccessResponse() || templateOutgoingResponse.checkSuccessResponse(response)) {

            String providerMessageId = null;

            if (templateOutgoingResponse.hasHeaderMessageId()) {
                for (Header header : headers) {
                    if (header.getName().equals(templateOutgoingResponse.getHeaderMessageId())) {
                        providerMessageId = header.getValue();
                    }
                }
                if (providerMessageId == null) {
                    logger.error("Unable to find provider message id in '{}' header",
                            templateOutgoingResponse.getHeaderMessageId());
                }
            }
            else if (templateOutgoingResponse.hasSingleSuccessMessageId()) {
                providerMessageId = templateOutgoingResponse.extractSingleSuccessMessageId(response);
            }

            //todo: HIPAA concerns?
            logger.info(String.format("Sent messageId %s '%s' to %s", providerMessageId, messageForLog(sms),
                    sms.getRecipients().toString()));
            for (String recipient : sms.getRecipients()) {
                auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(), now(),
                        DISPATCHED, null, sms.getMotechId(), providerMessageId, null));
            }
            events.add(outboundEvent(OUTBOUND_SMS_DISPATCHED, config.getName(), sms.getRecipients(), sms.getMessage(),
                    sms.getMotechId(), providerMessageId, null, null, null));

        }
        else {
            Integer failureCount = sms.getFailureCount() + 1;

            String failureMessage = templateOutgoingResponse.extractSingleFailureMessage(response);
            if (failureMessage == null) {
                failureMessage = response;
            }
            events.add(outboundEvent(config.RetryOrAbortSubject(failureCount), config.getName(), sms.getRecipients(),
                    sms.getMessage(), sms.getMotechId(), null, failureCount, null, null));
            logger.error("Failed to sent SMS: %s", failureMessage);
            for (String recipient : sms.getRecipients()) {
                auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(), now(),
                        config.RetryOrAbortStatus(failureCount), null, sms.getMotechId(), null, failureMessage));
            }
        }
    }
}
