package org.motechproject.mtraining.service.impl;

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
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.domain.NodeType;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllModules;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.motechproject.mtraining.validator.CourseStructureValidator;

import java.util.Collections;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ModuleNodeHandlerTest {
    public static final Integer DEFAULT_VERSION = 1;
    @InjectMocks
    private ModuleNodeHandler moduleNodeHandler = new ModuleNodeHandler();

    @Mock
    private CourseStructureValidator courseStructureValidator;
    @Mock
    private AllModules allModules;
    @Mock
    private EventRelay eventRelay;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldValidateGivenModuleDtoAndThrowExceptionIfInvalid() {
        ModuleDto moduleDto = new ModuleDto("name", "description", Collections.EMPTY_LIST);
        CourseStructureValidationResponse validationResponse = new CourseStructureValidationResponse(false);
        validationResponse.addError("some validation error");
        when(courseStructureValidator.validateModule(moduleDto)).thenReturn(validationResponse);

        expectedException.expect(CourseStructureValidationException.class);
        expectedException.expectMessage("Invalid module: some validation error");

        moduleNodeHandler.validateNodeData(moduleDto);
    }

    @Test
    public void shouldNotThrowExceptionIfTheGivenModuleDtoIsValid() {
        ModuleDto moduleDto = new ModuleDto("name", "description", asList(new ChapterDto()));
        when(courseStructureValidator.validateModule(moduleDto)).thenReturn(new CourseStructureValidationResponse(true));

        moduleNodeHandler.validateNodeData(moduleDto);
    }

    @Test
    public void shouldSaveTheGivenModuleDtoAsModuleEntityWithChaptersAndRaiseEvent() {
        Node chapterNode1 = new Node(NodeType.MESSAGE, new ChapterDto());
        Chapter expectedChapterForTheModule = new Chapter("", "", Collections.EMPTY_LIST);
        chapterNode1.setPersistentEntity(expectedChapterForTheModule);
        Node chapterNode2 = new Node(NodeType.MESSAGE, new ChapterDto());
        ModuleDto moduleDto = new ModuleDto("name", "description", asList(new ChapterDto()));
        Node moduleNode = new Node(NodeType.CHAPTER, moduleDto, asList(chapterNode1, chapterNode2));

        moduleNodeHandler.saveAndRaiseEvent(moduleNode);

        InOrder inOrder = inOrder(allModules, eventRelay);
        ArgumentCaptor<Module> moduleArgumentCaptor = ArgumentCaptor.forClass(Module.class);
        inOrder.verify(allModules).add(moduleArgumentCaptor.capture());
        Module savedModule = moduleArgumentCaptor.getValue();
        assertModuleDetails(moduleDto, expectedChapterForTheModule, savedModule);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        inOrder.verify(eventRelay).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();
        assertEventDetails(savedModule, raisedEvent);
    }

    private void assertModuleDetails(ModuleDto moduleDto, Chapter expectedChapter, Module savedModule) {
        assertEquals(savedModule.getName(), moduleDto.getName());
        assertEquals(savedModule.getDescription(), moduleDto.getDescription());
        assertEquals(1, savedModule.getChapters().size());
        assertEquals(expectedChapter.getContentId(), savedModule.getChapters().get(0).getContentId());
        assertEquals(expectedChapter.getVersion(), savedModule.getChapters().get(0).getVersion());
        assertNotNull(savedModule.getContentId());
        assertEquals(DEFAULT_VERSION, savedModule.getVersion());
    }

    private void assertEventDetails(Module savedModule, MotechEvent raisedEvent) {
        assertEquals(MTrainingEventConstants.MODULE_CREATION_EVENT, raisedEvent.getSubject());
        assertEquals(1, raisedEvent.getParameters().size());
        assertEquals(savedModule.getContentId(), raisedEvent.getParameters().get(MTrainingEventConstants.NODE_ID));
    }
}
