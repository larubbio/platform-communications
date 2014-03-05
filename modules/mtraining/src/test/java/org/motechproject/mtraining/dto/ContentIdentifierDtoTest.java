package org.motechproject.mtraining.dto;

import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.UUID;

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


}
