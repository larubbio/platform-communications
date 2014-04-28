package org.motechproject.commcare.domain;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;
import org.motechproject.commons.api.json.MotechJsonReader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class CommcareApplicationJson {

    @SerializedName("name")
    private String applicationName;
    @SerializedName("resource_uri")
    private String resourceUri;
    @SerializedName("modules")
    private List<CommcareModuleJson> modules;

    public CommcareApplicationJson(String applicationName, String resourceUri, List<CommcareModuleJson> modules) {
        this.applicationName = applicationName;
        this.resourceUri = resourceUri;
        this.modules = modules;
    }

    public CommcareApplicationJson(CommcareApplication commcareApplication) {
        this.applicationName = commcareApplication.getApplicationName();
        this.resourceUri = commcareApplication.getResourceUri();
        // read json data
        MotechJsonReader reader = new MotechJsonReader();
        Type type = new TypeToken<CommcareModuleJson>(){}.getType();

        modules = new ArrayList<>();
        for (String module : commcareApplication.getModules()) {
            CommcareModuleJson moduleJson = (CommcareModuleJson) reader.readFromString(module, type);
            modules.add(moduleJson);
        }
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

    public List<CommcareModuleJson> getModules() {
        return modules;
    }

    public void setModules(List<CommcareModuleJson> modules) {
        this.modules = modules;
    }

    public CommcareApplication toCommcareApplication() {
        Gson gson = new Gson();
        List<String> modulesAsStr = new ArrayList<>();

        for (CommcareModuleJson moduleJson : modules) {
            modulesAsStr.add(gson.toJson(moduleJson));
        }

        return new CommcareApplication(applicationName, resourceUri, modulesAsStr);
    }
}
