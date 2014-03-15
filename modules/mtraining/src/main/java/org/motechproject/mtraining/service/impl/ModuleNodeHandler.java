package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllModules;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#MODULE}
 */

@Component
public class ModuleNodeHandler extends NodeHandler {

    private static Logger logger = LoggerFactory.getLogger(ModuleNodeHandler.class);

    @Autowired
    private AllModules allModules;

    @Override
    protected void validateNodeData(ContentDto nodeData) {
        ModuleDto moduleDto = (ModuleDto) nodeData;
        CourseStructureValidationResponse validationResponse = validator().validateModule(moduleDto);
        if (!validationResponse.isValid()) {
            String message = String.format("Invalid module: %s", validationResponse.getErrorMessage());
            logger.error(message);
            throw new CourseStructureValidationException(message);
        }
    }

    @Override
    protected Module saveAndRaiseEvent(Node node) {
        ModuleDto moduleDto = (ModuleDto) node.getNodeData();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Saving module: %s", moduleDto.getName()));
        }

        Module module = getModule(moduleDto, getChapters(node));
        allModules.add(module);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved module: %s", module.getContentId()));
        }

        sendEvent(MTrainingEventConstants.MODULE_CREATION_EVENT, module.getContentId(), module.getVersion());
        return module;
    }

    private List<Chapter> getChapters(Node node) {
        return getChildContentNodes(node);
    }

    private Module getModule(ModuleDto moduleDto, List<Chapter> chapters) {
        UUID contentId = moduleDto.getContentId();
        if (contentId == null) {
            return new Module(moduleDto.isActive(), moduleDto.getName(), moduleDto.getDescription(), chapters);
        }

        Module existingModule = getLatestVersion(allModules.findByContentId(contentId));
        Module moduleToSave = new Module(existingModule.getContentId(), existingModule.getVersion(), moduleDto.isActive(), moduleDto.getName(), moduleDto.getDescription(), chapters);
        moduleToSave.incrementVersion();
        return moduleToSave;
    }
}
