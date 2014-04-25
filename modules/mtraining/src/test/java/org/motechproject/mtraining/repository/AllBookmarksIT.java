package org.motechproject.mtraining.repository;


import org.hamcrest.core.Is;
import org.hamcrest.core.IsNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.mtraining.builder.TestBookmarkBuilder;
import org.motechproject.mtraining.domain.Bookmark;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.UUID;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

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
        String someExternalId = "someExternalId";
        Bookmark bookmark = new TestBookmarkBuilder().withExternalId(someExternalId).withQuiz(UUID.randomUUID()).build();
        allBookmarks.add(bookmark);

        Bookmark bookmarkByExternalId = allBookmarks.findBy(someExternalId);

        assertNotNull(bookmarkByExternalId);
    }

    @Test
    public void shouldAddAndUpdateBookmark() {
        ContentIdentifier content = new ContentIdentifier(UUID.randomUUID(), 1);
        String someExternalId = "someExternalId";
        assertThat(allBookmarks.findBy(someExternalId), IsNull.nullValue());

        Bookmark bookmark = new TestBookmarkBuilder().withCourse(content.getContentId())
                .withExternalId(someExternalId).withoutQuiz().build();
        allBookmarks.add(bookmark);

        Bookmark bookmarkByExternalId = allBookmarks.findBy(someExternalId);

        assertThat(bookmarkByExternalId.getExternalId(), Is.is(someExternalId));
        assertThat(bookmarkByExternalId.getCourse().getContentId(), Is.is(content.getContentId()));
        assertThat(bookmarkByExternalId.getCourse().getVersion(), Is.is(content.getVersion()));

        bookmarkByExternalId.updateCourseVersion(2);

        allBookmarks.update(bookmarkByExternalId);

        bookmarkByExternalId = allBookmarks.findBy(someExternalId);

        assertNotNull(bookmarkByExternalId);
        assertThat(bookmarkByExternalId.getCourse().getContentId(), Is.is(content.getContentId()));
        assertThat(bookmarkByExternalId.getCourse().getVersion(), Is.is(2));

    }
}
