package org.motechproject.mtraining.service.impl;

import org.apache.log4j.Logger;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.Chapter;
import org.motechproject.mtraining.domain.ChildContentIdentifier;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.dto.ChapterDto;
import org.motechproject.mtraining.exception.CourseStructureValidationException;
import org.motechproject.mtraining.repository.AllChapters;
import org.motechproject.mtraining.validator.CourseStructureValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Implementation of abstract class {@link NodeHandler}.
 * Validates, saves and raises an event for a node of type {@link org.motechproject.mtraining.domain.NodeType#CHAPTER}
 */

@Component
public class ChapterNodeHandler extends NodeHandler {

    private static Logger logger = Logger.getLogger(ChapterNodeHandler.class);

    @Autowired
    private AllChapters allChapters;

    @Override
    protected void validateNodeData(Object nodeData) {
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

        Chapter chapter = new Chapter(chapterDto.getName(), chapterDto.getDescription(), getMessages(node));
        allChapters.add(chapter);

        if (logger.isDebugEnabled()) {
            logger.debug(String.format("Raising event for saved chapter: %s", chapter.getContentId()));
        }

        sendEvent(MTrainingEventConstants.CHAPTER_CREATION_EVENT, chapter.getContentId());
        return chapter;
    }

    private List<ChildContentIdentifier> getMessages(Node node) {
        return getChildContentIdentifiers(node);
    }
}
