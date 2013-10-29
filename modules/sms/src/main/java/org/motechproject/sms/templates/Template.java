package org.motechproject.sms.templates;

import com.google.gson.Gson;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.lang.StringUtils;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * todo
 */
public class Template {

    public static final String MESSAGE_PLACEHOLDER = "$message";
    public static final String RECIPIENTS_PLACEHOLDER = "$recipients";
    public static final Pattern pFindToken = Pattern.compile("\\[(\\w*)\\]");

    private Outgoing outgoing;
    private Status status;
    private Incoming incoming;
    private String name;
    private List<String> requires;

    public HttpMethod generateRequestFor(Map<String, String> props) {
        HttpMethod httpMethod;
        if (HttpMethodType.POST.equals(outgoing.getRequest().getType())) {
            httpMethod = new PostMethod(outgoing.getRequest().getUrlPath(props));
            if (outgoing.getRequest().getJsonContentType()) {
                Map<String, String> jsonParams = getJsonParameters(outgoing.getRequest().getBodyParameters(), props);
                Gson gson = new Gson();
                String json = gson.toJson(jsonParams);
                StringRequestEntity requestEntity = null;
                try {
                    requestEntity = new StringRequestEntity(json, "application/json", "UTF-8");
                }
                catch  (UnsupportedEncodingException e) {
                    //todo: not sure what....
                }
                if (requestEntity != null) {
                    ((PostMethod)httpMethod).setRequestEntity(requestEntity);
                }
                else {
                    //todo: what???
                }
            }
            else {
                httpMethod.setRequestHeader("Content-Type", PostMethod.FORM_URL_ENCODED_CONTENT_TYPE);
                addBodyParameters((PostMethod) httpMethod, props);
            }
        } else {
            httpMethod = new GetMethod(outgoing.getRequest().getUrlPath(props));
        }
        httpMethod.setQueryString(addQueryParameters(props));
        return httpMethod;
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

    private Map<String, String> getJsonParameters(Map<String, String> bodyParameters, Map<String, String> props) {
        Map<String, String> ret = new HashMap<String, String>();
        for (Map.Entry<String,String> entry: bodyParameters.entrySet()) {
            String value = placeHolderOrLiteral(entry.getValue(), props);
            ret.put(entry.getKey(), value);
        }
        return ret;
    }

    private void addBodyParameters(PostMethod postMethod, Map<String, String> props) {
        Map<String, String> bodyParameters = outgoing.getRequest().getBodyParameters();
        for (Map.Entry<String,String> entry: bodyParameters.entrySet()) {
            String value = placeHolderOrLiteral(entry.getValue(), props);
            postMethod.setParameter(entry.getKey(), value);
        }
    }

    // return input string with replaced values of [tokens] if found in props, so if you're passing
    // "foobar[baz][bee]" and props contains bar:goo, then returns "foobargoo[bee]"
    static private String placeHolderOrLiteral(String value, Map<String, String> props) {
        StringBuffer sb = new StringBuffer();
        Matcher matcher = pFindToken.matcher(value);

        while (matcher.find())
        {
            String repString = props.get(matcher.group(1));
            if (repString != null) {
                matcher.appendReplacement(sb, repString);
            }
            else {
                matcher.appendReplacement(sb, "[" + matcher.group(1) + "]");
            }
        }
        matcher.appendTail(sb);

        return sb.toString();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Template{" +
                "outgoing=" + outgoing +
                ", status=" + status +
                ", incoming=" + incoming +
                ", name='" + name + '\'' +
                ", requires=" + requires +
                '}';
    }
}
