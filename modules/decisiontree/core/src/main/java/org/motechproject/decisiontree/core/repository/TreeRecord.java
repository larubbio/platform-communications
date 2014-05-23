package org.motechproject.decisiontree.core.repository;

import org.apache.commons.lang.ArrayUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.motechproject.decisiontree.core.model.Tree;
import org.motechproject.mds.annotations.Entity;
import org.motechproject.mds.annotations.Field;
import org.motechproject.mds.annotations.Ignore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;


/**
 * todo
 */
@Entity
public class TreeRecord {

    private Logger logger = LoggerFactory.getLogger((this.getClass()));

    @Field(required = true)
    private String name;

    @Field
    private String description;

    @Field
    private byte[] data;

    private static byte[] serializeTree(Tree tree) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return bytesFromString(mapper.writeValueAsString(tree));
        } catch (IOException e) {
            throw new IllegalStateException("Error while serializing from a tree: " + e.getMessage());
        }
    }

    private static byte[] bytesFromString(String string) {
        try {
            return string.getBytes("UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("String to byte[] conversion error: " + e.getMessage());
        }
    }

    private static String stringFromBytes(byte[] bytes) {
        try {
            return new String(bytes, "UTF-8");
        } catch (IOException e) {
            throw new IllegalStateException("Error converting byte[] to String: " + e.getMessage());
        }
    }

    @Ignore
    public void fromTree(Tree tree) {
        data = serializeTree(tree);
        logger.info("********** tree -> data : {}", data);
    }

    @Ignore
    public Tree toTree() {
        ObjectMapper mapper = new ObjectMapper();
        Tree tree;
        try {
            tree = mapper.readValue(stringFromBytes(data), Tree.class);
        } catch (IOException e) {
            throw new IllegalStateException("Error while deserializing to a tree: " + e.getMessage());
        }
        return tree;
    }

    public TreeRecord(String name, String description, Tree tree) {
        this.name = name;
        this.description = description;
        if (tree != null) {
            this.data = serializeTree(tree);
        }
    }

    public TreeRecord() {
        this(null, null, null);
    }

    public TreeRecord(Tree tree) {
        this(tree.getName(), tree.getDescription(), tree);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Byte[] getData() {
        Byte[] bytes = new Byte[data.length];
        int i = 0;
        for (byte b : data) {
            bytes[i++] = b;
        }
        return bytes;
    }

    public void setData(Byte[] data) {
        this.data = ArrayUtils.toPrimitive(data);
    }
}
