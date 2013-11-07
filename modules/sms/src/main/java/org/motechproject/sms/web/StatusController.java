package org.motechproject.sms.web;

import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.alert.MotechAlert;
import org.motechproject.sms.audit.SmsAuditService;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.audit.SmsRecordSearchCriteria;
import org.motechproject.sms.audit.SmsRecords;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.configs.ConfigReader;
import org.motechproject.sms.configs.Configs;
import org.motechproject.sms.templates.Status;
import org.motechproject.sms.templates.Template;
import org.motechproject.sms.templates.TemplateReader;
import org.motechproject.sms.templates.Templates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.DeliveryStatus.*;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.*;

/**
 * Handles message delivery status updates sent by sms providers to
 * {motechserver}/motech-platform-server/module/sms/status{Config}
 */
@Controller
@RequestMapping(value = "/status")
public class StatusController {

    @Autowired
    MotechAlert motechAlert;
    private Logger logger = LoggerFactory.getLogger(StatusController.class);
    private ConfigReader configReader;
    private Configs configs;
    private Templates templates;
    private EventRelay eventRelay;
    private SmsAuditService smsAuditService;

    @Autowired
    public StatusController(@Qualifier("smsSettings") SettingsFacade settingsFacade, EventRelay eventRelay,
                            TemplateReader templateReader, SmsAuditService smsAuditService) {
        this.eventRelay = eventRelay;
        configReader = new ConfigReader(settingsFacade);
        configs = configReader.getConfigs();
        templates = templateReader.getTemplates();
        this.smsAuditService = smsAuditService;
    }

    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    @RequestMapping(value = "/{configName}")
    public void handle(@PathVariable String configName, @RequestParam Map<String, String> params) {
        logger.info("SMS Status - configName = {}, params = {}", configName, params);

        Config config;
        if (configs.hasConfig(configName)) {
            config = configs.getConfig(configName);
        }
        else {
            String msg = String.format("Received SMS Status for '%s' config but no matching config: %s", configName,
                    params);
            logger.error(msg);
            motechAlert.alert(msg);
            config = configs.getDefaultConfig();
        }
        Template template = templates.getTemplate(config.getTemplateName());
        Status status = template.getStatus();

        if (status.hasMessageIdKey() && params != null && params.containsKey(status.getMessageIdKey())) {
            String providerMessageId = params.get(status.getMessageIdKey());

            if (status.hasStatusKey() && status.hasStatusSuccess()) {
                String statusString = params.get(status.getStatusKey());

                SmsRecord smsRecord = null;
                QueryParam queryParam = new QueryParam();
                queryParam.setSortBy("timestamp");
                queryParam.setReverse(true);

                SmsRecords smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                        .withConfig(configName)
                        .withProviderId(providerMessageId)
                        .withQueryParam(queryParam));
                if (CollectionUtils.isEmpty(smsRecords.getRecords())) {
                    smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                            .withConfig(configName)
                            .withMotechId(providerMessageId)
                            .withQueryParam(queryParam));
                    if (CollectionUtils.isEmpty(smsRecords.getRecords())) {
                        String msg = String.format("Received status update but couldn't find a log record with matching providerMessageId or motechId: %s",
                                providerMessageId);
                        logger.error(msg);
                        motechAlert.alert(msg);
                    }
                    else {
                        logger.debug("Found log record with matching motechId {}", providerMessageId);
                    }
                }
                else {
                    logger.debug("Found log record with matching providerId {}", providerMessageId);
                }

                if (smsRecords.getCount() > 0) {
                    //results sorted on desc timestamp, so get(0) will be most recent
                    SmsRecord existingSmsRecord = smsRecords.getRecords().get(0);
                    smsRecord = new SmsRecord(configName, OUTBOUND, existingSmsRecord.getPhoneNumber(),
                            existingSmsRecord.getMessageContent(), now(), null, statusString,
                            existingSmsRecord.getMotechId(), providerMessageId, null);
                }
                else {
                    //start with an empty SMS record
                    smsRecord = new SmsRecord(configName, OUTBOUND, null, null, now(), null, statusString, null,
                            providerMessageId, null);
                }

                List<String> recipients = Arrays.asList(new String[]{smsRecord.getPhoneNumber()});

                if (statusString != null) {
                    String eventSubject;
                    if (statusString.matches(status.getStatusSuccess())) {
                        smsRecord.setDeliveryStatus(DELIVERY_CONFIRMED);
                        eventSubject = OUTBOUND_SMS_DELIVERY_CONFIRMED;
                    }
                    else if (status.hasStatusFailure() && statusString.matches(status.getStatusFailure())) {
                        smsRecord.setDeliveryStatus(FAILURE_CONFIRMED);
                        eventSubject = OUTBOUND_SMS_FAILURE_CONFIRMED;
                    }
                    else {
                        smsRecord.setDeliveryStatus(DISPATCHED);
                        eventSubject = OUTBOUND_SMS_DISPATCHED;
                    }
                    eventRelay.sendEventMessage(outboundEvent(eventSubject, configName, recipients,
                            smsRecord.getMessageContent(), smsRecord.getMotechId(), providerMessageId, null, statusString,
                            now()));
                }
                else {
                    String msg = String.format("Likely template error, unable to extract status string. Config: %s, Parameters: %s",
                            configName, params);
                    logger.error(msg);
                    motechAlert.alert(msg);
                    smsRecord.setDeliveryStatus(FAILURE_CONFIRMED);
                    eventRelay.sendEventMessage(outboundEvent(OUTBOUND_SMS_FAILURE_CONFIRMED, configName, recipients,
                            smsRecord.getMessageContent(), smsRecord.getMotechId(), providerMessageId, null, null,
                            now()));
                }

                smsAuditService.log(smsRecord);
            }
            else {
                String msg = String.format("We have a message id, but don't know how to extract message status, this is most likely a template error. Config: %s, Parameters: %s",
                        configName, params);
                logger.error(msg);
                motechAlert.alert(msg);
            }
        }
        else {
            String msg = String.format("Status message received from provider, but no template support! Config: %s, Parameters: %s",
                    configName, params);
            logger.error(msg);
            motechAlert.alert(msg);
        }
    }
}
