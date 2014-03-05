package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.MessageDto;

/**
 * Service Interface that exposes APIs to messages
 */

public interface MessageService {

    ContentIdentifierDto addMessage(MessageDto messageDto);

    MessageDto getMessage(ContentIdentifierDto messageIdentifier);
}
