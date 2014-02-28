package org.motechproject.mtraining.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.ChildContentIdentifier;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ModuleDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllModules;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#MODULE}
 */

@Component
public class ModuleNodeHandler extends NodeHandler {

    private static Logger logger = Logger.getLogger(ModuleNodeHandler.class);

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
        allModules.add(module);
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved module: %s", module.getContentId()));
        }

        sendEvent(MTrainingEventConstants.MODULE_CREATION_EVENT, module.getContentId());
        return module;
    }

    private List<ChildContentIdentifier> getChapters(Node node) {
        return getChildContentIdentifiers(node);
    }
}
