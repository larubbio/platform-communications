package org.motechproject.mtraining.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.mtraining.constants.MTrainingEventConstants;
import org.motechproject.mtraining.domain.ChildContentIdentifier;
import org.motechproject.mtraining.domain.Content;
import org.motechproject.mtraining.domain.Node;
import org.motechproject.mtraining.validator.CourseStructureValidator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * Abstract class responsible for validating and saving the given tree structured {@link Node}.
 * Expects an implementer to provide methods for validating and saving a node based on the {@link org.motechproject.mtraining.domain.NodeType}
 */

public abstract class NodeHandler {

    @Autowired
    private CourseStructureValidator courseStructureValidator;
    @Autowired
    private EventRelay eventRelay;

    protected abstract void validateNodeData(Object nodeData);

    protected abstract Content saveAndRaiseEvent(Node node);

    protected void sendEvent(String subject, UUID contentId) {
        HashMap<String, Object> eventParameters = new HashMap<>();
        eventParameters.put(MTrainingEventConstants.NODE_ID, contentId);
        MotechEvent motechEvent = new MotechEvent(subject, eventParameters);
        eventRelay.sendEventMessage(motechEvent);
    }

    protected List<ChildContentIdentifier> getChildContentIdentifiers(Node node) {
        List<ChildContentIdentifier> childContentIdentifiers = new ArrayList<>();
        for (Node childNode : node.getChildNodes()) {
            Content persistentEntity = childNode.getPersistentEntity();
            if (persistentEntity != null) {
                childContentIdentifiers.add(new ChildContentIdentifier(persistentEntity.getContentId(), persistentEntity.getVersion()));
            }
        }
        return childContentIdentifiers;
    }

    protected CourseStructureValidator validator() {
        return courseStructureValidator;
    }
}
