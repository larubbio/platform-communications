package org.motechproject.mtraining.service.impl;

import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.Message;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.dto.ContentDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#CHAPTER}
 */

@Component
public class ChapterNodeHandler extends NodeHandler {

    private static Logger logger = LoggerFactory.getLogger(ChapterNodeHandler.class);

    @Autowired
    private AllChapters allChapters;

    @Override
    protected void validateNodeData(ContentDto nodeData) {
        ChapterDto chapterDto = (ChapterDto) nodeData;
        CourseStructureValidationResponse validationResponse = validator().validateChapter(chapterDto);
        if (!validationResponse.isValid()) {
            String message = String.format("Invalid chapter: %s", validationResponse.getErrorMessage());
            logger.error(message);
            throw new CourseStructureValidationException(message);
        }
    }

    @Override
    protected Chapter saveAndRaiseEvent(Node node) {
        ChapterDto chapterDto = (ChapterDto) node.getNodeData();
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Saving chapter: %s", chapterDto.getName()));
        }


        Chapter chapter = chapterWithMessages(chapterDto, getMessages(node));

        allChapters.add(chapter);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved chapter: %s", chapter.getContentId()));
        }

        sendEvent(MTrainingEventConstants.CHAPTER_CREATION_EVENT, chapter.getContentId(), chapter.getVersion());
        return chapter;
    }


    private List<Message> getMessages(Node node) {
        return getChildContentNodes(node);
    }

    private Chapter chapterWithMessages(ChapterDto chapterDto, List<Message> messages) {
        UUID contentId = chapterDto.getContentId();
        if (contentId == null) {
            return new Chapter(chapterDto.isActive(), chapterDto.getName(), chapterDto.getDescription(), messages);
        }

        Chapter existingChapter = getLatestVersion(allChapters.findByContentId(contentId));
        Chapter chapterToSave = new Chapter(existingChapter.getContentId(), existingChapter.getVersion(), chapterDto.isActive(), chapterDto.getName(), chapterDto.getDescription(), messages);
        chapterToSave.incrementVersion();
        return chapterToSave;
    }

}
