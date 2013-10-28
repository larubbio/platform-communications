package org.motechproject.sms.web;

import org.motechproject.commons.couchdb.query.QueryParam;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.audit.SmsRecord;
import org.motechproject.sms.audit.SmsRecordSearchCriteria;
import org.motechproject.sms.audit.SmsRecords;
import org.motechproject.sms.configs.Config;
import org.motechproject.sms.configs.ConfigReader;
import org.motechproject.sms.configs.Configs;
import org.motechproject.sms.service.SmsAuditService;
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

import java.util.Map;

import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;
import static java.util.Collections.reverseOrder;
import static org.motechproject.commons.date.util.DateUtil.now;
import static org.motechproject.sms.audit.SmsDeliveryStatus.DELIVERY_CONFIRMED;
import static org.motechproject.sms.audit.SmsDeliveryStatus.FAILURE_CONFIRMED;
import static org.motechproject.sms.audit.SmsType.INBOUND;
import static org.motechproject.sms.audit.SmsType.OUTBOUND;
import static org.motechproject.sms.event.SmsEvents.makeOutboundSmsFailureEvent;
import static org.motechproject.sms.event.SmsEvents.makeOutboundSmsSuccessEvent;

/**
 * todo
 */
@Controller
@RequestMapping(value = "/status")
public class StatusController {

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
        logger.info("configName = {}, params = {}", configName, params);

        Config config;
        if (configs.hasConfig(configName)) {
            config = configs.getConfig(configName);
        }
        else {
            //todo: do we really want to do that?
            config = configs.getDefaultConfig();
        }
        Template template = templates.getTemplate(config.getTemplateName());
        Status status = template.getStatus();

        if (status.hasMessageIdKey() && params != null && params.containsKey(status.getMessageIdKey())) {
            String providerId = params.get(status.getMessageIdKey());

            if (status.hasStatusKey() && status.hasStatusSuccess()) {
                String statusString = params.get(status.getStatusKey());

                SmsRecord smsRecord = null;
                QueryParam queryParam = new QueryParam();
                queryParam.setSortBy("timestamp");
                queryParam.setReverse(true);

                SmsRecords smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                        .withConfig(configName)
                        .withProviderId(providerId)
                        .withQueryParam(queryParam));
                if (CollectionUtils.isEmpty(smsRecords.getRecords())) {
                    smsRecords = smsAuditService.findAllSmsRecords(new SmsRecordSearchCriteria()
                            .withConfig(configName)
                            .withMotechId(providerId)
                            .withQueryParam(queryParam));
                    if (CollectionUtils.isEmpty(smsRecords.getRecords())) {
                        logger.debug("Couldn't find a log record with matching providerId or motechId {}", providerId);
                    }
                    else {
                        logger.debug("Found log record with matching motechId {}", providerId);
                    }
                }
                else {
                    logger.debug("Found log record with matching providerId {}", providerId);
                }

                if (smsRecords.getCount() > 0) {
                    //results sorted on desc timestamp, so get(0) will be most recent
                    SmsRecord existingSmsRecord = smsRecords.getRecords().get(0);
                    smsRecord = new SmsRecord(configName, OUTBOUND, existingSmsRecord.getPhoneNumber(),
                            existingSmsRecord.getMessageContent(), now(), null, existingSmsRecord.getMotechId(),
                            providerId, null);
                }
                else {
                    //start with an empty SMS record
                    smsRecord = new SmsRecord(configName, OUTBOUND, null, null, now(), null, null, providerId, null);
                }

                if (statusString != null && statusString.matches(status.getStatusSuccess())) {
                    //todo: should we be more discriminant and log intermediary statuses - when provided?
                    smsRecord.setSmsDeliveryStatus(DELIVERY_CONFIRMED);
                    eventRelay.sendEventMessage(makeOutboundSmsSuccessEvent(configName, null, null, null, providerId,
                        now(), null));
                }
                else {
                    //todo: FAILURE_CONFIRMED or UNKNOWN???
                    smsRecord.setSmsDeliveryStatus(FAILURE_CONFIRMED);
                    eventRelay.sendEventMessage(makeOutboundSmsFailureEvent(configName, null, null, null, providerId,
                            now(), null));
                }

                smsAuditService.log(smsRecord);
            }
            else {
                //todo: we have a message but no way to know about its status, what do we do???
            }
        }
        else {
            logger.error("Status message received from provider, but no template support! Config: {}, Parameters: {}",
                    configName, params);
        }
    }
}
