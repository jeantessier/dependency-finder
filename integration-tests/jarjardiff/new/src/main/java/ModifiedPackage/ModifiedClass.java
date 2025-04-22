package ModifiedPackage;

/** @level public */
public class ModifiedClass {
    @Deprecated
    public int deprecatedFieldByAnnotation;

    /** @deprecated */
    public int deprecatedFieldByJavadocTag;

    /** @level private */
    public int undocumentedField;
    
    public float modifiedField;

    public static final String MODIFIED_LESS_THAN_STRING = "<";
    public static final String MODIFIED_AMPERSAND_STRING = "&";
    public static final String MODIFIED_GREATER_THAN_STRING = ">";
    public static final String MODIFIED_QUOTE_STRING = "\"";
    public static final String MODIFIED_APOSTROPHE_STRING = "'";
    public static final String MODIFIED_NON_ASCII_STRING = "\u00A5";

    /** @level public */
    public int documentedField;

    public int undeprecatedFieldByAnnotation;

    public int undeprecatedFieldByJavadocTag;

    public int newField;

    public static final String NEW_LESS_THAN_STRING = "<";
    public static final String NEW_AMPERSAND_STRING = "&";
    public static final String NEW_GREATER_THAN_STRING = ">";
    public static final String NEW_QUOTE_STRING = "\"";
    public static final String NEW_APOSTROPHE_STRING = "'";
    public static final String NEW_NON_ASCII_STRING = "\u00A5";

    /**
     *  Deprecated Constructor by annotation
     */
    @Deprecated
    public ModifiedClass(int i) {
    }

    /**
     *  Deprecated Constructor by javadoc tag
     *  @deprecated
     */
    public ModifiedClass(long i) {
    }

    /**
     *  Undocumented Constructor
     *  @level private
     */
    public ModifiedClass(int i, int j) {
    }

    /**
     *  Modified Constructor
     */
    private ModifiedClass(int i, int j, int k) {
    }

    /**
     *  Modified Code Constructor
     */
    public ModifiedClass(float f) {
        float g = f + f;
    }
    
    /**
     *  Documented Constructor
     *  @level public
     */
    public ModifiedClass(int i, int j, int k, int m) {
    }

    /**
     *  Undeprecated Constructor by annotation
     */
    public ModifiedClass(int i, int j, int k, int m, int n) {
    }

    /**
     *  Undeprecated Constructor by javadoc tag
     */
    public ModifiedClass(long i, int j, int k, int m, int n) {
    }

    /**
     *  New Constructor
     */
    public ModifiedClass(int i, int j, int k, int m, int n, int p) {
    }

    @Deprecated
    public void deprecatedMethodByAnnotation() {
    }

    /** @deprecated */
    public void deprecatedMethodByJavadocTag() {
    }

    /** @level private */
    public void undocumentedMethod() {
    }
    
    public boolean modifiedMethod() {
        return false;
    }
    
    public void modifiedCodeMethod() {
        int i = 2 + 2;
    }

    /** @level public */
    public void documentedMethod() {
    }

    public void undeprecatedMethodByAnnotation() {
    }

    public void undeprecatedMethodByJavadocTag() {
    }

    public void newMethod() {
    }
}
