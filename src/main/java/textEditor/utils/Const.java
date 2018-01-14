package textEditor.utils;

public class Const {
    public class Format {
        public static final String BOLD_PATTERN_KEY = "weight";
        public static final String ITALIC_PATTERN_KEY = "style";
        public static final String UNDERSCORE_PATTERN_KEY = "decoration";
        public static final String ALIGN_PATTERN_KEY = "alignment";

        public static final String FONTSIZE_PATTERN_KEY = "fontsize";
        public static final String FONTFAMILY_PATTERN_KEY = "fontFamily";
        public static final String FONTCOLOR_PATTERN_KEY = "color";
        public static final String HEADING_PATTERN_KEY = "heading";

        public static final String ALIGN_LEFT = "alignmentLeft";
        public static final String ALIGN_RIGHT = "alignmentRight";
        public static final String ALIGN_CENTER = "alignmentCenter";
        public static final String ALIGN_ADJUST = "alignmentAdjust";

        public static final String TEXT_BOLD = "Bold";
        public static final String TEXT_ITALIC = "Italic";
        public static final String TEXT_UNDERSCORE = "Underscore";
        public static final String TEXT_NORMAL = "Normal";

        public static final String BULLET_LIST = "BulletList";
        public static final String BULLET_UNLIST = "Unlist";
    }

    public class RegistrationFields {
        public final static int USER_LOGIN = 0;
        public final static int USER_PASSWORD = 1;
        public final static int EMAIL = 2;
        public final static int ZIPCODE = 3;
        public final static int ADRESS = 4;
        public final static int REGION = 5;
        public final static int LAST_NAME = 5;
        public final static int FIRST_NAME = 6;
    }

    public class Files {
        public final static String PROJECTS_PATH = "project_model";
    }
}
