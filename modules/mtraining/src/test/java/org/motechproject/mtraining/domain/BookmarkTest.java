package org.motechproject.mtraining.domain;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.motechproject.mtraining.util.DateTimeUtil;

import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BookmarkTest {

    @Test
    public void shouldIndicateIfBookmarkWasModifiedBeforeOrAfterGivenDate() {
        ContentIdentifier contentIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
        DateTime tenInTheMorning = DateTime.parse("01-01-2001 10:00:00.000", DateTimeFormat.forPattern(DateTimeUtil.DATE_TIME_FORMAT));
        DateTime elevenInTheMorning = tenInTheMorning.plusHours(1);
        DateTime nineInTheMorning = tenInTheMorning.minusHours(1);

        Bookmark bookmarkModifiedAtTen = new Bookmark("001", contentIdentifier, contentIdentifier, contentIdentifier, contentIdentifier, tenInTheMorning);

        assertTrue(bookmarkModifiedAtTen.wasModifiedAfter(nineInTheMorning));
        assertFalse(bookmarkModifiedAtTen.wasModifiedAfter(tenInTheMorning));
        assertFalse(bookmarkModifiedAtTen.wasModifiedAfter(elevenInTheMorning));
    }
}
