package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;

/**
 * Service Interface that exposes APIs to manage bookmarks of callers
 */
public interface BookmarkService {

    BookmarkDto getBookmark(String externalId);

    void addBookmark(String externalId, ContentIdentifierDto courseIdentifier);

    void update(BookmarkDto bookmark);
}
