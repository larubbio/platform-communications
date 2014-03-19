package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.builder.ChapterContentBuilder;
import org.motechproject.mtraining.builder.MessageContentBuilder;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.repository.AllMessages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.motechproject.mtraining.domain.NodeType.MESSAGE;
import static org.motechproject.mtraining.domain.NodeType.MODULE;

@RunWith(MockitoJUnitRunner.class)
public class ChapterServiceImplTest {

    @Mock
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;
    @Mock
    private AllChapters allChapters;
    @Mock
    private AllMessages allMessages;
    @Mock
    private QuizServiceImpl quizService;
    @Mock
    private MessageServiceImpl messageService;

    private ChapterServiceImpl chapterServiceImpl;

    @Before
    public void setUp() {
        chapterServiceImpl = new ChapterServiceImpl(nodeHandlerOrchestrator, allChapters, messageService, quizService);
    }

    @Test
    public void shouldConstructChapterNodeWithMessageChildNodesAndInvokeHandler() {
        MessageDto messageDto1 = new MessageContentBuilder().withName("ms001").withAudioFile("audio1").buildMessageDTO();
        MessageDto messageDto2 = new MessageContentBuilder().withName("ms002").withAudioFile("audio2").buildMessageDTO();
        List<MessageDto> messageDTOs = newArrayList(messageDto1, messageDto2);
        ChapterDto chapterDto = new ChapterContentBuilder().withMessageDTOs(messageDTOs).buildChapterDTO();
        when(messageService.constructMessageNodes(chapterDto.getMessages())).thenReturn(newArrayList(new Node(MESSAGE, messageDto1), new Node(MESSAGE, messageDto2)));

        chapterServiceImpl.addOrUpdateChapter(chapterDto);

        ArgumentCaptor<Node> nodeArgumentCaptor = ArgumentCaptor.forClass(Node.class);
        verify(nodeHandlerOrchestrator).process(nodeArgumentCaptor.capture());
        Node actualChapterNode = nodeArgumentCaptor.getValue();
        assertEquals(NodeType.CHAPTER, actualChapterNode.getNodeType());
        assertEquals(chapterDto, actualChapterNode.getNodeData());
        assertNull(actualChapterNode.getPersistentEntity());
        assertMessageNodesForChapter(actualChapterNode, messageDTOs);
    }

    @Test
    public void shouldGetAllChapters() {
        Message message = new MessageContentBuilder().buildMessage();
        ArrayList<Message> messages = new ArrayList<>();
        messages.add(message);
        Chapter chapter1 = new ChapterContentBuilder().withName("ch01").withMessages(messages).buildChapter();
        Chapter chapter2 = new ChapterContentBuilder().withName("ch02").buildChapter();
        when(allChapters.getAll()).thenReturn(asList(chapter1, chapter2));
        when(messageService.mapToMessageDto(message)).thenReturn(new MessageContentBuilder().withContentId(message.getContentId()).
                withVersion(message.getVersion()).buildMessageDTO());

        List<ChapterDto> allChapterDtos = chapterServiceImpl.getAllChapters();

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
    public void shouldReturnChapterDtoIfChapterFound() {
        Chapter chapter = new ChapterContentBuilder().withName("Ch001").buildChapter();
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allChapters.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(chapter);
        ChapterDto chapterFromDb = chapterServiceImpl.getChapter(contentIdentifierDto);

        assertEquals(chapter.getContentId(), chapterFromDb.getContentId());
    }

    @Test
    public void shouldReturnNullIfChapterByContentIdNotFound() {
        ContentIdentifierDto contentIdentifierDto = new ContentIdentifierDto(UUID.randomUUID(), 1);

        when(allChapters.findBy(contentIdentifierDto.getContentId(), contentIdentifierDto.getVersion())).thenReturn(null);
        ChapterDto chapterFromDb = chapterServiceImpl.getChapter(contentIdentifierDto);

        assertNull(chapterFromDb);
    }

    private void assertChapterDetails(Chapter chapter, ChapterDto chapterDto) {
        assertEquals(chapter.getContentId(), chapterDto.getContentId());
        assertEquals(chapter.getVersion(), chapterDto.getVersion());
        assertEquals(chapter.getName(), chapterDto.getName());
        assertEquals(chapter.getDescription(), chapterDto.getDescription());
    }

    private void assertMessageNodesForChapter(Node chapterNode, List<MessageDto> messageDtos) {
        assertEquals(messageDtos.get(0), chapterNode.getChildNodes().get(0).getNodeData());
        assertEquals(messageDtos.get(1), chapterNode.getChildNodes().get(1).getNodeData());
    }
}