package org.motechproject.mtraining.validator;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

public class CourseStructureValidatorTest {

    private CourseStructureValidator courseStructureValidator;
    private ContentIdentifierDto contentIdentifier;

    @Before
    public void setUp() throws Exception {
        courseStructureValidator = new CourseStructureValidator();
        contentIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldValidateIfMessageIsNotNull() {
        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(null);

        assertFalse(validationResponse.isValid());
        assertEquals("Message should not be null", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateMessageNameAndExternalIdForAMessage() {
        MessageDto invalidMessage = new MessageDto();

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(invalidMessage);
        assertFalse(validationResponse.isValid());
        assertEquals("Message name should not be blank,ExternalId should not be blank for a message", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldNotReturnAnyErrorsForAValidMessage() {
        MessageDto validMessage = new MessageDto("name", "externalId", "description", contentIdentifier);

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(validMessage);
        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrorMessage().isEmpty());
    }

    @Test
    public void shouldValidateIfChapterIsNotNull() {
        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateChapter(null);

        assertFalse(validationResponse.isValid());
        assertEquals("Chapter should not be null", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateIfChapterHasValidName() {
        ChapterDto invalidChapter = new ChapterDto();

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateChapter(invalidChapter);

        assertFalse(validationResponse.isValid());
        assertEquals("Chapter name should not be blank", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldNotReturnAnyErrorsForValidChapter() {
        ChapterDto invalidChapter = new ChapterDto("name", "desc", contentIdentifier, asList(new MessageDto()));

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateChapter(invalidChapter);

        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrorMessage().isEmpty());
    }

    @Test
    public void shouldValidateIfModuleIsNotNull() {
        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateModule(null);

        assertFalse(validationResponse.isValid());
        assertEquals("Module should not be null", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateIfModuleHasValidName() {
        ModuleDto invalidModule = new ModuleDto();

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateModule(invalidModule);

        assertFalse(validationResponse.isValid());
        assertEquals("Module name should not be blank", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldNotReturnAnyErrorsForValidModule() {
        ModuleDto invalidModule = new ModuleDto("name", "desc", contentIdentifier, asList(new ChapterDto()));

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateModule(invalidModule);

        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrorMessage().isEmpty());
    }

    @Test
    public void shouldValidateIfCourseIsNotNull() {
        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateCourse(null);

        assertFalse(validationResponse.isValid());
        assertEquals("Course should not be null", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateIfCourseHasValidName() {
        CourseDto invalidCourse = new CourseDto();

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateCourse(invalidCourse);

        assertFalse(validationResponse.isValid());
        assertEquals("Course name should not be blank", validationResponse.getErrorMessage());
    }

    @Test
    public void shouldNotReturnAnyErrorsForValidCourse() {
        CourseDto invalidCourse = new CourseDto("name", "desc", contentIdentifier, asList(new ModuleDto()));

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateCourse(invalidCourse);

        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrorMessage().isEmpty());
    }
}
