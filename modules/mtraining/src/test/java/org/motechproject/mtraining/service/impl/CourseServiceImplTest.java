package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;

import java.util.List;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceImplTest {

    private CourseService courseService;

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;

    @Before
    public void setUp() throws Exception {
        courseService = new CourseServiceImpl(nodeHandlerOrchestrator);
    }

    @Test
    public void shouldConstructMessageNodeAndInvokeHandler() {
        MessageDto messageDto = new MessageDto();

        courseService.addMessage(messageDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualMessageNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.MESSAGE, actualMessageNode.getNodeType());
        assertEquals(messageDto, actualMessageNode.getNodeData());
        assertTrue(actualMessageNode.getChildNodes().isEmpty());
        assertNull(actualMessageNode.getPersistentEntity());
    }

    @Test
    public void shouldConstructChapterNodeWithMessageChildNodesAndInvokeHandler() {
        MessageDto messageDto1 = new MessageDto();
        MessageDto messageDto2 = new MessageDto();
        ChapterDto chapterDto = new ChapterDto("name", "desc", asList(messageDto1, messageDto2));

        courseService.addChapter(chapterDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualChapterNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.CHAPTER, actualChapterNode.getNodeType());
        assertEquals(chapterDto, actualChapterNode.getNodeData());
        assertNull(actualChapterNode.getPersistentEntity());
        assertMessageNodesForChapter(actualChapterNode, asList(messageDto1, messageDto2));
    }

    @Test
    public void shouldConstructModuleNodeWithAllDescendantNodesAndInvokeHandler() {
        MessageDto messageDto1 = new MessageDto();
        MessageDto messageDto2 = new MessageDto();
        ChapterDto chapterDto1 = new ChapterDto("name", "desc", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto("name", "desc", asList(messageDto1, messageDto2));
        ModuleDto moduleDto = new ModuleDto("name", "desc", asList(chapterDto1, chapterDto2));

        courseService.addModule(moduleDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualModuleNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.MODULE, actualModuleNode.getNodeType());
        assertEquals(moduleDto, actualModuleNode.getNodeData());
        assertChapterNodesForModule(actualModuleNode, asList(chapterDto1, chapterDto2), asList(messageDto1, messageDto2));
        assertNull(actualModuleNode.getPersistentEntity());
    }

    @Test
    public void shouldConstructCourseNodeWithAllDescendantNodesAndInvokeHandler() {
        MessageDto messageDto1 = new MessageDto();
        MessageDto messageDto2 = new MessageDto();
        ChapterDto chapterDto1 = new ChapterDto("name", "desc", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto("name", "desc", asList(messageDto1, messageDto2));
        ModuleDto moduleDto1 = new ModuleDto("name", "desc", asList(chapterDto1, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto("name", "desc", asList(chapterDto1, chapterDto2));
        CourseDto courseDto = new CourseDto("name", "desc", asList(moduleDto1, moduleDto2));

        courseService.addCourse(courseDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualCourseNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.COURSE, actualCourseNode.getNodeType());
        assertEquals(courseDto, actualCourseNode.getNodeData());
        assertModuleNodesForCourse(actualCourseNode, asList(moduleDto1, moduleDto2), asList(chapterDto1, chapterDto2), asList(messageDto1, messageDto2));
        assertNull(actualCourseNode.getPersistentEntity());
    }

    private void assertModuleNodesForCourse(Node courseNode, List<ModuleDto> moduleDtos, List<ChapterDto> chapterDtos, List<MessageDto> messageDtos) {
        Node actualModuleNode1 = courseNode.getChildNodes().get(0);
        assertEquals(moduleDtos.get(0), actualModuleNode1.getNodeData());
        assertChapterNodesForModule(actualModuleNode1, chapterDtos, messageDtos);
        Node actualModuleNode2 = courseNode.getChildNodes().get(1);
        assertEquals(moduleDtos.get(1), actualModuleNode2.getNodeData());
        assertChapterNodesForModule(actualModuleNode2, chapterDtos, messageDtos);
    }

    private void assertChapterNodesForModule(Node moduleNode, List<ChapterDto> chapterDtos, List<MessageDto> messageDtos) {
        Node actualChapterNode1 = moduleNode.getChildNodes().get(0);
        assertEquals(chapterDtos.get(0), actualChapterNode1.getNodeData());
        assertMessageNodesForChapter(actualChapterNode1, messageDtos);
        Node actualChapterNode2 = moduleNode.getChildNodes().get(1);
        assertEquals(chapterDtos.get(1), actualChapterNode2.getNodeData());
        assertMessageNodesForChapter(actualChapterNode2, messageDtos);
    }

    private void assertMessageNodesForChapter(Node chapterNode, List<MessageDto> messageDtos) {
        assertEquals(messageDtos.get(0), chapterNode.getChildNodes().get(0).getNodeData());
        assertEquals(messageDtos.get(1), chapterNode.getChildNodes().get(1).getNodeData());
    }

}
