package org.motechproject.mtraining.service;

import org.motechproject.mtraining.dto.ContentIdentifierDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.List;

/**
 * Service Interface that exposes APIs to modules
 */

public interface ModuleService {

    ContentIdentifierDto addOrUpdateModule(ModuleDto moduleDto);

    ModuleDto getModule(ContentIdentifierDto moduleIdentifier);

    List<ModuleDto> getAllModules();
}
