package org.motechproject.commcare.domain;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;

import javax.jdo.annotations.Column;
import java.util.List;

/**
 * Represents the current schema of the Commcare application.
 * Entity managed by MDS.
 */
@Entity
public class CommcareApplication {

    @Field(required = true)
    private String applicationName;

    @Field
    @Column(length = 5000)
    private String resourceUri;

    @Field
    private List<String> modules;

    public CommcareApplication() {
    }

    public CommcareApplication(String applicationName, String resourceUri, List<String> moduleJsons) {
        this.applicationName = applicationName;
        this.resourceUri = resourceUri;
        this.modules = moduleJsons;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public String getResourceUri() {
        return resourceUri;
    }

    public void setResourceUri(String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public List<String> getModules() {
        return modules;
    }

    public void setModules(List<String> modules) {
        this.modules = modules;
    }
}
