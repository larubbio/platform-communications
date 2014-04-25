package org.motechproject.mtraining.builder;

import org.motechproject.mtraining.domain.Course;
import org.motechproject.mtraining.domain.Module;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.CourseDto;
import org.motechproject.mtraining.dto.ModuleDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class CourseContentBuilder {

    private String name = "Default Chapter Name";
    private String description = "Default Chapter Description";
    private String externalId = "Default external Id";
    private boolean isActive = true;
    private String createBy = "Chapter Author";
    private UUID contentId = null;
    private Integer version = -1;

    private List<Module> modules = new ArrayList<>();

    private List<ModuleDto> moduleDtos = new ArrayList<>();


    public CourseContentBuilder withContentId(UUID contentId) {
        this.contentId = contentId;
        return this;
    }

    public CourseContentBuilder withVersion(Integer version) {
        this.version = version;
        return this;
    }

    public CourseContentBuilder withName(String name) {
        this.name = name;
        return this;
    }

    public CourseContentBuilder withDescription(String description) {
        this.description = description;
        return this;
    }

    public CourseContentBuilder asInactive() {
        this.isActive = false;
        return this;
    }

    public CourseContentBuilder createBy(String createdBy) {
        this.createBy = createdBy;
        return this;
    }

    public CourseContentBuilder withModules(List<Module> modules) {
        this.modules.clear();
        this.modules.addAll(modules);
        return this;
    }

    public CourseContentBuilder withModuleDtos(List<ModuleDto> moduleDTOs) {
        this.moduleDtos.clear();
        this.moduleDtos.addAll(moduleDTOs);
        return this;
    }

    public Course buildCourse() {
        checkContentIdAndVersion();
        List<Module> moduleList = CollectionUtils.copy(modules);
        if (contentId == null || version < 0) {
            return new Course(isActive, name, description, externalId, createBy, moduleList);
        }
        return new Course(contentId, version, isActive, name, description, externalId, createBy, moduleList);
    }

    public CourseDto buildCourseDTO() {
        checkContentIdAndVersion();
        List<ModuleDto> moduleDtoList = CollectionUtils.copy(moduleDtos);
        if (contentId == null || version < 0) {
            return new CourseDto(isActive, name, description, externalId, createBy, moduleDtoList);
        }
        return new CourseDto(contentId, version, isActive, name, description, externalId, createBy, moduleDtoList);
    }

    public ModuleDto buildModuleDTO() {
        checkContentIdAndVersion();
        if (contentId == null || version < 0) {
            return new ModuleDto(isActive, name, description, externalId, createBy, Collections.<ChapterDto>emptyList());
        }
        return new ModuleDto(contentId, version, isActive, name, description, externalId, createBy, Collections.<ChapterDto>emptyList());
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
