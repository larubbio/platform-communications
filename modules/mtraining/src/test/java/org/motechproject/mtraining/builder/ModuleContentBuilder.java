package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ModuleContentBuilder {

    private String name = "Default Module Name";
    private String description = "Default Module Description";
    private String externalId = "Default external Id";
    private boolean isActive = true;
    private String createBy = "Module Author";
    private UUID contentId = null;
    private Integer version = -1;

    private List<Chapter> chapters = new ArrayList<>();

    private List<ChapterDto> chapterDtos = new ArrayList<>();


    public ModuleContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public ModuleContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public ModuleContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public ModuleContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public ModuleContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public ModuleContentBuilder createBy(String createdBy) {
        this.createBy = createdBy;
        return this;
    }

    public ModuleContentBuilder withChapters(List<Chapter> messages) {
        this.chapters.clear();
        this.chapters.addAll(messages);
        return this;
    }

    public ModuleContentBuilder withChapterDTOs(List<ChapterDto> messageDTOs) {
        this.chapterDtos.clear();
        this.chapterDtos.addAll(messageDTOs);
        return this;
    }

    public Module buildModule() {
        checkContentIdAndVersion();
        List<Chapter> chapterList = CollectionUtils.copy(chapters);
        if (contentId == null || version < 0) {
            return new Module(isActive, name, description, externalId, createBy, chapterList);
        }
        return new Module(contentId, version, isActive, name, description, externalId, createBy, chapterList);
    }

    public ModuleDto buildModuleDTO() {
        checkContentIdAndVersion();
        List<ChapterDto> chapterDtoList = CollectionUtils.copy(chapterDtos);
        if (contentId == null || version < 0) {
            return new ModuleDto(isActive, name, description, externalId, createBy, chapterDtoList);
        }
        return new ModuleDto(contentId, version, isActive, name, description, externalId, createBy, chapterDtoList);
    }

    private void checkContentIdAndVersion() {
        boolean illegalState = false;
        if (version < 0 && contentId != null) {
            illegalState = true;
        } else if (version >= 0 && contentId == null) {
            illegalState = true;
        }
        if (illegalState) {
            throw new IllegalStateException(String.format("Both version and contentId id need to be supplied or both should be left null. As of now contentId %s and version %s", contentId, version));
        }
    }
}
