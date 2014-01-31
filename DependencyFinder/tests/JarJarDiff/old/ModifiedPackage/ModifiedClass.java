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

    public int deprecatedField;

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

    /** @deprecated */
    public int undeprecatedField;

    /**
     *  Removed Constructor
     */
    public ModifiedClass() {
    }

    /**
     *  Deprecated Constructor
     */
    public ModifiedClass(int i) {
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
     *  Undeprecated Constructor
     *  @deprecated
     */
    public ModifiedClass(int i, int j, int k, int m, int n) {
    }
    
    public void removedMethod() {
    }

    public void deprecatedMethod() {
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

    /** @deprecated */
    public void undeprecatedMethod() {
    }
}
