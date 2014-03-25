package org.motechproject.cmslite.api;

import org.motechproject.cmslite.api.model.CMSContent;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.service.CMSContentService;
import org.motechproject.commons.api.AbstractDataProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CMSDataProvider extends AbstractDataProvider {
    private static final String ID_FIELD = "cmslite.id";
    private static final String NAME_FIELD = "cmslite.dataname";
    private static final String LANGUAGE_FIELD = "cmslite.language";

    private CMSContentService cmsContentService;

    @Autowired
    public CMSDataProvider(ResourceLoader resourceLoader, CMSContentService cmsContentService) {
        Resource resource = resourceLoader.getResource("task-data-provider.json");
        if (resource != null) {
            setBody(resource);
        }
        this.cmsContentService = cmsContentService;
    }

    @Override
    public String getName() {
        return "CMS";
    }

    @Override
    public Object lookup(String type, String lookupName, Map<String, String> lookupFields) {
        Object obj = null;
        try {
            if (supports(type)) {
                if (lookupFields.containsKey(ID_FIELD)) {
                    Class<?> cls = getClassForType(type);
                    obj = getContent(cls);

                } else if (lookupFields.containsKey(NAME_FIELD) && lookupFields.containsKey(LANGUAGE_FIELD)) {
                    String name = lookupFields.get(NAME_FIELD);
                    String language = lookupFields.get(LANGUAGE_FIELD);
                    Class<?> cls = getClassForType(type);
                    obj = getContent(cls, language, name);
                }
            }
        } catch (ClassNotFoundException | ContentNotFoundException e) {
            logError("Cannot lookup object: {type: %s, fields: %s}", type, lookupFields.keySet(), e);
        }
        return obj;
    }

    @Override
    public List<Class<?>> getSupportClasses() {
        List<Class<?>> list = new ArrayList<>();
        list.add(CMSContent.class);
        return list;
    }

    @Override
    public String getPackageRoot() {
        return "org.motechproject.cmslite.api.model";
    }

    private Object getStringContent(String stringContentLanguage, String stringContentName) throws ContentNotFoundException {
        return cmsContentService.byNameAndLanguage(stringContentLanguage, stringContentName);
    }

    private Object getStreamContent(String streamContentLanguage, String streamContentName) throws ContentNotFoundException {
        return cmsContentService.byNameAndLanguage(streamContentLanguage, streamContentName);
    }

    private Object getContent(Class<?> cls, String language, String name) throws ContentNotFoundException {
        if (CMSContent.class.isAssignableFrom(cls)) {
            return getStringContent(language, name);
        } else if (CMSContent.class.isAssignableFrom(cls)) {
            return getStreamContent(language, name);
        }
        throw new ContentNotFoundException();
    }

    private Object getContent(Class<?> cls) throws ContentNotFoundException {
        if (CMSContent.class.isAssignableFrom(cls)) {
            return null;
        }
        throw new ContentNotFoundException();
    }
}
