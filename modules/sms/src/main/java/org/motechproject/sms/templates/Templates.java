package org.motechproject.sms.templates;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import org.apache.commons.io.IOUtils;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.settings.ConfigsDto;
import org.springframework.core.io.ByteArrayResource;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * todo
 */
public class Templates {
    private List<Template> templates;
    private Boolean test = false;

    public Templates() {
        test = true;
    }

    public List<Template> getTemplates() {
        Boolean foo = test;
        return templates;
    }

    public Map<String, TemplateForWeb> templatesForWeb() {
        Map<String, TemplateForWeb> ret = new HashMap<String, TemplateForWeb>();
        for (Template template : templates) {
            ret.put(template.getName(), new TemplateForWeb(template));
        }
        return ret;
    }

    public void setTemplates(List<Template> templates) {
        Boolean foo = test;
        this.templates = templates;
    }
}
