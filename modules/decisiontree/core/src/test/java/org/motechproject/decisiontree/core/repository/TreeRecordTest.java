package org.motechproject.decisiontree.core.repository;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.motechproject.decisiontree.core.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class TreeRecordTest {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    private static final String SAMPLE_TREE_STRING_VAL = serializeTree(buildSampleTree());

    private static String serializeTree(Tree tree) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(tree);
        } catch (IOException e) {
            throw new IllegalStateException("Error while serializing from a tree: " + e.getMessage());
        }

    }

    private static Tree buildSampleTree() {
        return new Tree()
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
    }

    @Test
    public void shouldCreateTreeRecord() throws Exception {
        Tree tree = buildSampleTree();
        TreeRecord treeRecord = new TreeRecord("name", "description", tree);
        assertEquals("name", treeRecord.getName());
        assertEquals("description", treeRecord.getDescription());
        assertEquals(SAMPLE_TREE_STRING_VAL, new String(ArrayUtils.toPrimitive(treeRecord.getData()), "UTF-8"));
        assertEquals(tree, treeRecord.toTree());
    }

    @Test
    public void shouldSerializeAndDeserializeTree() throws Exception {
        Tree tree1 = buildSampleTree();

        TreeRecord treeRecord = new TreeRecord(tree1);

        Tree tree2 = treeRecord.toTree();

        assertEquals(tree1, tree2);
    }

}
