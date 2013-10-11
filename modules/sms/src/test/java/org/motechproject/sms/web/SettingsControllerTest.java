package org.motechproject.sms.web;

import org.mockito.Mock;
import org.motechproject.server.config.SettingsFacade;
import org.springframework.test.web.server.MockMvc;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

public class SettingsControllerTest {
    private static final String NEW_LINE = System.lineSeparator();
    private static final String REQUIRED_FORMAT = "%s is required" + NEW_LINE;
    private static final String NUMERIC_FORMAT = "%s must be numeric" + NEW_LINE;

    private static final String HOST = "localhost";
    private static final String PORT = "8099";
    private static final String LOG_ADDRESS = "true";
    private static final String LOG_BODY = "true";
    private static final String LOG_PURGE = "true";
    private static final String LOG_TIME = "1";
    private static final String LOG_MULTIPLIER = "weeks";

    @Mock
    private SettingsFacade settingsFacade;

    private MockMvc controller;
/*
    @Before
    public void setUp() throws Exception {
        initMocks(this);

        when(settingsFacade.getProperty(MAIL_HOST_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(HOST);
        when(settingsFacade.getProperty(MAIL_PORT_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(PORT);
        when(settingsFacade.getProperty(MAIL_LOG_ADDRESS_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(LOG_ADDRESS);
        when(settingsFacade.getProperty(MAIL_LOG_BODY_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(LOG_BODY);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_ENABLE_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(LOG_PURGE);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_PROPERY, SMS_PROPERTIES_FILE_NAME)).thenReturn(LOG_TIME);
        when(settingsFacade.getProperty(MAIL_LOG_PURGE_TIME_MULTIPLIER_PROPERTY, SMS_PROPERTIES_FILE_NAME)).thenReturn(LOG_MULTIPLIER);


        controller = MockMvcBuilders.standaloneSetup(new SettingsController(settingsFacade)).build();
    }

    @Test
    public void shouldReturnSettingsDto() throws Exception {
        controller.perform(
                get("/configs")
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        ).andExpect(
                content().string(jsonMatcher(
                        settingsJson(HOST, PORT, LOG_ADDRESS, LOG_BODY, LOG_PURGE, LOG_TIME, LOG_MULTIPLIER)
                ))
        );
    }

    @Test
    public void shouldChangeSettings() throws Exception {
        String remotehost = "remotehost";
        String port = "9999";
        String logAddress = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/configs").body(
                        settingsJson(
                                remotehost, port, logAddress, logBody, logPurge, logPurgeTime,
                                logPurgeMultiplier
                        ).getBytes()
                ).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_OK)
        );

        Properties properties = new Settings(
                remotehost, port, logAddress, logBody, logPurge, logPurgeTime,logPurgeMultiplier
        ).toProperties();

        verify(settingsFacade).saveConfigProperties(SMS_PROPERTIES_FILE_NAME, properties);
    }

    @Test
    public void shouldNotChangeSettingsWhenHostIsBlank() throws Exception {
        String port = "9999";
        String logAddress = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/configs").body(settingsJson(
                        "", port, logAddress, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(REQUIRED_FORMAT, MAIL_HOST_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
    }

    @Test
    public void shouldNotChangeSettingsWhenPortIsBlank() throws Exception {
        String remotehost = "remotehost";
        String logAddress = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/configs").body(settingsJson(
                        remotehost, "", logAddress, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(REQUIRED_FORMAT, MAIL_PORT_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
    }

    @Test
    public void shouldNotChangeSettingsWhenPortIsNotNumeric() throws Exception {
        String remotehost = "remotehost";
        String port = "9999a";
        String logAddress = "false";
        String logBody = "false";
        String logPurge = "false";
        String logPurgeTime = "0";
        String logPurgeMultiplier = "days";

        controller.perform(
                post("/configs").body(settingsJson(
                        remotehost, port, logAddress, logBody, logPurge, logPurgeTime,logPurgeMultiplier
                ).getBytes()).contentType(APPLICATION_JSON)
        ).andExpect(
                status().is(HttpStatus.SC_NOT_FOUND)
        ).andExpect(
                content().string(String.format(NUMERIC_FORMAT, MAIL_PORT_PROPERTY))
        );

        verify(settingsFacade, never()).saveConfigProperties(anyString(), any(Properties.class));
    }

    private String settingsJson(String host, String port, String logAddress, String logBody,
                                String logPurgeEnable, String logPurgeTime, String logPurgeTimeMultiplier) {

        ObjectNode jsonNode = new ObjectMapper().createObjectNode();
        jsonNode.put("host", host);
        jsonNode.put("port", port);
        jsonNode.put("logAddress", logAddress);
        jsonNode.put("logBody", logBody);
        jsonNode.put("logPurgeEnable", logPurgeEnable);
        jsonNode.put("logPurgeTime", logPurgeTime);
        jsonNode.put("logPurgeTimeMultiplier", logPurgeTimeMultiplier);


        return jsonNode.toString();
    }
*/
}
