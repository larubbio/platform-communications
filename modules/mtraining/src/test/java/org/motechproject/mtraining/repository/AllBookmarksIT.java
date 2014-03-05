package org.motechproject.mtraining.repository;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class AllBookmarksIT {

    @Autowired
    AllBookmarks allBookmarks;

    @Before
    @After
    public void clearData() throws Exception {
        allBookmarks.removeAll();
    }

    @Test
    public void shouldReturnBookmarkByExternalId() {
        ContentIdentifier content = new ContentIdentifier(UUID.randomUUID(), 1);
        String someExternalId = "someExternalId";
        Bookmark bookmark = new Bookmark(someExternalId, content, content, content, content);
        allBookmarks.add(bookmark);

        Bookmark bookmarkByExternalId = allBookmarks.findBy(someExternalId);

        assertNotNull(bookmarkByExternalId);
    }

    @Test
    public void anc() {
        ContentIdentifier content = new ContentIdentifier(UUID.randomUUID(), 1);
        String someExternalId = "someExternalId";
        Bookmark bookmark = new Bookmark(someExternalId, content, content, content, content);
        allBookmarks.add(bookmark);

        Bookmark bookmarkByExternalId = allBookmarks.findBy(someExternalId);
        bookmarkByExternalId.setCourse(null);

        allBookmarks.update(bookmarkByExternalId);

        bookmarkByExternalId = allBookmarks.findBy(someExternalId);

        assertNotNull(bookmarkByExternalId);
        assertNull(bookmarkByExternalId.getCourse());

    }
}
