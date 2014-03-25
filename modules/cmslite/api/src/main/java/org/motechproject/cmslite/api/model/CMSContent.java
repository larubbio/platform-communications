package org.motechproject.cmslite.api.model;

import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Ignore;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * Abstract representation of CMS Lite content. Identified by name and language.
 */
@Entity
public class CMSContent  {
    private static final long serialVersionUID = 753195533829136573L;

    private String language;
    private String name;
    private String value;
    private String checksum;
    private String contentType;
    @Ignore
    private Map<String, String> metadata;

    @Ignore
    private InputStream inputStream;

    public CMSContent() {
        this(null, null, null);
    }

    public CMSContent(String language, String name) {
        this(language, name, null);
    }

    public CMSContent(String language, String name, Map<String, String> metadata) {
        this(language, name, metadata, "", "", "", null);
    }

    public CMSContent(String language, String name, Map<String, String> metadata, String value, String checksum, String contentType,  InputStream inputStream) {
        this.language = language;
        this.name = name;
        this.value = value;
        this.checksum = checksum;
        this.contentType = contentType;
        this.metadata = metadata;
        this.inputStream = inputStream;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    @Ignore
    public Map<String, String> getMetadata() {
        return metadata;
    }

    @Ignore
    public InputStream getInputStream() {
        return inputStream;
    }

    @Ignore
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public int hashCode() {
        return Objects.hash(language, name, metadata);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final CMSContent other = (CMSContent) obj;

        return Objects.equals(this.language, other.language) &&
                Objects.equals(this.name, other.name) &&
                Objects.equals(this.value, other.value) &&
                Objects.equals(this.checksum, other.checksum) &&
                Objects.equals(this.contentType, other.contentType) &&
                Objects.equals(this.inputStream, other.inputStream) &&
                Objects.equals(this.metadata, other.metadata);
    }

    @Override
    public String toString() {
        return String.format("CMSContent{language='%s', name='%s', metadata=%s, value=%s, checksum=%s, contentType=%s}",
                language, name, metadata, value, checksum, contentType);
    }
}
