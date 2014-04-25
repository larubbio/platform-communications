package org.motechproject.mtraining.exception;

public class BookmarkNotFoundException extends IllegalStateException {

    /**
     * Exception thrown when a bookmark is not found for a given External ID.
     */

    public BookmarkNotFoundException(String externalId) {
        super(String.format("No Bookmark found for external id %s ", externalId));
    }
}
