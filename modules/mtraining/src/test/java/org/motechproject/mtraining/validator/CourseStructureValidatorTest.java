package org.motechproject.mtraining.validator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.CourseContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.repository.AllModules;

import java.util.Collections;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseStructureValidatorTest {

    @Mock
    private AllCourses allCourses;
    @Mock
    private AllModules allModules;
    @Mock
    private AllChapters allChapters;
    @Mock
    private AllMessages allMessages;

    @InjectMocks
    private CourseStructureValidator courseStructureValidator = new CourseStructureValidator();
    private MessageContentBuilder messageContentBuilder;
    private ChapterContentBuilder chapterContentBuilder;
    private ModuleContentBuilder moduleContentBuilder;
    private CourseContentBuilder courseContentBuilder;

    @Before
    public void before() {
        messageContentBuilder = new MessageContentBuilder();
        chapterContentBuilder = new ChapterContentBuilder();
        moduleContentBuilder = new ModuleContentBuilder();
        courseContentBuilder = new CourseContentBuilder();
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
        MessageDto validMessage = messageContentBuilder.withName("name").withDescription("description").withAudioFile("hello.wav").buildMessageDTO();

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
        ChapterDto invalidChapter = chapterContentBuilder.withName("name").withDescription("desc").buildChapterDTO();

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
        ModuleDto invalidModule = moduleContentBuilder.buildModuleDTO();

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
        CourseDto invalidCourse = new CourseContentBuilder().buildCourseDTO();

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateCourse(invalidCourse);

        assertTrue(validationResponse.isValid());
        assertTrue(validationResponse.getErrorMessage().isEmpty());
    }

    @Test
    public void shouldValidateIfMessageExistsByContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        MessageDto messageDto = messageContentBuilder
                .withVersion(1)
                .withContentId(contentId)
                .withName("name")
                .withAudioFile("hello.wav")
                .withDescription("desc")
                .buildMessageDTO();
        when(allMessages.findByContentId(contentId)).thenReturn(Collections.EMPTY_LIST);

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(messageDto);

        assertFalse(validationResponse.isValid());
        assertEquals("Message does not exist for given contentId: " + contentId, validationResponse.getErrorMessage());
    }

    @Test
    public void shouldNotReturnAnyErrorIfMessageExistsByContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        MessageDto messageDto = messageContentBuilder
                .withContentId(contentId)
                .withVersion(1)
                .withName("name")
                .withAudioFile("hello.wav")
                .withDescription("desc")
                .buildMessageDTO();

        when(allMessages.findByContentId(contentId)).thenReturn(asList(new MessageContentBuilder().buildMessage()));

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(messageDto);

        assertTrue(validationResponse.isValid());
    }

    @Test
    public void shouldNotCheckIfMessageExistsIfContentIdIsNotProvidedWithDto() {
        MessageDto messageDto = messageContentBuilder.buildMessageDTO();
        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateMessage(messageDto);

        verifyZeroInteractions(allMessages);
        assertTrue(validationResponse.isValid());
    }

    @Test
    public void shouldValidateIfChapterExistsByContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        ChapterDto chapterDto = chapterContentBuilder
                .withContentId(contentId)
                .withVersion(1)
                .buildChapterDTO();
        when(allChapters.findByContentId(contentId)).thenReturn(Collections.EMPTY_LIST);

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateChapter(chapterDto);

        assertFalse(validationResponse.isValid());
        assertEquals("Chapter does not exist for given contentId: " + contentId, validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateIfModuleExistsByContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        ModuleDto moduleDto = moduleContentBuilder
                .withContentId(contentId)
                .withVersion(1)
                .buildModuleDTO();
        when(allModules.findByContentId(contentId)).thenReturn(Collections.EMPTY_LIST);

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateModule(moduleDto);

        assertFalse(validationResponse.isValid());
        assertEquals("Module does not exist for given contentId: " + contentId, validationResponse.getErrorMessage());
    }

    @Test
    public void shouldValidateIfCourseExistsByContentIdWhenContentIdIsProvidedWithDto() {
        UUID contentId = UUID.randomUUID();
        CourseDto courseDto = courseContentBuilder
                .withContentId(contentId)
                .withVersion(1)
                .buildCourseDTO();
        when(allCourses.findByContentId(contentId)).thenReturn(Collections.EMPTY_LIST);

        CourseStructureValidationResponse validationResponse = courseStructureValidator.validateCourse(courseDto);

        assertFalse(validationResponse.isValid());
        assertEquals("Course does not exist for given contentId: " + contentId, validationResponse.getErrorMessage());
    }
}
