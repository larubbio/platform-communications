package org.motechproject.mtraining.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllCourses;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.repository.AllModules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class CourseServiceImplTest {

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private AllCourses allCourses;
    @Mock
    private AllModules allModules;
    @Mock
    private AllChapters allChapters;
    @Mock
    private AllMessages allMessages;

    @InjectMocks
    private CourseServiceImpl courseServiceImpl = new CourseServiceImpl();

    @Test
    public void shouldConstructMessageNodeAndInvokeHandler() {
        MessageDto messageDto = new MessageDto();

        courseServiceImpl.addOrUpdateMessage(messageDto);

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
        ChapterDto chapterDto = new ChapterDto(true, "name", "desc", asList(messageDto1, messageDto2));

        courseServiceImpl.addOrUpdateChapter(chapterDto);

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
        ChapterDto chapterDto1 = new ChapterDto(true, "name", "desc", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto(true, "name", "desc", asList(messageDto1, messageDto2));
        ModuleDto moduleDto = new ModuleDto(true, "name", "desc", asList(chapterDto1, chapterDto2));

        courseServiceImpl.addOrUpdateModule(moduleDto);

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
        ChapterDto chapterDto1 = new ChapterDto(true, "name", "desc", asList(messageDto1, messageDto2));
        ChapterDto chapterDto2 = new ChapterDto(true, "name", "desc", asList(messageDto1, messageDto2));
        ModuleDto moduleDto1 = new ModuleDto(true, "name", "desc", asList(chapterDto1, chapterDto2));
        ModuleDto moduleDto2 = new ModuleDto(true, "name", "desc", asList(chapterDto1, chapterDto2));
        CourseDto courseDto = new CourseDto(true, "name", "desc", asList(moduleDto1, moduleDto2));

        courseServiceImpl.addOrUpdateCourse(courseDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualCourseNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.COURSE, actualCourseNode.getNodeType());
        assertEquals(courseDto, actualCourseNode.getNodeData());
        assertModuleNodesForCourse(actualCourseNode, asList(moduleDto1, moduleDto2), asList(chapterDto1, chapterDto2), asList(messageDto1, messageDto2));
        assertNull(actualCourseNode.getPersistentEntity());
    }

    @Test
    public void shouldGetAllCourses() {
        Module module = new Module(true, "moduleName", null, Collections.<Chapter>emptyList());
        ContentIdentifier moduleIdentifier = new ContentIdentifier(module.getContentId(), module.getVersion());
        List<Module> modules = new ArrayList<>();
        modules.add(module);
        Course course1 = new Course(true, "course1", null, modules);
        Course course2 = new Course(true, "course2", null, Collections.<Module>emptyList());
        when(allCourses.getAll()).thenReturn(asList(course1, course2));
        when(allModules.findBy(moduleIdentifier.getContentId(), moduleIdentifier.getVersion())).thenReturn(module);

        List<CourseDto> allCourseDtos = courseServiceImpl.getAllCourses();

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
    public void shouldGetAllModules() {
        Chapter chapter = new Chapter(true, "chapterName", null, Collections.<Message>emptyList());
        ContentIdentifier chapterIdentifier = new ContentIdentifier(chapter.getContentId(), chapter.getVersion());
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(chapter);
        Module module1 = new Module(true, "module1", null, chapters);
        Module module2 = new Module(true, "module2", null, Collections.<Chapter>emptyList());
        when(allModules.getAll()).thenReturn(asList(module1, module2));
        when(allChapters.findBy(chapterIdentifier.getContentId(), chapterIdentifier.getVersion())).thenReturn(chapter);

        List<ModuleDto> allModuleDtos = courseServiceImpl.getAllModules();

        assertEquals(2, allModuleDtos.size());
        ModuleDto moduleDto1 = allModuleDtos.get(0);
        assertModuleDetails(module1, moduleDto1);
        assertEquals(chapter.getContentId(), moduleDto1.getChapters().get(0).getContentId());
        assertEquals(chapter.getVersion(), moduleDto1.getChapters().get(0).getVersion());
        ModuleDto moduleDto2 = allModuleDtos.get(1);
        assertModuleDetails(module2, moduleDto2);
        assertTrue(moduleDto2.getChapters().isEmpty());
    }

    @Test
    public void shouldGetAllChapters() {
        Message message = new Message(true, "messageName", "fileName", null);
        ContentIdentifier messageIdentifier = new ContentIdentifier(message.getContentId(), message.getVersion());
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Chapter chapter1 = new Chapter(true, "chapter1", null, messages);
        Chapter chapter2 = new Chapter(true, "chapter2", null, Collections.<Message>emptyList());
        when(allChapters.getAll()).thenReturn(asList(chapter1, chapter2));
        when(allMessages.findBy(messageIdentifier.getContentId(), messageIdentifier.getVersion())).thenReturn(message);

        List<ChapterDto> allChapterDtos = courseServiceImpl.getAllChapters();

        assertEquals(2, allChapterDtos.size());
        ChapterDto chapterDto1 = allChapterDtos.get(0);
        assertChapterDetails(chapter1, chapterDto1);
        assertEquals(message.getContentId(), chapterDto1.getMessages().get(0).getContentId());
        assertEquals(message.getVersion(), chapterDto1.getMessages().get(0).getVersion());
        ChapterDto chapterDto2 = allChapterDtos.get(1);
        assertChapterDetails(chapter2, chapterDto2);
        assertTrue(chapterDto2.getMessages().isEmpty());
    }

    @Test
    public void shouldGetAllMessages() {
        Message message1 = new Message(true, "messageName1", "fileName1", null);
        Message message2 = new Message(true, "messageName2", "fileName2", null);
        when(allMessages.getAll()).thenReturn(asList(message1, message2));

        List<MessageDto> allMessageDtos = courseServiceImpl.getAllMessages();

        assertEquals(2, allMessageDtos.size());
        assertMessageDetails(message1, allMessageDtos.get(0));
        assertMessageDetails(message2, allMessageDtos.get(1));
    }

    @Test
    public void shouldReturnCourseDtoIfCourseFound() {
        Course course = new Course(true, "course1", "some description", Collections.EMPTY_LIST);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allCourses.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(course);
        CourseDto courseFromDb = courseServiceImpl.getCourse(contentIdentifierDto);

        assertEquals(course.getContentId(), courseFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfCourseByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allCourses.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        CourseDto courseFromDb = courseServiceImpl.getCourse(contentIdentifierDto);

        assertNull(courseFromDb);
    }

    @Test
    public void shouldReturnModuleDtoIfModuleFound() {
        Module module = new Module(true, "module1", "some description", Collections.EMPTY_LIST);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allModules.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(module);
        ModuleDto moduleFromDb = courseServiceImpl.getModule(contentIdentifierDto);

        assertEquals(module.getContentId(), moduleFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfModuleByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allModules.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        ModuleDto moduleFromDb = courseServiceImpl.getModule(contentIdentifierDto);

        assertNull(moduleFromDb);
    }

    @Test
    public void shouldReturnChapterDtoIfChapterFound() {
        Chapter chapter = new Chapter(true, "chapter1", "some description", Collections.EMPTY_LIST);
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allChapters.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(chapter);
        ChapterDto chapterFromDb = courseServiceImpl.getChapter(contentIdentifierDto);

        assertEquals(chapter.getContentId(), chapterFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfChapterByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allChapters.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        ChapterDto chapterFromDb = courseServiceImpl.getChapter(contentIdentifierDto);

        assertNull(chapterFromDb);
    }

    @Test
    public void shouldReturnMessageDtoIfMessageFound() {
        Message message = new Message(true, "message1", "filename", "some description");
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allMessages.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(message);
        MessageDto messageFromDb = courseServiceImpl.getMessage(contentIdentifierDto);

        assertEquals(message.getContentId(), messageFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfMessageByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allMessages.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        MessageDto messageFromDb = courseServiceImpl.getMessage(contentIdentifierDto);

        assertNull(messageFromDb);
    }

    private void assertCourseDetails(Course course, CourseDto courseDto) {
        assertEquals(course.getContentId(), courseDto.getContentId());
        assertEquals(course.getVersion(), courseDto.getVersion());
        assertEquals(course.getName(), courseDto.getName());
        assertEquals(course.getDescription(), courseDto.getDescription());
    }

    private void assertModuleDetails(Module module, ModuleDto moduleDto) {
        assertEquals(module.getContentId(), moduleDto.getContentId());
        assertEquals(module.getVersion(), moduleDto.getVersion());
        assertEquals(module.getName(), moduleDto.getName());
        assertEquals(module.getDescription(), moduleDto.getDescription());
    }

    private void assertChapterDetails(Chapter chapter, ChapterDto chapterDto) {
        assertEquals(chapter.getContentId(), chapterDto.getContentId());
        assertEquals(chapter.getVersion(), chapterDto.getVersion());
        assertEquals(chapter.getName(), chapterDto.getName());
        assertEquals(chapter.getDescription(), chapterDto.getDescription());
    }

    private void assertMessageDetails(Message message, MessageDto messageDto) {
        assertEquals(message.getContentId(), messageDto.getContentId());
        assertEquals(message.getVersion(), messageDto.getVersion());
        assertEquals(message.getName(), messageDto.getName());
        assertEquals(message.getExternalId(), messageDto.getExternalId());
        assertEquals(message.getDescription(), messageDto.getDescription());
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