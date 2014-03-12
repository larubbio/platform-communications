package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Content;
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
import org.motechproject.mtraining.service.ChapterService;
import org.motechproject.mtraining.service.CourseService;
import org.motechproject.mtraining.service.MessageService;
import org.motechproject.mtraining.service.ModuleService;
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
public class CourseServiceImpl implements CourseService, ModuleService, ChapterService, MessageService {

    @Autowired
    private NodeHandlerOrchestrator nodeHandlerOrchestrator;

    @Autowired
    private AllCourses allCourses;

    @Autowired
    private AllModules allModules;

    @Autowired
    private AllChapters allChapters;

    @Autowired
    private AllMessages allMessages;

    @Override
    public ContentIdentifierDto addOrUpdateCourse(CourseDto courseDto) {
        Node courseNode = constructCourseNode(courseDto);
        nodeHandlerOrchestrator.process(courseNode);
        return getContentIdentifier(courseNode);
    }

    @Override
    public ContentIdentifierDto addOrUpdateModule(ModuleDto moduleDto) {
        Node moduleNode = constructModuleNode(moduleDto);
        nodeHandlerOrchestrator.process(moduleNode);
        return getContentIdentifier(moduleNode);
    }

    @Override
    public ContentIdentifierDto addOrUpdateChapter(ChapterDto chapterDto) {
        Node chapterNode = constructChapterNode(chapterDto);
        nodeHandlerOrchestrator.process(chapterNode);
        return getContentIdentifier(chapterNode);
    }

    @Override
    public ContentIdentifierDto addOrUpdateMessage(MessageDto messageDto) {
        Node messageNode = constructMessageNode(messageDto);
        nodeHandlerOrchestrator.process(messageNode);
        return getContentIdentifier(messageNode);
    }

    @Override
    public CourseDto getCourse(ContentIdentifierDto courseIdentifier) {
        Course course = allCourses.findBy(courseIdentifier.getContentId(), courseIdentifier.getVersion());
        return course != null ? mapToCourseDto(course) : null;
    }

    @Override
    public List<CourseDto> getAllCourses() {
        List<Course> courses = allCourses.getAll();
        List<CourseDto> courseDtoList = new ArrayList<>();
        for (Course course : courses) {
            courseDtoList.add(mapToCourseDto(course));
        }
        return courseDtoList;
    }

    @Override
    public ModuleDto getModule(ContentIdentifierDto moduleIdentifier) {
        Module module = allModules.findBy(moduleIdentifier.getContentId(), moduleIdentifier.getVersion());
        return module != null ? mapToModuleDto(module) : null;
    }

    @Override
    public List<ModuleDto> getAllModules() {
        List<Module> modules = allModules.getAll();
        List<ModuleDto> moduleDtoList = new ArrayList<>();
        for (Module module : modules) {
            moduleDtoList.add(mapToModuleDto(module));
        }
        return moduleDtoList;
    }

    @Override
    public ChapterDto getChapter(ContentIdentifierDto chapterIdentifier) {
        Chapter chapter = allChapters.findBy(chapterIdentifier.getContentId(), chapterIdentifier.getVersion());
        return chapter != null ? mapToChapterDto(chapter) : null;
    }

    @Override
    public List<ChapterDto> getAllChapters() {
        ArrayList<ChapterDto> chapterDtoList = new ArrayList<>();
        List<Chapter> chapters = allChapters.getAll();
        for (Chapter chapter : chapters) {
            chapterDtoList.add(mapToChapterDto(chapter));
        }
        return chapterDtoList;
    }

    @Override
    public MessageDto getMessage(ContentIdentifierDto messageIdentifier) {
        Message message = allMessages.findBy(messageIdentifier.getContentId(), messageIdentifier.getVersion());
        return message != null ? mapToMessageDto(message) : null;
    }

    @Override
    public List<MessageDto> getAllMessages() {
        List<Message> messages = allMessages.getAll();
        List<MessageDto> messageDtoList = new ArrayList<>();
        for (Message message : messages) {
            messageDtoList.add(mapToMessageDto(message));
        }
        return messageDtoList;
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

    private CourseDto mapToCourseDto(Course course) {
        ArrayList<ModuleDto> modules = new ArrayList<>();
        for (ContentIdentifier moduleIdentifier : course.getModules()) {
            ModuleDto moduleDto = getModule(new ContentIdentifierDto(moduleIdentifier.getContentId(), moduleIdentifier.getVersion()));
            modules.add(moduleDto);
        }
        return new CourseDto(course.getContentId(), course.getVersion(), course.isActive(), course.getName(), course.getDescription(), modules);
    }

    private ModuleDto mapToModuleDto(Module module) {
        List<ChapterDto> chapters = new ArrayList<>();
        for (ContentIdentifier chapterIdentifier : module.getChapters()) {
            ChapterDto chapter = getChapter(new ContentIdentifierDto(chapterIdentifier.getContentId(), chapterIdentifier.getVersion()));
            chapters.add(chapter);
        }
        return new ModuleDto(module.getContentId(), module.getVersion(), module.isActive(), module.getName(), module.getDescription(), chapters);
    }

    private ChapterDto mapToChapterDto(Chapter chapter) {
        ArrayList<MessageDto> messages = new ArrayList<>();
        for (ContentIdentifier messageIdentifier : chapter.getMessages()) {
            MessageDto message = getMessage(new ContentIdentifierDto(messageIdentifier.getContentId(), messageIdentifier.getVersion()));
            messages.add(message);
        }
        return new ChapterDto(chapter.getContentId(), chapter.getVersion(), chapter.isActive(), chapter.getName(), chapter.getDescription(), messages);
    }

    private MessageDto mapToMessageDto(Message message) {
        return new MessageDto(message.getContentId(), message.getVersion(), message.isActive(), message.getName(), message.getExternalId(), message.getDescription());
    }
}
