package org.motechproject.mtraining.service.impl;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.CourseContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseNotFoundException;
import org.motechproject.mtraining.exception.CoursePublicationException;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.service.CourseService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mtraining.domain.NodeType.COURSE;
import static org.motechproject.mtraining.domain.NodeType.MODULE;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceImplTest {

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private ModuleServiceImpl moduleService;
    @Mock
    private AllCourses allCourses;

    private CourseService courseService;

    @Before
    public void setUp() {
        courseService = new CourseServiceImpl(nodeHandlerOrchestrator, moduleService, allCourses);
    }

    @Test
    public void shouldConstructCourseNodeWithAllDescendantNodesAndInvokeHandler() {
        List<MessageDto> messageDTOs = getMessageDTOs();
        ChapterDto chapterDto1 = new ChapterContentBuilder().withName("name").withDescription("desc").withMessageDTOs(messageDTOs).buildChapterDTO();
        ChapterDto chapterDto2 = new ChapterContentBuilder().withName("name").withDescription("desc").withMessageDTOs(messageDTOs).buildChapterDTO();
        ModuleDto moduleDto1 = new ModuleContentBuilder().withName("module 01").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();
        ModuleDto moduleDto2 = new ModuleContentBuilder().withName("module 02").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();
        CourseDto courseDto = new CourseContentBuilder().withName("CS 001").withModuleDtos(asList(moduleDto1, moduleDto2)).buildCourseDTO();
        when(moduleService.constructModuleNodes(courseDto.getModules())).thenReturn(newArrayList(new Node(MODULE, moduleDto1), new Node(MODULE, moduleDto2)));

        courseService.addOrUpdateCourse(courseDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualCourseNode = nodeArgumentCaptor.getValue();
        assertEquals(COURSE, actualCourseNode.getNodeType());
        assertEquals(courseDto, actualCourseNode.getNodeData());
        assertModuleNodesForCourse(actualCourseNode, asList(moduleDto1, moduleDto2));
        assertNull(actualCourseNode.getPersistentEntity());
    }

    @Test
    public void shouldGetAllCourses() {
        Module module = new ModuleContentBuilder().buildModule();
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Course course1 = new CourseContentBuilder().withName("course1").withModules(modules).buildCourse();
        Course course2 = new CourseContentBuilder().withName("course2").buildCourse();
        when(allCourses.getAll()).thenReturn(asList(course1, course2));
        when(moduleService.mapToModuleDto(module)).thenReturn(new ModuleContentBuilder().withContentId(module.getContentId()).
                withVersion(module.getVersion()).buildModuleDTO());

        List<CourseDto> allCourseDtos = courseService.getAllCourses();

        assertEquals(2, allCourseDtos.size());
        CourseDto courseDto1 = allCourseDtos.get(0);
        assertCourseDetails(course1, courseDto1);
        assertEquals(module.getContentId(), courseDto1.getModules().get(0).getContentId());
        assertEquals(module.getVersion(), courseDto1.getModules().get(0).getVersion());
        CourseDto courseDto2 = allCourseDtos.get(1);
        assertCourseDetails(course2, courseDto2);
        assertTrue(courseDto2.getModules().isEmpty());
    }

    @Test
    public void shouldReturnCourseDtoIfCourseFound() {
        Course course = new Course(true, "course1", "some description", "externalId", "Author", Collections.<Module>emptyList());
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allCourses.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(course);
        CourseDto courseFromDb = courseService.getCourse(contentIdentifierDto);

        assertEquals(course.getContentId(), courseFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfCourseNotFound() throws Exception {
        UUID contentId = UUID.randomUUID();

        when(allCourses.findLatestPublishedCourse(contentId)).thenReturn(null);
        CourseDto latestPublishedCourse = courseService.getLatestPublishedCourse(contentId);

        verify(allCourses).findLatestPublishedCourse(contentId);
        assertNull(latestPublishedCourse);
    }

    @Test
    public void shouldReturnLatestPublishedCourse() throws Exception {
        UUID contentId = UUID.randomUUID();

        Course course = new CourseContentBuilder().withContentId(contentId).withVersion(3).buildCourse();

        when(allCourses.findLatestPublishedCourse(contentId)).thenReturn(course);
        CourseDto latestPublishedCourse = courseService.getLatestPublishedCourse(contentId);

        verify(allCourses).findLatestPublishedCourse(contentId);
        assertThat(latestPublishedCourse.getContentId(), Is.is(course.getContentId()));
    }

    @Test
    public void shouldReturnNullIfCourseByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allCourses.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        CourseDto courseFromDb = courseService.getCourse(contentIdentifierDto);

        assertNull(courseFromDb);
    }

    @Test
    public void shouldMarkCourseAsPublished() {
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        Course cs001 = new CourseContentBuilder()
                .withContentId(courseIdentifier.getContentId())
                .withVersion(courseIdentifier.getVersion())
                .withName("CS001").buildCourse();

        when(allCourses.findBy(courseIdentifier.getContentId(), courseIdentifier.getVersion())).thenReturn(cs001);

        courseService.publish(courseIdentifier);

        ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);
        verify(allCourses).update(courseArgumentCaptor.capture());

        Course publishedCourse = courseArgumentCaptor.getValue();

        assertThat(publishedCourse.getContentId(), Is.is(cs001.getContentId()));
        assertThat(publishedCourse.getVersion(), Is.is(cs001.getVersion()));
        assertThat(publishedCourse.isPublished(), Is.is(true));

    }

    @Test(expected = CourseNotFoundException.class)
    public void shouldThrowCourseNotFoundExceptionIfCourseToPublishIsNotFound() {
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allCourses.findBy(courseIdentifier.getContentId(), courseIdentifier.getVersion())).thenReturn(null);

        courseService.publish(courseIdentifier);
    }

    @Test(expected = CoursePublicationException.class)
    public void shouldThrowCoursePublicationExceptionIfCourseToPublishIsNotFound() {
        ContentIdentifierDto courseIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);

        Course inactiveCourse = new CourseContentBuilder().asInactive().buildCourse();

        when(allCourses.findBy(courseIdentifier.getContentId(), courseIdentifier.getVersion())).thenReturn(inactiveCourse);

        courseService.publish(courseIdentifier);
    }


    private List<MessageDto> getMessageDTOs() {
        MessageDto messageDto1 = new MessageContentBuilder().withName("ms001").withAudioFile("audio1").buildMessageDTO();
        MessageDto messageDto2 = new MessageContentBuilder().withName("ms002").withAudioFile("audio2").buildMessageDTO();
        return Arrays.asList(messageDto1, messageDto2);
    }

    private void assertCourseDetails(Course course, CourseDto courseDto) {
        assertEquals(course.getContentId(), courseDto.getContentId());
        assertEquals(course.getVersion(), courseDto.getVersion());
        assertEquals(course.getName(), courseDto.getName());
        assertEquals(course.getDescription(), courseDto.getDescription());
    }

    private void assertModuleNodesForCourse(Node courseNode, List<ModuleDto> moduleDtos) {
        Node actualModuleNode1 = courseNode.getChildNodes().get(0);
        assertEquals(moduleDtos.get(0), actualModuleNode1.getNodeData());
        Node actualModuleNode2 = courseNode.getChildNodes().get(1);
        assertEquals(moduleDtos.get(1), actualModuleNode2.getNodeData());
    }
}