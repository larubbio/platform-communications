package org.motechproject.sms.templates;

import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.server.config.SettingsFacade;
import org.motechproject.sms.settings.ConfigsDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

/**
 * todo
 */
@Component
public class TemplateReader {

    public static final String FILE_NAME = "sms-templates.json";
    private String templateFileName;
    private SettingsFacade settingsFacade;

    @Autowired
    public TemplateReader(@Qualifier("smsSettings") SettingsFacade settingsFacade) {
        this.settingsFacade = settingsFacade;
        this.templateFileName = FILE_NAME;
    }

    public List<Template> getTemplates() {
        List<Template> templates;
        Type type = new TypeToken<List<Template>>() { }.getType();
        InputStream is = settingsFacade.getRawConfig(templateFileName);
        try {
            String jsonText = IOUtils.toString(is);
            Gson gson = new Gson();
            templates = gson.fromJson(jsonText, type);
        } catch (Exception e) {
            //todo: what do we do with these? (might be coming from malformed .json config file)
            throw new JsonIOException("Might you have a malformed " + FILE_NAME + " file? " + e.toString());
        }

        return templates;
    }
}
