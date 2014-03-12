package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to messages
 */

public interface MessageService {

    ContentIdentifierDto addOrUpdateMessage(MessageDto messageDto);

    MessageDto getMessage(ContentIdentifierDto messageIdentifier);

    List<MessageDto> getAllMessages();
}
