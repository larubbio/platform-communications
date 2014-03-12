package org.motechproject.mtraining.validator;

import org.apache.commons.lang.StringUtils;
import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllContents;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.repository.AllModules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Validator for validating fields of different DTOs which represent the course structure:
 * {@link CourseDto}
 * {@link ModuleDto}
 * {@link ChapterDto}
 * {@link MessageDto}
 */

@Component
public class CourseStructureValidator {
    private static Logger logger = LoggerFactory.getLogger(CourseStructureValidator.class);

    @Autowired
    private AllCourses allCourses;
    @Autowired
    private AllModules allModules;
    @Autowired
    private AllChapters allChapters;
    @Autowired
    private AllMessages allMessages;

    public CourseStructureValidationResponse validateMessage(MessageDto message) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (message == null) {
            validationResponse.addError("Message should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Message: %s", message.getName()));
        }
        validateName(message.getName(), validationResponse, "Message name should not be blank");
        validateExternalId(message.getExternalId(), validationResponse);
        validateIfContentExists(message.getContentId(), allMessages, validationResponse, "Message does not exist for given contentId");

        return validationResponse;
    }

    public CourseStructureValidationResponse validateChapter(ChapterDto chapter) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (chapter == null) {
            validationResponse.addError("Chapter should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Chapter: %s", chapter.getName()));
        }
        validateName(chapter.getName(), validationResponse, "Chapter name should not be blank");
        validateIfContentExists(chapter.getContentId(), allChapters, validationResponse, "Chapter does not exist for given contentId");

        return validationResponse;
    }

    public CourseStructureValidationResponse validateModule(ModuleDto module) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (module == null) {
            validationResponse.addError("Module should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Module: %s", module.getName()));
        }
        validateName(module.getName(), validationResponse, "Module name should not be blank");
        validateIfContentExists(module.getContentId(), allModules, validationResponse, "Module does not exist for given contentId");

        return validationResponse;
    }

    public CourseStructureValidationResponse validateCourse(CourseDto course) {
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse();
        if (course == null) {
            validationResponse.addError("Course should not be null");
            return validationResponse;
        }
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Validating Course: %s", course.getName()));
        }
        validateName(course.getName(), validationResponse, "Course name should not be blank");
        validateIfContentExists(course.getContentId(), allCourses, validationResponse, "Course does not exist for given contentId");

        return validationResponse;
    }

    private void validateName(String chapterName, CourseStructureValidationResponse validationResponse, String errorMessage) {
        if (StringUtils.isBlank(chapterName)) {
            validationResponse.addError(errorMessage);
        }
    }

    private void validateExternalId(String fileName, CourseStructureValidationResponse validationResponse) {
        if (StringUtils.isBlank(fileName)) {
            validationResponse.addError("ExternalId should not be blank for a message");
        }
    }

    private <T extends Content> void validateIfContentExists(UUID contentId, AllContents<T> allContents, CourseStructureValidationResponse validationResponse, String errorMessage) {
        if (contentId == null) {
            return;
        }
        List<T> existingCourses = allContents.findByContentId(contentId);
        if (existingCourses.isEmpty()) {
            validationResponse.addError(errorMessage + ": " + contentId);
        }
    }
}
