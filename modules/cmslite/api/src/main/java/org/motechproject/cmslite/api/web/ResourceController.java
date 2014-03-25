package org.motechproject.cmslite.api.web;

import org.apache.commons.io.IOUtils;
import org.ektorp.AttachmentInputStream;
import org.motechproject.cmslite.api.model.CMSContent;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.service.CMSContentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import static java.util.Locale.getAvailableLocales;
import static org.apache.commons.codec.digest.DigestUtils.md5Hex;
import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.startsWith;
import static org.apache.commons.lang.StringUtils.startsWithIgnoreCase;

@Controller
public class ResourceController {

    private static final String NOT_FOUND_RESPONSE = "Content not found";

    private static final Logger LOG = LoggerFactory.getLogger(ResourceController.class);

    private CMSContentService cmsContentService;

    @Autowired
    public ResourceController(CMSContentService cmsContentService) {
        this.cmsContentService = cmsContentService;
    }

    @RequestMapping(value = "/resource/available/{field}", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> availableField(@PathVariable String field, @RequestParam String term) {
        Set<String> strings = new TreeSet<>();

        switch (field) {
            case "name":
                for (CMSContent content : cmsContentService.retrieveAll()) {
                    if (startsWith(content.getName(), term)) {
                        strings.add(content.getName());
                    }
                }
                break;
            case "language":
                for (Locale locale : getAvailableLocales()) {
                    if (startsWithIgnoreCase(locale.getDisplayLanguage(), term)) {
                        strings.add(locale.getDisplayLanguage());
                    }
                }

                break;
            default:
        }

        return strings;
    }

    @RequestMapping(value = "/resource", method = RequestMethod.GET)
    @ResponseBody
    public Resources getContents(GridSettings settings) {
        List<CMSContent> contents = cmsContentService.retrieveAll();
        List<ResourceDto> resourceDtos = ResourceFilter.filter(settings, contents);

        Collections.sort(resourceDtos, new ResourceComparator(settings));

        return new Resources(settings, resourceDtos);
    }

    @RequestMapping(value = "/resource/all/languages", method = RequestMethod.GET)
    @ResponseBody
    public Set<String> getAllLanguages() throws ContentNotFoundException {
        List<CMSContent> contents = cmsContentService.retrieveAll();
        Set<String> strings = new TreeSet<>();

        for (CMSContent content : contents) {
            strings.add(content.getLanguage());
        }

        return strings;
    }

    @RequestMapping(value = "/resource/{type}/{language}/{name}", method = RequestMethod.GET)
    @ResponseBody
    public CMSContent getContent(@PathVariable String type, @PathVariable String language, @PathVariable String name) throws ContentNotFoundException {
        CMSContent content = null;

        switch (type) {
            case "stream":
                content = null;
                break;
            case "string":
                content = null;
                break;
            default:
        }

        return content;
    }

    @RequestMapping(value = "/resource/string/{language}/{name}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void editStringContent(@PathVariable String language, @PathVariable String name,
                                  @RequestParam String value) throws ContentNotFoundException, CMSLiteException, IOException {
        CMSContent content = cmsContentService.byNameAndLanguage(language, name).get(0);
        content.setValue(value);

        cmsContentService.create(content);
    }

    @RequestMapping(value = "/resource/stream/{language}/{name}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void editStreamContent(@PathVariable String language, @PathVariable String name,
                                  @RequestParam MultipartFile contentFile) throws ContentNotFoundException, CMSLiteException, IOException {
        CMSContent content = cmsContentService.byNameAndLanguage(language, name).get(0);

        try (InputStream inputStream = contentFile.getInputStream()) {
            content.setChecksum(md5Hex(contentFile.getBytes()));
            content.setContentType(contentFile.getContentType());
            content.setInputStream(inputStream);

            cmsContentService.create(content);
        }
    }

    @RequestMapping(value = "/resource", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.OK)
    public void addContent(@RequestParam String type,
                           @RequestParam String name,
                           @RequestParam String language,
                           @RequestParam(required = false) String value,
                           @RequestParam(required = false) MultipartFile contentFile) throws CMSLiteException, IOException {
        if (isBlank(type)) {
            throw new CMSLiteException("Resource type is required");
        }

        if (isBlank(name)) {
            throw new CMSLiteException("Resource name is required");
        }

        if (isBlank(language)) {
            throw new CMSLiteException("Resource language is required");
        }

        switch (type) {
            case "string":
                if (isBlank(value)) {
                    throw new CMSLiteException("Resource content is required");
                }

                if (cmsContentService.byNameAndLanguage(language, name) != null) {
                    throw new CMSLiteException(String.format("Resource %s in %s language already exists.", name, language));
                }

                cmsContentService.create(new CMSContent(language, name, null, value, null, null, null));
                break;
            case "stream":
                if (null == contentFile) {
                    throw new CMSLiteException("Resource content is required");
                }

                if (cmsContentService.byNameAndLanguage(language, name) != null) {
                    throw new CMSLiteException(String.format("Resource %s in %s language already exists.", name, language));
                }

                try (InputStream inputStream = contentFile.getInputStream()) {
                    String checksum = md5Hex(contentFile.getBytes());
                    String contentType = contentFile.getContentType();

                    cmsContentService.create(new CMSContent(language, name, null, null, checksum, contentType, inputStream));
                }
                break;
            default:
        }
    }

    @RequestMapping(value = "/resource/{type}/{language}/{name}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void removeContent(@PathVariable String type, @PathVariable String language, @PathVariable String name) throws ContentNotFoundException {
        cmsContentService.delete(cmsContentService.byNameAndLanguage(language, name).get(0));
    }

    @RequestMapping(value = "/stream/{language}/{name}", method = RequestMethod.GET)
    public void getStreamContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : stream:%s:%s", language, name));

        OutputStream out = null;
        AttachmentInputStream contentStream = null;

        try {
            out = response.getOutputStream();

            contentStream = (AttachmentInputStream) cmsContentService.byNameAndLanguage(language, name).get(0).getInputStream();

            response.setContentLength((int) contentStream.getContentLength());
            response.setContentType(contentStream.getContentType());
            response.setHeader("Accept-Ranges", "bytes");
            response.setStatus(HttpServletResponse.SC_OK);

            IOUtils.copy(contentStream, out);
        } catch (Exception e) {
            LOG.error(String.format("Content not found for : stream:%s:%s%n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(contentStream);
            IOUtils.closeQuietly(out);
        }
    }

    @RequestMapping(value = "/string/{language}/{name}", method = RequestMethod.GET)
    public void getStringContent(@PathVariable String language, @PathVariable String name, HttpServletResponse response)
            throws IOException {
        LOG.info(String.format("Getting resource for : string:%s:%s", language, name));

        PrintWriter writer = null;

        try {
            writer = response.getWriter();

            CMSContent stringContent = cmsContentService.byNameAndLanguage(language, name).get(0);

            response.setContentLength(stringContent.getValue().length());
            response.setContentType("text/plain");
            response.setStatus(HttpServletResponse.SC_OK);

            writer.print(stringContent.getValue());
        } catch (Exception e) {
            LOG.error(String.format("Content not found for : string:%s:%s%n:%s", language, name,
                    Arrays.toString(e.getStackTrace())));
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, NOT_FOUND_RESPONSE);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    @ExceptionHandler({ContentNotFoundException.class, CMSLiteException.class, IOException.class })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleException(Exception e) {
        return e.getMessage();
    }

}
