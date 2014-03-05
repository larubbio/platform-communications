package org.motechproject.mtraining.service.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class NodeHandlerOrchestratorTest {

    private NodeHandlerOrchestrator nodeHandlerOrchestrator;

    @Mock
    private NodeHandlerFactory nodeHandlerFactory;
    private ContentIdentifierDto messageIdentifier;

    @Before
    public void setUp() throws Exception {
        nodeHandlerOrchestrator = new NodeHandlerOrchestrator(nodeHandlerFactory);
        messageIdentifier = new ContentIdentifierDto(UUID.randomUUID(), 1);
    }

    @Test
    public void shouldValidateGivenNodeAndThenItsChildNodesLevelByLevelInOrder() {
        TestNodeHandler testNodeHandler = new TestNodeHandler();
        MessageDto messageDto1 = new MessageDto();
        MessageDto messageDto2 = new MessageDto();
        MessageDto messageDto3 = new MessageDto();
        MessageDto messageDto4 = new MessageDto();
        ChapterDto chapterDto1 = new ChapterDto();
        ChapterDto chapterDto2 = new ChapterDto();
        ModuleDto moduleDto = new ModuleDto();
        Node chapterNode1 = new Node(NodeType.CHAPTER, chapterDto1,
                asList(new Node(NodeType.MESSAGE, messageDto1), new Node(NodeType.MESSAGE, messageDto2), new Node(NodeType.MESSAGE, messageDto3)));
        Node chapterNode2 = new Node(NodeType.CHAPTER, chapterDto2, asList(new Node(NodeType.MESSAGE, messageDto4)));
        Node moduleNode = new Node(NodeType.MODULE, moduleDto, asList(chapterNode1, chapterNode2));
        when(nodeHandlerFactory.getHandler(any(NodeType.class))).thenReturn(testNodeHandler);

        nodeHandlerOrchestrator.process(moduleNode);

        List<Object> expectedValidatedNodesInOrder = asList(moduleDto, chapterDto1, chapterDto2, messageDto1, messageDto2, messageDto3, messageDto4);
        assertEquals(expectedValidatedNodesInOrder, testNodeHandler.getValidatedNodes());
    }

    @Test
    public void shouldAddChildNodesFirstBeforeAddingGivenNodeInOrder() {
        TestNodeHandler testNodeHandler = new TestNodeHandler();
        ModuleDto moduleDto = new ModuleDto();
        Node messageNode1 = new Node(NodeType.MESSAGE, new MessageDto());
        Node messageNode2 = new Node(NodeType.MESSAGE, new MessageDto());
        Node messageNode3 = new Node(NodeType.MESSAGE, new MessageDto());
        Node messageNode4 = new Node(NodeType.MESSAGE, new MessageDto());
        Node chapterNode1 = new Node(NodeType.CHAPTER, new ChapterDto(), asList(messageNode1, messageNode2, messageNode3));
        Node chapterNode2 = new Node(NodeType.CHAPTER, new ChapterDto(), asList(messageNode4));
        Node moduleNode = new Node(NodeType.MODULE, moduleDto, asList(chapterNode1, chapterNode2));
        when(nodeHandlerFactory.getHandler(any(NodeType.class))).thenReturn(testNodeHandler);

        nodeHandlerOrchestrator.process(moduleNode);

        List<Node> expectedSavedNodesInOrder = asList(messageNode1, messageNode2, messageNode3, chapterNode1, messageNode4, chapterNode2, moduleNode);
        assertEquals(expectedSavedNodesInOrder, testNodeHandler.getSavedNodes());
    }

    @Test
    public void shouldUpdateThePersistentEntityAfterSavingTheContent() {
        TestNodeHandler testNodeHandler = mock(TestNodeHandler.class);
        Node messageNode = new Node(NodeType.MESSAGE, new MessageDto("name", "fileName", "desc", messageIdentifier));
        when(nodeHandlerFactory.getHandler(any(NodeType.class))).thenReturn(testNodeHandler);
        Message savedMessageEntity = new Message("name", "fileName", "desc");
        when(testNodeHandler.saveAndRaiseEvent(messageNode)).thenReturn(savedMessageEntity);

        nodeHandlerOrchestrator.process(messageNode);

        assertEquals(savedMessageEntity, messageNode.getPersistentEntity());
    }

    class TestNodeHandler extends NodeHandler {
        List<Object> validatedNodes = new ArrayList<>();
        List<Node> savedNodes = new ArrayList<>();

        @Override
        protected void validateNodeData(Object nodeData) {
            validatedNodes.add(nodeData);
        }

        @Override
        protected Content saveAndRaiseEvent(Node node) {
            savedNodes.add(node);
            return null;
        }

        public List<Object> getValidatedNodes() {
            return validatedNodes;
        }

        public List<Node> getSavedNodes() {
            return savedNodes;
        }
    }

}
