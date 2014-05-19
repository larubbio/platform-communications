package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;

import java.util.List;
import java.util.Map;

@Entity
public class FormSchemaJson {

    @SerializedName("name")
    @Field
    private Map<String, String> formNames;

    @SerializedName("questions")
    @Ignore
    private List<FormSchemaQuestionJson> questions;

    public Map<String, String> getFormNames() {
        return formNames;
    }

    public void setFormNames(Map<String, String> formNames) {
        this.formNames = formNames;
    }

    public List<FormSchemaQuestionJson> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FormSchemaQuestionJson> questions) {
        this.questions = questions;
    }
}
