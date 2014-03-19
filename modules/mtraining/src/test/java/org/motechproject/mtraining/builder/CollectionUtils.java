package org.motechproject.mtraining.builder;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtils {

    public static <T> List<T> copy(List<T> itemsToCopy) {
        List<T> copiedItems = new ArrayList<>();
        for (T item : itemsToCopy) {
            copiedItems.add(item);
        }
        return copiedItems;
    }
}
