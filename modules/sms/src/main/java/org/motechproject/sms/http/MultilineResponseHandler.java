package org.motechproject.sms.http;

import org.apache.commons.httpclient.Header;
import org.motechproject.sms.alert.MotechStatusMessage;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.service.OutgoingSms;
import org.motechproject.sms.templates.Template;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.DeliveryStatus.DISPATCHED;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.OUTBOUND_SMS_DISPATCHED;
import static org.motechproject.sms.event.SmsEvents.outboundEvent;

/**
 * Deals with multi-line responses, like the ones sent by Clickatell
 */
public class MultilineResponseHandler extends ResponseHandler {

    @Autowired
    MotechStatusMessage motechStatusMessage;

    MultilineResponseHandler(Template template, Config config) {
        super(template, config);
    }

    public void handle(OutgoingSms sms, String response, Header[] headers) {

        for (String responseLine : response.split("\\r?\\n")) {

            String[] messageIdAndRecipient = templateOutgoingResponse.extractSuccessMessageIdAndRecipient(responseLine);

            if (messageIdAndRecipient == null) {
                Integer failureCount = sms.getFailureCount() + 1;
                String[] messageAndRecipient;

                messageAndRecipient = templateOutgoingResponse.extractFailureMessageAndRecipient(responseLine);
                if (messageAndRecipient == null) {
                    events.add(outboundEvent(config.RetryOrAbortSubject(failureCount), config.getName(),
                            sms.getRecipients(), sms.getMessage(), sms.getMotechId(), null, failureCount, null, null));

                    String errorMessage = String.format(
                            "Failed to sent SMS. Template error. Can't parse response: %s", responseLine);
                    logger.error(errorMessage);
                    motechStatusMessage.alert(errorMessage);

                    auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, sms.getRecipients().toString(),
                            sms.getMessage(), now(), config.RetryOrAbortStatus(failureCount), null, sms.getMotechId(),
                            null, null));
                }
                else {
                    String failureMessage = messageAndRecipient[0];
                    String recipient = messageAndRecipient[1];
                    List<String> recipients = Arrays.asList(new String[]{recipient});
                    events.add(outboundEvent(config.RetryOrAbortSubject(failureCount), config.getName(), recipients,
                            sms.getMessage(), sms.getMotechId(), null, failureCount, null, null));
                    logger.info("Failed to sent SMS: {}", failureMessage);
                    auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(), now(),
                            config.RetryOrAbortStatus(failureCount), null, sms.getMotechId(), null, failureMessage));
                }
            }
            else {
                String messageId = messageIdAndRecipient[0];
                String recipient = messageIdAndRecipient[1];
                List<String> recipients = Arrays.asList(new String[]{recipient});
                //todo: HIPAA concerns?
                logger.info(String.format("Sent messageId %s '%s' to %s", messageId, messageForLog(sms), recipient));
                auditRecords.add(new SmsRecord(config.getName(), OUTBOUND, recipient, sms.getMessage(), now(),
                        DISPATCHED, null, sms.getMotechId(), messageId, null));
                events.add(outboundEvent(OUTBOUND_SMS_DISPATCHED, config.getName(), recipients, sms.getMessage(),
                        sms.getMotechId(), messageId, null, null, null));
            }
        }
    }
}
