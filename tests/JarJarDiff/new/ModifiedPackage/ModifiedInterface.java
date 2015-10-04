package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
    /** @deprecated */
    public int deprecatedField = 1;

    /** @level private */
    public int undocumentedField = 1;
    
    public float modifiedField = 1;

    public int modifiedValueField = 2;

    /** @level public */
    public int documentedField = 1;

    public int undeprecatedField = 1;

    public int newField = 1;

    /** @deprecated */
    public void deprecatedMethod();

    /** @level private */
    public void undocumentedMethod();
    
    public boolean modifiedMethod();

    /** @level public */
    public void documentedMethod();

    public void undeprecatedMethod();

    public void newMethod();
}
