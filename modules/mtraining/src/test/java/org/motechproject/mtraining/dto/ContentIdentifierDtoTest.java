package org.motechproject.mtraining.dto;

import org.hamcrest.core.Is;
import org.junit.Test;
import org.motechproject.mtraining.builder.QuizContentBuilder;
import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Quiz;

import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

public class ContentIdentifierDtoTest {

    @Test
    public void shouldTestEquality() {
        UUID contentId = UUID.randomUUID();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        assertThat(contentIdentifierDto.equals(null), Is.is(false));

        ContentIdentifierDto similarContentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        assertThat(contentIdentifierDto.equals(similarContentIdentifierDto), Is.is(true));

        ContentIdentifierDto identifierDtoWithDifferentContentID = new ContentIdentifierDto(UUID.randomUUID(), 2);
        assertThat(contentIdentifierDto.equals(identifierDtoWithDifferentContentID), Is.is(false));

        ContentIdentifierDto identifierDtoWithoutContentID = new ContentIdentifierDto(null, 2);
        assertThat(contentIdentifierDto.equals(identifierDtoWithoutContentID), Is.is(false));

        ContentIdentifierDto identifierDtoWithDifferentVersion = new ContentIdentifierDto(contentId, 3);
        assertThat(contentIdentifierDto.equals(identifierDtoWithDifferentVersion), Is.is(false));

        ContentIdentifierDto identifierDtoWithoutVersion = new ContentIdentifierDto(contentId, null);
        assertThat(contentIdentifierDto.equals(identifierDtoWithoutVersion), Is.is(false));

    }

    @Test
    public void shouldTestThatEqualContentIdentifierDTOsHaveSameHashCodes() {
        UUID contentId = UUID.randomUUID();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        ContentIdentifierDto similarContentIdentifierDto = new ContentIdentifierDto(contentId, 1);
        assertThat(contentIdentifierDto.hashCode(), Is.is(similarContentIdentifierDto.hashCode()));
    }

    @Test
    public void shouldTestThatFindContentByContentIdReturnsNullIfNotFound() {
        ContentIdentifier contentIdentifier = new ContentIdentifier(UUID.randomUUID(), 1);
        Quiz quiz1 = new QuizContentBuilder().
                withContentId(contentIdentifier.getContentId())
                .withVersion(contentIdentifier.getVersion())
                .buildQuiz();
        Quiz quiz2 = new QuizContentBuilder().
                withContentId(contentIdentifier.getContentId())
                .withVersion(contentIdentifier.getVersion())
                .buildQuiz();
        Quiz quizByContentId = (Quiz) Content.findContentByContentId(newArrayList(quiz1, quiz2), UUID.randomUUID());
        assertNull(quizByContentId);
    }

    @Test
    public void shouldTestThatFindContentByContentIdReturnsContentIfFound() {
        UUID contentId = UUID.randomUUID();
        ContentIdentifier contentIdentifier = new ContentIdentifier(contentId, 1);
        Quiz quiz1 = new QuizContentBuilder().
                withContentId(contentIdentifier.getContentId())
                .withVersion(contentIdentifier.getVersion())
                .buildQuiz();
        Quiz quiz2 = new QuizContentBuilder().
                withContentId(contentIdentifier.getContentId())
                .withVersion(contentIdentifier.getVersion())
                .buildQuiz();
        Quiz quizByContentId = (Quiz) Content.findContentByContentId(newArrayList(quiz1, quiz2), contentId);
        assertNotNull(quizByContentId);
    }

}
