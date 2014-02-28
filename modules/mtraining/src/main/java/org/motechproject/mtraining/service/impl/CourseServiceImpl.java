package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;

/**
 * Implementation class for {@link CourseService}.
 * Given a content DTO to add, it constructs a tree structured generic {@link Node}
 * and uses {@link CourseServiceImpl#nodeHandlerOrchestrator} to process the node
 */

@Service("courseService")
public class CourseServiceImpl implements CourseService {

    private NodeHandlerOrchestrator nodeHandlerOrchestrator;

    @Autowired
    public CourseServiceImpl(NodeHandlerOrchestrator nodeHandlerOrchestrator) {
        this.nodeHandlerOrchestrator = nodeHandlerOrchestrator;
    }

    @Override
    public ContentIdentifierDto addCourse(CourseDto courseDto) {
        Node courseNode = constructCourseNode(courseDto);
        nodeHandlerOrchestrator.process(courseNode);
        return getContentIdentifier(courseNode);
    }

    @Override
    public ContentIdentifierDto addModule(ModuleDto moduleDto) {
        Node moduleNode = constructModuleNode(moduleDto);
        nodeHandlerOrchestrator.process(moduleNode);
        return getContentIdentifier(moduleNode);
    }

    @Override
    public ContentIdentifierDto addChapter(ChapterDto chapterDto) {
        Node chapterNode = constructChapterNode(chapterDto);
        nodeHandlerOrchestrator.process(chapterNode);
        return getContentIdentifier(chapterNode);
    }

    @Override
    public ContentIdentifierDto addMessage(MessageDto messageDto) {
        Node messageNode = constructMessageNode(messageDto);
        nodeHandlerOrchestrator.process(messageNode);
        return getContentIdentifier(messageNode);
    }

    private Node constructCourseNode(CourseDto courseDto) {
        List<Node> moduleNodes = constructModuleNodes(courseDto.getModules());
        return new Node(NodeType.COURSE, courseDto, moduleNodes);
    }

    private Node constructModuleNode(ModuleDto moduleDto) {
        return constructModuleNodes(asList(moduleDto)).get(0);
    }

    private Node constructChapterNode(ChapterDto chapterDto) {
        return constructChapterNodes(asList(chapterDto)).get(0);
    }

    private Node constructMessageNode(MessageDto messageDto) {
        return constructMessageNodes(asList(messageDto)).get(0);
    }

    private List<Node> constructModuleNodes(List<ModuleDto> modules) {
        List<Node> moduleNodes = new ArrayList<>();
        for (ModuleDto module : modules) {
            List<Node> chapterNodes = constructChapterNodes(module.getChapters());
            Node moduleNode = new Node(NodeType.MODULE, module, chapterNodes);
            moduleNodes.add(moduleNode);
        }
        return moduleNodes;
    }

    private List<Node> constructChapterNodes(List<ChapterDto> chapters) {
        List<Node> chapterNodes = new ArrayList<>();
        for (ChapterDto chapter : chapters) {
            List<Node> messageNodes = constructMessageNodes(chapter.getMessages());
            Node chapterNode = new Node(NodeType.CHAPTER, chapter, messageNodes);
            chapterNodes.add(chapterNode);
        }
        return chapterNodes;
    }

    private List<Node> constructMessageNodes(List<MessageDto> messages) {
        List<Node> messageNodes = new ArrayList<>();
        for (MessageDto message : messages) {
            Node messageNode = new Node(NodeType.MESSAGE, message);
            messageNodes.add(messageNode);
        }
        return messageNodes;
    }

    private ContentIdentifierDto getContentIdentifier(Node node) {
        Content savedContent = node.getPersistentEntity();
        return savedContent != null ? new ContentIdentifierDto(savedContent.getContentId(), savedContent.getVersion()) : null;
    }
}
