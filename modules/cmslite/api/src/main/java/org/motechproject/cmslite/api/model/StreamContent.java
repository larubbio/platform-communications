package org.motechproject.cmslite.api.model;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.ektorp.support.TypeDiscriminator;

import java.io.InputStream;

/**
 * \ingroup cmslite
 * Represents stream content along with checksum.
 */
@TypeDiscriminator("doc.type === 'StreamContent'")
public class StreamContent extends Content {
    private static final long serialVersionUID = 8169367710567919494L;

    private InputStream inputStream;
    @JsonProperty
    private String checksum;
    @JsonProperty
    private String contentType;

    public StreamContent() {
    }

    public StreamContent(String language, String name, InputStream inputStream, String checksum, String contentType) {
        super(language, name);
        this.inputStream = inputStream;
        this.checksum = checksum;
        this.contentType = contentType;
    }

    @JsonIgnore
    public InputStream getInputStream() {
        return inputStream;
    }

    public String getChecksum() {
        return checksum;
    }

    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public String getContentType() {
        return contentType;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}
