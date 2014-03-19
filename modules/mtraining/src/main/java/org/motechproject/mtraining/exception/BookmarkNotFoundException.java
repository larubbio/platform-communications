package org.motechproject.mtraining.exception;

public class BookmarkNotFoundException extends IllegalStateException {

    public BookmarkNotFoundException(String externalId) {
        super(String.format("No Bookmark found for external id %s ", externalId));
    }
}
