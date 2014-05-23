package org.motechproject.decisiontree.core.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.decisiontree.core.DecisionTreeService;
import org.motechproject.decisiontree.core.model.*;
import org.motechproject.decisiontree.core.repository.TreeRecord;
import org.motechproject.decisiontree.core.repository.TreeRecordService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TreeRecordIT extends BasePaxIT {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Inject
    private DecisionTreeService decisionTreeService;

    @Inject
    private TreeRecordService treeRecordService;

    @Test
    public void testDecisionTreeService() {
        Tree tree = new Tree()
                .setName("tree1")
                .setDescription("desc")
                .setRootTransition(new Transition().setDestinationNode(new Node()
                                .setActionsBefore(asList(Action.newBuilder()
                                        .setEventId("event_x")
                                        .build()))
                                .setPrompts(new TextToSpeechPrompt()
                                        .setMessage("haha"))
                                .setTransitions(new Object[][]{
                                        {"1", new Transition()
                                                .setName("sick")},
                                        {"2", new Transition()
                                                .setName("healthy")}
                                })
                ));

        TreeRecord treeRecord = new TreeRecord(tree);
        treeRecordService.create(treeRecord);
        logger.info("********** created TreeRecord id {} ", treeRecordService.getDetachedField(treeRecord, "id"));
    }
}
