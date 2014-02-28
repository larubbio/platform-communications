package org.motechproject.mtraining.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.ChildContentIdentifier;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#COURSE}
 */

@Component
public class CourseNodeHandler extends NodeHandler {

    private static Logger logger = Logger.getLogger(CourseNodeHandler.class);

    @Autowired
    private AllCourses allCourses;

    @Override
    protected void validateNodeData(Object nodeData) {
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

        Course course = new Course(courseDto.getName(), courseDto.getDescription(), getModules(node));
        allCourses.add(course);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved course: %s", course.getContentId()));
        }

        sendEvent(MTrainingEventConstants.COURSE_CREATION_EVENT, course.getContentId());
        return course;
    }

    private List<ChildContentIdentifier> getModules(Node node) {
        return getChildContentIdentifiers(node);
    }
}
