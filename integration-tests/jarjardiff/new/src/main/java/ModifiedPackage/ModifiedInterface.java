package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
    @Deprecated
    public int deprecatedFieldByAnnotation = 1;

    /** @deprecated */
    public int deprecatedFieldByJavadocTag = 1;

    /** @level private */
    public int undocumentedField = 1;
    
    public float modifiedField = 1;

    public int modifiedValueField = 2;

    /** @level public */
    public int documentedField = 1;

    public int undeprecatedFieldByAnnotation = 1;

    public int undeprecatedFieldByJavadocTag = 1;

    public int newField = 1;

    @Deprecated
    public void deprecatedMethodByAnnotation();

    /** @deprecated */
    public void deprecatedMethodByJavadocTag();

    /** @level private */
    public void undocumentedMethod();
    
    public default void modifiedMethod() {};

    /** @level public */
    public void documentedMethod();

    public void undeprecatedMethodByAnnotation();

    public void undeprecatedMethodByJavadocTag();

    public void newMethod();
}
