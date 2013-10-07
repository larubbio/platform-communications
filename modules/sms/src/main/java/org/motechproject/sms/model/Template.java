package org.motechproject.sms.model;

import java.util.Properties;

/**
 * todo
 */
public class Template {
    private String name;
    private HttpMethodType httpMethod;
    Properties props;

    public Template(Properties props) {
        this.props = props;
    }

    public String getName() {
        return (String)props.get("name");
    }

    public String getURL() {
        return (String)props.get("url");
    }

    public String getBodyParameters() {
        return (String)props.get("bodyParameters");
    }

    public String getQueryParameters() {
        return (String)props.get("queryParameters");
    }

    public HttpMethodType getHttpMethod() {
        return HttpMethodType.valueOf((String)props.get("httpMethod"));
    }

    public Boolean hasAuthentication() {
        return props.containsKey("username") && !props.get("username").toString().isEmpty() &&
            props.containsKey("password") && !props.get("password").toString().isEmpty();
    }

    public String getUsername() {
        return (String)props.get("username");
    }

    public String getPassword() {
        return (String)props.get("password");
    }

    public Object getProp(String name) {
        return props.get(name);
    }
}
