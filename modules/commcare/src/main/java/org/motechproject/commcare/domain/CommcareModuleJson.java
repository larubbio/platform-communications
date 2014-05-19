package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;

import java.util.List;

@Entity
public class CommcareModuleJson {

    @SerializedName("case_properties")
    @Field
    private List<String> caseProperties;

    @SerializedName("case_type")
    @Field
    private String caseType;

    @SerializedName("forms")
    @Ignore
    private List<FormSchemaJson> formSchemas;

    public List<String> getCaseProperties() {
        return caseProperties;
    }

    public void setCaseProperties(List<String> caseProperties) {
        this.caseProperties = caseProperties;
    }

    public String getCaseType() {
        return caseType;
    }

    public void setCaseType(String caseType) {
        this.caseType = caseType;
    }

    public List<FormSchemaJson> getFormSchemas() {
        return formSchemas;
    }

    public void setFormSchemas(List<FormSchemaJson> formSchemas) {
        this.formSchemas = formSchemas;
    }
}
