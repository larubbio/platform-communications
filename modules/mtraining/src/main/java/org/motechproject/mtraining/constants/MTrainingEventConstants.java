package org.motechproject.mtraining.constants;

/**
 * Class having all the constants used in event mechanism
 */
public final class MTrainingEventConstants {
    private static final String BASE_SUBJECT = "org.motechproject.mtraining";

    public static final String MESSAGE_CREATION_EVENT = BASE_SUBJECT + "message.creation";
    public static final String CHAPTER_CREATION_EVENT = BASE_SUBJECT + "chapter.creation";
    public static final String MODULE_CREATION_EVENT = BASE_SUBJECT + "chapter.creation";
    public static final String COURSE_CREATION_EVENT = BASE_SUBJECT + "chapter.creation";

    public static final String NODE_ID = "NODE_ID";

    private MTrainingEventConstants() {
    }
}
