package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.builder.ModuleContentBuilder;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.repository.AllModules;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mtraining.domain.NodeType.CHAPTER;

@RunWith(MockitoJUnitRunner.class)
public class ModuleServiceImplTest {

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private AllModules allModules;
    @Mock
    private ChapterServiceImpl chapterService;

    private ModuleServiceImpl moduleServiceImpl;

    @Before
    public void setUp() {
        moduleServiceImpl = new ModuleServiceImpl(nodeHandlerOrchestrator, allModules, chapterService);
    }

    @Test
    public void shouldConstructModuleNodeWithAllDescendantNodesAndInvokeHandler() {
        List<MessageDto> messageDTOs = getMessageDTOs();
        ChapterDto chapterDto1 = new ChapterContentBuilder().withName("ch01").withDescription("desc 01").withMessageDTOs(messageDTOs).buildChapterDTO();
        ChapterDto chapterDto2 = new ChapterContentBuilder().withName("ch02").withDescription("desc 02").withMessageDTOs(messageDTOs).buildChapterDTO();
        ModuleDto moduleDto = new ModuleContentBuilder().withName("module 01").withChapterDTOs(asList(chapterDto1, chapterDto2)).buildModuleDTO();
        when(chapterService.constructChapterNodes(moduleDto.getChapters())).thenReturn(newArrayList(new Node(CHAPTER, chapterDto1), new Node(CHAPTER, chapterDto2)));

        moduleServiceImpl.addOrUpdateModule(moduleDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualModuleNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.MODULE, actualModuleNode.getNodeType());
        assertEquals(moduleDto, actualModuleNode.getNodeData());
        assertChapterNodesForModule(actualModuleNode, asList(chapterDto1, chapterDto2));
        assertNull(actualModuleNode.getPersistentEntity());
    }

    @Test
    public void shouldGetAllModules() {
        Chapter chapter = new ChapterContentBuilder().withName("chapterName").buildChapter();
        List<Chapter> chapters = new ArrayList<>();
        chapters.add(chapter);
        Module module1 = new ModuleContentBuilder().withName("module1").withChapters(chapters).buildModule();
        Module module2 = new ModuleContentBuilder().withName("module2").buildModule();
        when(allModules.getAll()).thenReturn(asList(module1, module2));
        when(chapterService.mapToChapterDto(chapter)).thenReturn(new ChapterContentBuilder().withContentId(chapter.getContentId()).
                withVersion(chapter.getVersion()).buildChapterDTO());

        List<ModuleDto> allModuleDtos = moduleServiceImpl.getAllModules();

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
    public void shouldReturnModuleDtoIfModuleFound() {
        Module module = new Module(true, "module1", "some description", "externalId", "Author", Collections.<Chapter>emptyList());
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allModules.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(module);
        ModuleDto moduleFromDb = moduleServiceImpl.getModule(contentIdentifierDto);

        assertEquals(module.getContentId(), moduleFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfModuleByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allModules.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        ModuleDto moduleFromDb = moduleServiceImpl.getModule(contentIdentifierDto);

        assertNull(moduleFromDb);
    }

    private void assertModuleDetails(Module module, ModuleDto moduleDto) {
        assertEquals(module.getContentId(), moduleDto.getContentId());
        assertEquals(module.getVersion(), moduleDto.getVersion());
        assertEquals(module.getName(), moduleDto.getName());
        assertEquals(module.getDescription(), moduleDto.getDescription());
    }

    private void assertChapterNodesForModule(Node moduleNode, List<ChapterDto> chapterDtos) {
        Node actualChapterNode1 = moduleNode.getChildNodes().get(0);
        assertEquals(chapterDtos.get(0), actualChapterNode1.getNodeData());
        Node actualChapterNode2 = moduleNode.getChildNodes().get(1);
        assertEquals(chapterDtos.get(1), actualChapterNode2.getNodeData());
    }

    private List<MessageDto> getMessageDTOs() {
        MessageDto messageDto1 = new MessageContentBuilder().withName("ms001").withAudioFile("audio1").buildMessageDTO();
        MessageDto messageDto2 = new MessageContentBuilder().withName("ms002").withAudioFile("audio2").buildMessageDTO();
        return Arrays.asList(messageDto1, messageDto2);
    }
}