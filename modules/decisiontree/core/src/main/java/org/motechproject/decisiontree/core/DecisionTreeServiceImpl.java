package org.motechproject.decisiontree.core;

import org.motechproject.decisiontree.core.model.FlowSession;
import org.motechproject.decisiontree.core.model.Node;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.decisiontree.core.repository.TreeRecord;
import org.motechproject.decisiontree.core.repository.TreeRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static java.lang.String.format;

@Component
public class DecisionTreeServiceImpl implements DecisionTreeService {
    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    private TreeRecordService treeRecordService;
    private TreeNodeLocator treeNodeLocator;

    @Autowired
    public DecisionTreeServiceImpl(TreeRecordService treeRecordService, TreeNodeLocator treeNodeLocator) {
        this.treeRecordService = treeRecordService;
        this.treeNodeLocator = treeNodeLocator;
    }

    @Override
    public Node getNode(String treeName, String path, FlowSession session) {
        Node node = treeNodeLocator.findNode(findTreeByName(treeName), path, session);
        logger.info(format("Looking for node by path: %s, found: %s", path, node.getPrompts()));

        return node;
    }

    @Override
    public Node getRootNode(String treeName, FlowSession session) {
        Node node = treeNodeLocator.findRootNode(findTreeByName(treeName), session);
        logger.info(format("Looking for node by path: , found: %s", node.getPrompts()));

        return node;
    }

    @Override
    public List<Tree> getDecisionTrees() {
        List<TreeRecord> treeRecords = treeRecordService.retrieveAll();
        List<Tree> trees = new ArrayList<>();
        for (TreeRecord treeRecord : treeRecords) {
            trees.add(treeRecord.toTree());
        }
        return trees;
    }

    @Override
    public Tree getDecisionTree(String treeId) {
        TreeRecord treeRecord = treeRecordService.retrieve("id", treeId);
        return treeRecord.toTree();
    }

    @Override
    public void saveDecisionTree(final Tree tree) {
//todo: magic
        //cmt treeRecordService.addOrReplace(tree);
    }

    @Override
    public void deleteDecisionTree(final String treeId) {
//todo: magic
//cmt        Tree tree = treeRecordService.get(treeId);
//cmt        logger.info(format("Removing tree with name: %s and id: %s", tree.getName(), tree.getId()));
//cmt        treeRecordService.remove(tree);
    }

    private Tree findTreeByName(String treeName) {
        TreeRecord treeRecord = treeRecordService.findByName(treeName);
        logger.info(format("Looking for tree by name: %s, found: %s", treeName, treeRecord));
        return treeRecord.toTree();
    }
}
