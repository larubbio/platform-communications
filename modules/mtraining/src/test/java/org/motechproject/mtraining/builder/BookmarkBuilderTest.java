package org.motechproject.mtraining.builder;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.dto.AnswerDto;
import org.motechproject.mtraining.dto.BookmarkDto;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.dto.QuestionDto;
import org.motechproject.mtraining.dto.QuizDto;

import java.util.Arrays;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class BookmarkBuilderTest {

    @Test
    public void shouldBuildBookmarkFromMessageWhenBothMessageAndQuizArePresent() {
        MessageDto activeMessage = new MessageDto(UUID.randomUUID(), 1, true, "ms001", "desc1", "aud001", "auth");
        MessageDto inactiveMessage = new MessageDto(UUID.randomUUID(), 1, false, "ms002", "desc2", "aud002", "auth");

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto activeQuiz = new QuizDto(UUID.randomUUID(), 1, true, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");


        ChapterDto chapter01 = new ChapterDto(UUID.randomUUID(), 3, true, "ch001", "desc", "externalId", "auth",
                asList(activeMessage, inactiveMessage), activeQuiz);


        ModuleDto module01 = new ModuleDto(UUID.randomUUID(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));


        CourseDto course01 = new CourseContentBuilder().withContentId(UUID.randomUUID())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        BookmarkDto bookmark = new BookmarkBuilder().buildBookmarkFromFirstActiveContent("roll001", course01, module01, chapter01);

        assertThat(bookmark.getExternalId(), Is.is("roll001"));
        assertThat(bookmark.getMessage().getContentId(), Is.is(activeMessage.getContentId()));
        assertThat(bookmark.getMessage().getVersion(), Is.is(activeMessage.getVersion()));

    }

    @Test
    public void shouldBuildBookmarkFromQuizWhenBothMessageIsInactiveAndQuizIsActive() {
        MessageDto msg01 = new MessageDto(UUID.randomUUID(), 1, false, "ms001", "desc1", "aud001", "auth");
        MessageDto msg02 = new MessageDto(UUID.randomUUID(), 1, false, "ms002", "desc2", "aud002", "auth");

        QuestionDto questionDto = new QuestionDto(UUID.randomUUID(), 1, true, "ques001", "desc", "ex01", new AnswerDto("A", "ans01"), Arrays.asList("A", "B", "C"), "auth");
        QuizDto activeQuiz = new QuizDto(UUID.randomUUID(), 1, true, "quiz001", "externalId", Arrays.asList(questionDto), 0, 100.0, "auth");


        ChapterDto chapter01 = new ChapterDto(UUID.randomUUID(), 3, true, "ch001", "desc", "externalId", "auth",
                asList(msg01, msg02), activeQuiz);


        ModuleDto module01 = new ModuleDto(UUID.randomUUID(), 2, true, "mod001", "des", "externalId", "auth", asList(chapter01));


        CourseDto course01 = new CourseContentBuilder().withContentId(UUID.randomUUID())
                .withVersion(4)
                .withModuleDtos(asList(module01))
                .buildCourseDTO();

        BookmarkDto bookmark = new BookmarkBuilder().buildBookmarkFromFirstActiveContent("roll001", course01, module01, chapter01);
        assertThat(bookmark.getExternalId(), Is.is("roll001"));

        assertNull(bookmark.getMessage());
        assertThat(bookmark.getQuiz().getContentId(), Is.is(activeQuiz.getContentId()));
        assertThat(bookmark.getQuiz().getVersion(), Is.is(activeQuiz.getVersion()));

    }
}
