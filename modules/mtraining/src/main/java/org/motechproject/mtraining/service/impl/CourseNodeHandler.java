package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#COURSE}
 */

@Component
public class CourseNodeHandler extends NodeHandler {

    private static Logger logger = LoggerFactory.getLogger(CourseNodeHandler.class);

    @Autowired
    private AllCourses allCourses;

    @Override
    protected void validateNodeData(ContentDto nodeData) {
        CourseDto courseDto = (CourseDto) nodeData;
        CourseStructureValidationResponse validationResponse = validator().validateCourse(courseDto);
        if (!validationResponse.isValid()) {
            String message = String.format("Invalid course: %s", validationResponse.getErrorMessage());
            logger.error(message);
            throw new CourseStructureValidationException(message);
        }
    }

    @Override
    protected Course saveAndRaiseEvent(Node node) {
        CourseDto courseDto = (CourseDto) node.getNodeData();

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Saving course: %s", courseDto.getName()));
        }

        Course course = getCourse(courseDto, getModules(node));
        allCourses.add(course);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved course: %s", course.getContentId()));
        }

        sendEvent(MTrainingEventConstants.COURSE_CREATION_EVENT, course.getContentId(), course.getVersion());
        return course;
    }

    private List<Module> getModules(Node node) {
        return getChildContentNodes(node);
    }

    private Course getCourse(CourseDto courseDto, List<Module> modules) {
        UUID contentId = courseDto.getContentId();
        if (contentId == null) {
            return new Course(courseDto.isActive(), courseDto.getName(), courseDto.getDescription(), modules);
        }

        Course existingCourse = getLatestVersion(allCourses.findByContentId(contentId));
        Course courseToSave = new Course(existingCourse.getContentId(), existingCourse.getVersion(), courseDto.isActive(), courseDto.getName(), courseDto.getDescription(), modules);
        courseToSave.incrementVersion();
        return courseToSave;
    }
}
