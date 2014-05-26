package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;
import org.motechproject.mds.annotations.Entity;

import java.util.List;

@Entity
public class FormSchemaJson {

/*    @SerializedName("name")
    @Field
    private Map<String, Object> formNames;*/

    @SerializedName("questions")
    private List<FormSchemaQuestionJson> questions;
/*
    public Map<String, Object> getFormNames() {
        return formNames;
    }

    public void setFormNames(Map<String, Object> formNames) {
        this.formNames = formNames;
    }*/

    public List<FormSchemaQuestionJson> getQuestions() {
        return questions;
    }

    public void setQuestions(List<FormSchemaQuestionJson> questions) {
        this.questions = questions;
    }
}
