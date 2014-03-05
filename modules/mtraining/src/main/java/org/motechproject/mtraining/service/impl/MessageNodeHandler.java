package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllMessages;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#MESSAGE}
 */

@Component
public class MessageNodeHandler extends NodeHandler {
    private static Logger logger = LoggerFactory.getLogger(MessageNodeHandler.class);

    @Autowired
    private AllMessages allMessages;

    @Override
    protected void validateNodeData(Object nodeData) {
        MessageDto messageDto = (MessageDto) nodeData;
        CourseStructureValidationResponse validationResponse = validator().validateMessage(messageDto);
        if (!validationResponse.isValid()) {
            String message = String.format("Invalid message: %s", validationResponse.getErrorMessage());
            logger.error(message);
            throw new CourseStructureValidationException(message);
        }
    }

    @Override
    protected Message saveAndRaiseEvent(Node node) {
        MessageDto messageDto = (MessageDto) node.getNodeData();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Saving message: %s", messageDto.getName()));
        }

        Message message = new Message(messageDto.getName(), messageDto.getExternalId(), messageDto.getDescription());
        ContentIdentifierDto messageIdentifier = messageDto.getMessageIdentifier();
        if (messageIdentifier != null) {
            message.setContentId(messageIdentifier.getContentId());
            message.setVersion(messageIdentifier.getVersion());
        }
        allMessages.add(message);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved message: %s", message.getContentId()));
        }
        sendEvent(MTrainingEventConstants.MESSAGE_CREATION_EVENT, message.getContentId(), message.getVersion());
        return message;
    }
}
