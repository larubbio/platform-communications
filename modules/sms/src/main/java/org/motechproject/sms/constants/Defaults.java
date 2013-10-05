package org.motechproject.sms.constants;

import java.util.Map;

/**
 * TODO
 */
public class Defaults {
    public static final String MAX_RETRIES = "3";
    public static final String MAX_SMS_SIZE = "160";
    public static final String SPLIT_HEADER = "Msg $1 of $2";
    public static final String SPLIT_FOOTER = "...";
    public static final String  SPLIT_EXCLUDE = "true";
    public static final String MULTI_RECIPIENT = "false";

    static public String emptyStrPropOrVal(Map<String, Object> map, String key, String val) {
        if (map.containsKey(key)) {
            return (String)map.get(key);
        }
        return val;
    }

    static public String strPropOrVal(Map<String, Object> map, String key, String val) {
        if (map.containsKey(key) && (((String)map.get(key)).length() > 0)) {
            return (String)map.get(key);
        }
        return val;
    }
}
