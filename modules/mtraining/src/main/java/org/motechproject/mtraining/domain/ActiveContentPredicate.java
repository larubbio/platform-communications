package org.motechproject.mtraining.domain;

import org.apache.commons.collections.Predicate;

class ActiveContentPredicate implements Predicate {
    @Override
    public boolean evaluate(Object object) {
        Content content = (Content) object;
        return content.isActive();
    }
}
