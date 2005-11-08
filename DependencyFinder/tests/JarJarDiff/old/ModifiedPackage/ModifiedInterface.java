package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
    public int removedField = 1;

    public int deprecatedField = 1;

    /** @level public */
    public int undocumentedField = 1;
    
    public int modifiedField = 1;

    public int modifiedValueField = 1;

    /** @level private */
    public int documentedField = 1;

    /** @deprecated */
    public int undeprecatedField = 1;

    public void removedMethod();

    public void deprecatedMethod();

    /** @level public */
    public void undocumentedMethod();
    
    public void modifiedMethod();

    /** @level private */
    public void documentedMethod();

    /** @deprecated */
    public void undeprecatedMethod();
}
