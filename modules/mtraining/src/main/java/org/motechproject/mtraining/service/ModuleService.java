package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.ModuleDto;

/**
 * Service Interface that exposes APIs to modules
 */

public interface ModuleService {

    ContentIdentifierDto addModule(ModuleDto moduleDto);

    ModuleDto getModule(ContentIdentifierDto moduleIdentifier);
}
