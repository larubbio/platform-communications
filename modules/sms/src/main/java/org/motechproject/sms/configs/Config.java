package org.motechproject.sms.configs;

import java.util.List;

/**
 * todo
 */
public class Config {
    public static final String NAME = "name";
    public static final String MAX_RETRIES = "max_retries";
    public static final String MAX_SMS_SIZE = "max_sms_size";
    public static final String SPLIT_HEADER = "split_header";
    public static final String SPLIT_FOOTER = "split_footer";
    public static final String  SPLIT_EXCLUDE = "split_exclude";
    public static final String MULTI_RECIPIENT = "multi_recipient";

    public static final Integer MAX_RETRIES_DEFAULT = 3;
    public static final Integer MAX_SMS_SIZE_DEFAULT = 160;
    public static final String SPLIT_HEADER_DEFAULT = "Msg $1 of $2";
    public static final String SPLIT_FOOTER_DEFAULT = "...";
    public static final Boolean  SPLIT_EXCLUDE_DEFAULT = true;
    public static final Boolean MULTI_RECIPIENT_DEFAULT = false;

    private String name;
    private Integer maxRetries;
    //todo: should that not be a property of the template? I think yes.
    private Integer maxSmsSize;
    private Boolean excludeLastFooter;
    private Boolean multiRecipientSupport;
    private String splitHeader;
    private String splitFooter;
    private String templateName;

    private List<ConfigProp> props;

    public Config() {
        maxRetries = MAX_RETRIES_DEFAULT;
        maxSmsSize = MAX_SMS_SIZE_DEFAULT;
        excludeLastFooter = SPLIT_EXCLUDE_DEFAULT;
        multiRecipientSupport = MULTI_RECIPIENT_DEFAULT;
        splitHeader = SPLIT_HEADER_DEFAULT;
        splitFooter = SPLIT_FOOTER_DEFAULT;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getMaxSmsSize() {
        return maxSmsSize;
    }

    public void setMaxSmsSize(Integer maxSmsSize) {
        this.maxSmsSize = maxSmsSize;
    }

    public Boolean getExcludeLastFooter() {
        return excludeLastFooter;
    }

    public void setExcludeLastFooter(Boolean excludeLastFooter) {
        this.excludeLastFooter = excludeLastFooter;
    }

    public Boolean getMultiRecipientSupport() {
        return multiRecipientSupport;
    }

    public void setMultiRecipientSupport(Boolean multiRecipientSupport) {
        this.multiRecipientSupport = multiRecipientSupport;
    }

    public String getSplitHeader() {
        return splitHeader;
    }

    public void setSplitHeader(String splitHeader) {
        this.splitHeader = splitHeader;
    }

    public String getSplitFooter() {
        return splitFooter;
    }

    public void setSplitFooter(String splitFooter) {
        this.splitFooter = splitFooter;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public List<ConfigProp> getProps() {
        return props;
    }

    public void setProps(List<ConfigProp> props) {
        this.props = props;
    }
}
