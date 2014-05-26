package org.motechproject.commcare.parser;

import com.google.gson.FieldNamingStrategy;
import org.motechproject.commcare.domain.CommcareApplicationJson;
import org.motechproject.commcare.domain.CommcareModuleJson;
import org.motechproject.commcare.domain.FormSchemaJson;
import org.motechproject.commcare.domain.FormSchemaQuestionJson;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Component
public class CommcareApplicationNamingStrategy implements FieldNamingStrategy {

    @Override
    public String translateName(Field f) {
        Class<?> declaring = f.getDeclaringClass();

        if (CommcareApplicationJson.class.equals(declaring)) {
            switch (f.getName()) {
                case "applicationName":
                    return "name";
                case "resourceUri":
                    return "resource_uri";
                default:
                    return f.getName();
            }
        } else if (CommcareModuleJson.class.equals(declaring)) {
            switch (f.getName()) {
                case "formSchemas":
                    return "forms";
                case "caseType":
                    return "case_type";
                case "caseProperties":
                    return "case_properties";
                default:
                    return f.getName();
            }
        } else if (FormSchemaJson.class.equals(declaring)) {
            switch (f.getName()) {
                case "formNames":
                    return "name";
                default:
                    return f.getName();
            }
        } else if (FormSchemaQuestionJson.class.equals(declaring)) {
            switch (f.getName()) {
                case "questionLabel":
                    return "label";
                case "questionRepeat":
                    return "repeat";
                case "questionTag":
                    return "tag";
                case "questionValue":
                    return "value";
                default:
                    return f.getName();
            }
        } else {
            return f.getName();
        }
    }
}
