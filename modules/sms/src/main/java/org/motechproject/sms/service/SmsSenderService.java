package org.motechproject.sms.service;

import org.apache.commons.httpclient.HttpClient;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.model.OutgoingSms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class SmsSenderService {

    private Logger logger = LoggerFactory.getLogger(SmsSenderService.class);
    private EventRelay eventRelay;
    private HttpClient commonsHttpClient;
    private MotechSchedulerService schedulerService;

    @Autowired
    public SmsSenderService(@Qualifier("smsSettings") SettingsFacade settings, EventRelay eventRelay,
                            HttpClient commonsHttpClient, MotechSchedulerService schedulerService) {
        this.eventRelay = eventRelay;
        this.commonsHttpClient = commonsHttpClient;
        this.schedulerService = schedulerService;
    }

    public void send(OutgoingSms sms) {
        logger.info("Actually sending {}", sms.toString());
        //imagine we're failing, post an event to send again.
    }
}
