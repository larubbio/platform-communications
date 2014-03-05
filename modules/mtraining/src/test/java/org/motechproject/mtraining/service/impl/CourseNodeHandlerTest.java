package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import java.util.Collections;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseNodeHandlerTest {
    @InjectMocks
    private CourseNodeHandler courseNodeHandler = new CourseNodeHandler();
    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllCourses allCourses;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ContentIdentifierDto courseIdentifier;

    @Before
    public void setUp() throws Exception {
        courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldValidateGivenCourseDtoAndThrowExceptionIfInvalid() {
        CourseDto courseDto = new CourseDto("name", "description", courseIdentifier, Collections.EMPTY_LIST);
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateCourse(courseDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid course: some validation error");

        courseNodeHandler.validateNodeData(courseDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenCourseDtoIsValid() {
        CourseDto courseDto = new CourseDto("name", "description", courseIdentifier, asList(new ModuleDto()));
        when(courseStructureValidator.validateCourse(courseDto)).thenReturn(new CourseStructureValidationResponse(true));

        courseNodeHandler.validateNodeData(courseDto);
    }

    @Test
    public void shouldSaveTheGivenCourseDtoAsCourseEntityWithModulesAndRaiseEvent() {
        Node moduleNode1 = new Node(NodeType.MESSAGE, new ModuleDto());
        Module expectedModuleForTheCourse = new Module("", "", Collections.EMPTY_LIST);
        moduleNode1.setPersistentEntity(expectedModuleForTheCourse);
        Node moduleNode2 = new Node(NodeType.MESSAGE, new ModuleDto());
        CourseDto courseDto = new CourseDto("name", "description", courseIdentifier, asList(new ModuleDto()));
        Node courseNode = new Node(NodeType.CHAPTER, courseDto, asList(moduleNode1, moduleNode2));

        courseNodeHandler.saveAndRaiseEvent(courseNode);

        InOrder inOrder = inOrder(allCourses, eventRelay);
        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
        inOrder.verify(allCourses).add(courseArgumentCaptor.capture());
        Course savedCourse = courseArgumentCaptor.getValue();
        assertCourseDetails(courseDto, expectedModuleForTheCourse, savedCourse);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedCourse, raisedEvent);
    }

    private void assertCourseDetails(CourseDto courseDto, Module expectedModule, Course savedCourse) {
        assertEquals(savedCourse.getName(), courseDto.getName());
        assertEquals(savedCourse.getDescription(), courseDto.getDescription());
        assertEquals(1, savedCourse.getModules().size());
        assertEquals(expectedModule.getContentId(), savedCourse.getModules().get(0).getContentId());
        assertEquals(expectedModule.getVersion(), savedCourse.getModules().get(0).getVersion());
        assertNotNull(savedCourse.getContentId());
        assertEquals(courseIdentifier.getVersion(), savedCourse.getVersion());
    }

    private void assertEventDetails(Course savedCourse, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.COURSE_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(2, raisedEvent.getParameters().size());
        assertEquals(savedCourse.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.CONTENT_ID));
        assertEquals(savedCourse.getVersion(), raisedEvent.getParameters().get(MTrainingEventConstants.VERSION));
    }
}
