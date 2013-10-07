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
    private Map<String, Template> templates = new HashMap<String, Template>();

    public Templates(List<Template> templates) {
        for(Template template : templates) {
            this.templates.put(template.getName(), template);
        }
    }

    public Template getTemplate(String name) {
        return templates.get(name);
    }

    public Map<String, TemplateForWeb> templatesForWeb() {
        Map<String, TemplateForWeb> ret = new HashMap<String, TemplateForWeb>();
        for (Map.Entry<String, Template> entry : templates.entrySet()) {
            ret.put(entry.getKey(), new TemplateForWeb(entry.getValue()));
        }
        return ret;
    }
}
