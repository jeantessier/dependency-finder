package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
    public int removedField = 1;

    public int deprecatedFieldByAnnotation = 1;

    public int deprecatedFieldByJavadocTag = 1;

    /** @level public */
    public int undocumentedField = 1;
    
    public int modifiedField = 1;

    public int modifiedValueField = 1;

    /** @level private */
    public int documentedField = 1;

    @Deprecated
    public int undeprecatedFieldByAnnotation = 1;

    /** @deprecated */
    public int undeprecatedFieldByJavadocTag = 1;

    public void removedMethod();

    public void deprecatedMethodByAnnotation();

    public void deprecatedMethodByJavadocTag();

    /** @level public */
    public void undocumentedMethod();
    
    public void modifiedMethod();

    /** @level private */
    public void documentedMethod();

    @Deprecated
    public void undeprecatedMethodByAnnotation();

    /** @deprecated */
    public void undeprecatedMethodByJavadocTag();
}
