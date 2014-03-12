package org.motechproject.mtraining.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.junit.Assert;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class AssertCourseContents {


    public static void assertCourse(CourseDto courseDto, List<Course> coursesInDb, List<Module> modulesInDb) {
        assertEquals(1, coursesInDb.size());
        Course courseInDb = coursesInDb.get(0);
        assertEquals(courseDto.getName(), courseInDb.getName());
        assertChildNodes(courseInDb.getModules(), modulesInDb);
    }

    public static void assertMessage(List<MessageDto> messageDtos, List<Message> messagesInDb) {
        assertMessageWithExistingMessage(messageDtos, messagesInDb, 0);
    }

    public static void assertMessageWithExistingMessage(List<MessageDto> messageDtos, List<Message> messagesInDb, int existingMessageCount) {
        assertEquals(messageDtos.size() + existingMessageCount, messagesInDb.size());

        for (int i = 0; i < messageDtos.size(); i++) {
            Message messageInDb = messagesInDb.get(existingMessageCount + i);
            assertEquals(messageDtos.get(i).getName(), messageInDb.getName());
        }
    }

    public static void assertChapter(List<ChapterDto> chapterDtos, List<Chapter> chaptersInDb, List<Message> messagesInDb) {
        assertChapterWithExistingChapter(chapterDtos, chaptersInDb, messagesInDb, 0);
    }

    public static void assertChapterWithExistingChapter(List<ChapterDto> chapterDtos, List<Chapter> chaptersInDb, List<Message> messagesInDb, int existingChapterCount) {
        assertEquals(chapterDtos.size() + existingChapterCount, chaptersInDb.size());

        for (int i = 0; i < chapterDtos.size(); i++) {
            Chapter chapterInDb = chaptersInDb.get(existingChapterCount + i);
            assertEquals(chapterDtos.get(i).getName(), chapterInDb.getName());
            assertChildNodes(chapterInDb.getMessages(), messagesInDb);
        }
    }

    public static void assertModule(List<ModuleDto> moduleDtos, List<Module> modulesInDb, List<Chapter> chaptersInDb) {
        assertModuleWithExistingModule(moduleDtos, modulesInDb, chaptersInDb, 0);
    }

    public static void assertModuleWithExistingModule(List<ModuleDto> moduleDtos, List<Module> modulesInDb, List<Chapter> chaptersInDb, int existingModuleCount) {
        assertEquals(moduleDtos.size() + existingModuleCount, modulesInDb.size());

        for (int i = 0; i < moduleDtos.size(); i++) {
            Module moduleInDb = modulesInDb.get(existingModuleCount + i);
            assertEquals(moduleDtos.get(i).getName(), moduleInDb.getName());
            assertChildNodes(moduleInDb.getChapters(), chaptersInDb);
        }
    }

    public static <T extends Content> void assertChildNodes(List<ContentIdentifier> childContentIdentifiers, List<T> childNodesInDb) {
        for (final ContentIdentifier childContentIdentifier : childContentIdentifiers) {
            boolean childNodeExist = CollectionUtils.exists(childNodesInDb, new Predicate() {
                @Override
                public boolean evaluate(Object object) {
                    Content content = (Content) object;
                    return content.getContentId().equals(childContentIdentifier.getContentId()) && content.getVersion().equals(childContentIdentifier.getVersion());
                }
            });
            assertTrue(childNodeExist);
        }
    }

    public static void assertContentIdentifier(ContentIdentifierDto returnedContent, Content contentInDb) {
        assertEquals(contentInDb.getContentId(), returnedContent.getContentId());
        assertEquals(contentInDb.getVersion(), returnedContent.getVersion());
    }

    public static void assertContentIdUpdate(Content existingContent, Content savedContent) {
        Assert.assertEquals(existingContent.getContentId(), savedContent.getContentId());
        Assert.assertEquals(existingContent.getVersion() + 1, savedContent.getVersion().intValue());
    }
}
