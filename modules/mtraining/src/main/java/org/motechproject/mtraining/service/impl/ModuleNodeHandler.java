package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.ContentIdentifier;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllModules;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

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
    protected void validateNodeData(Object nodeData) {
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

        Module module = new Module(moduleDto.getName(), moduleDto.getDescription(), getChapters(node));
        ContentIdentifierDto moduleIdentifier = moduleDto.getModuleIdentifier();
        if (moduleIdentifier != null) {
            module.setContentId(moduleIdentifier.getContentId());
            module.setVersion(moduleIdentifier.getVersion());
        }
        allModules.add(module);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved module: %s", module.getContentId()));
        }

        sendEvent(MTrainingEventConstants.MODULE_CREATION_EVENT, module.getContentId(), module.getVersion());
        return module;
    }

    private List<ContentIdentifier> getChapters(Node node) {
        return getChildContentIdentifiers(node);
    }
}
