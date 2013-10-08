package org.motechproject.sms.templates;

import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * todo
 */
public class Template {

    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";

    private Outgoing outgoing;
    private Incoming incoming;
    private String name;
    private List<String> requires;

    public HttpMethod generateRequestFor(Map<String, String> props) {
        HttpMethod httpMethod;
        if (HttpMethodType.POST.equals(outgoing.getRequest().getType())) {
            httpMethod = new PostMethod(outgoing.getRequest().getUrlPath());
            httpMethod.setRequestHeader("Content-Type", PostMethod.FORM_URL_ENCODED_CONTENT_TYPE);
            addBodyParameters((PostMethod) httpMethod, props);
        } else {
            httpMethod = new GetMethod(outgoing.getRequest().getUrlPath());
        }
        httpMethod.setQueryString(addQueryParameters(props));
        return httpMethod;
    }

    public Boolean outgoingSuccess(Integer status, String response) {
        Response templateResp = outgoing.getResponse();

        if (templateResp.hasSuccessStatus()) {
            if (!status.toString().matches(templateResp.getSuccessStatus())) {
                return false;
            }
            if (templateResp.hasSuccessResponse()) {
                if (response.matches(templateResp.getSuccessResponse())) {
                    return true;
                }
            }
            return false;
        }

        if (templateResp.hasSuccessResponse()) {
            if (response.matches(templateResp.getSuccessResponse())) {
                return true;
            }
            return false;
        }

        // no success status or success response, assume HTTP 200 is success
        if (status == 200) {
            return true;
        }

        return false;
    }

    private NameValuePair[] addQueryParameters(Map<String, String> props) {
        List<NameValuePair> queryStringValues = new ArrayList<NameValuePair>();
        Map<String, String> queryParameters = outgoing.getRequest().getQueryParameters();
        for (Map.Entry< String, String > entry : queryParameters.entrySet()) {
            String value = placeHolderOrLiteral(entry.getValue(), props);
            queryStringValues.add(new NameValuePair(entry.getKey(), value));
        }
        return queryStringValues.toArray(new NameValuePair[queryStringValues.size()]);
    }

    private void addBodyParameters(PostMethod postMethod, Map<String, String> props) {
        Map<String, String> bodyParameters = outgoing.getRequest().getBodyParameters();
        for (Map.Entry<String,String> entry: bodyParameters.entrySet()) {
            String value = placeHolderOrLiteral(entry.getValue(), props);
            postMethod.setParameter(entry.getKey(), value);
        }
    }

    static private String placeHolderOrLiteral(String value, Map<String, String> props) {
        if (value.matches("^\\[[\\w]+\\]$")) {
            String key = value.substring(1, value.length()-1);
            if (props.containsKey(key)) {
                return props.get(key);
            }
        }
        return value;
    }

    public String recipientsAsString(List<String> recipients) {
        return StringUtils.join(recipients.iterator(), outgoing.getRequest().getRecipientsSeparator());
    }

    public Outgoing getOutgoing() {
        return outgoing;
    }

    public void setOutgoing(Outgoing outgoing) {
        this.outgoing = outgoing;
    }

    public Incoming getIncoming() {
        return incoming;
    }

    public void setIncoming(Incoming incoming) {
        this.incoming = incoming;
    }

    public Authentication getAuthentication() {
        return outgoing.getRequest().getAuthentication();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getRequires() {
        return requires;
    }

    public void setRequires(List<String> requires) {
        this.requires = requires;
    }
}
