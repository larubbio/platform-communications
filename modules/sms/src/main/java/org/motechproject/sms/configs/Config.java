package org.motechproject.sms.configs;

import java.util.List;

/**
 * todo
 */
public class Config {
    private String name;
    private Integer maxRetries;
    private Boolean excludeLastFooter;
    private Boolean multiRecipientSupport;
    private String splitHeader;
    private String splitFooter;
    private String templateName;

    private List<ConfigProp> props;

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
