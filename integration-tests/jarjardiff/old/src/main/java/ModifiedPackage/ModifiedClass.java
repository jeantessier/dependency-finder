package ModifiedPackage;

/** @level public */
public class ModifiedClass {
    public int removedField;

    public static final String REMOVED_LESS_THAN_STRING = "<";
    public static final String REMOVED_AMPERSAND_STRING = "&";
    public static final String REMOVED_GREATER_THAN_STRING = ">";
    public static final String REMOVED_QUOTE_STRING = "\"";
    public static final String REMOVED_APOSTROPHE_STRING = "'";
    public static final String REMOVED_NON_ASCII_STRING = "\u00A5";

    public int deprecatedFieldByAnnotation;

    public int deprecatedFieldByJavadocTag;

    /** @level public */
    public int undocumentedField;
    
    public int modifiedField;

    public static final char MODIFIED_LESS_THAN_STRING = '<';
    public static final char MODIFIED_AMPERSAND_STRING = '&';
    public static final char MODIFIED_GREATER_THAN_STRING = '>';
    public static final char MODIFIED_QUOTE_STRING = '"';
    public static final char MODIFIED_APOSTROPHE_STRING = '\'';
    public static final char MODIFIED_NON_ASCII_STRING = '\u00A5';

    /** @level private */
    public int documentedField;

    @Deprecated
    public int undeprecatedFieldByAnnotation;

    /** @deprecated */
    public int undeprecatedFieldByJavadocTag;

    /**
     *  Removed Constructor
     */
    public ModifiedClass() {
    }

    /**
     *  Deprecated Constructor by annotation
     */
    public ModifiedClass(int i) {
    }

    /**
     *  Deprecated Constructor by javadoc tag
     */
    public ModifiedClass(long i) {
    }

    /**
     *  Undocumented Constructor
     *  @level public
     */
    public ModifiedClass(int i, int j) {
    }

    /**
     *  Modified Constructor
     */
    public ModifiedClass(int i, int j, int k) {
    }

    /**
     *  Modified Code Constructor
     */
    public ModifiedClass(float f) {
        float g = 10 * f;
    }
    
    /**
     *  Documented Constructor
     *  @level private
     */
    public ModifiedClass(int i, int j, int k, int m) {
    }

    /**
     *  Undeprecated Constructor by annotation
     */
    @Deprecated
    public ModifiedClass(int i, int j, int k, int m, int n) {
    }

    /**
     *  Undeprecated Constructor by javadoc tag
     *  @deprecated
     */
    public ModifiedClass(long i, int j, int k, int m, int n) {
    }

    public void removedMethod() {
    }

    public void deprecatedMethodByAnnotation() {
    }

    public void deprecatedMethodByJavadocTag() {
    }

    /** @level public */
    public void undocumentedMethod() {
    }
    
    public void modifiedMethod() {
    }
    
    public void modifiedCodeMethod() {
        int i = 10 * 10;
    }

    /** @level private */
    public void documentedMethod() {
    }

    @Deprecated
    public void undeprecatedMethodByAnnotation() {
    }

    /** @deprecated */
    public void undeprecatedMethodByJavadocTag() {
    }
}
