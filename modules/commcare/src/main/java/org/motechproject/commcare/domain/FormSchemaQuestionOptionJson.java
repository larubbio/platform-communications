package org.motechproject.commcare.domain;

import com.google.gson.annotations.SerializedName;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

@Entity
public class FormSchemaQuestionOptionJson {

    @SerializedName("label")
    @Field
    private String label;

    @SerializedName("value")
    @Field
    private String value;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
