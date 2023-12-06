package ModifiedPackage;

public abstract class ModifiedClass {

    /*
     * Fields
     */
    
    private int privateToPrivateField;
    protected int privateToProtectedField;
    int privateToPackageField;
    public int privateToPublicField;

    private int protectedToPrivateField;
    protected int protectedToProtectedField;
    int protectedToPackageField;
    public int protectedToPublicField;

    private int packageToPrivateField;
    protected int packageToProtectedField;
    int packageToPackageField;
    public int packageToPublicField;

    private int publicToPrivateField;
    protected int publicToProtectedField;
    int publicToPackageField;
    public int publicToPublicField;

    public static int staticToStaticField;
    public int staticToNonStaticField;
    public static int nonStaticToStaticField;
    public int nonStaticToNonStaticField;

    public final int finalToFinalField = 1;
    public int finalToNonFinalField = 1;
    public final int nonFinalToFinalField = 1;
    public int nonFinalToNonFinalField = 1;

    public int typeToSameTypeField;
    public float typeToDifferentTypeField;

    /*
     * Constructors
     */

    private ModifiedClass(int i1) {}    // privateToPrivateConstructor
    protected ModifiedClass(long l1) {} // privateToProtectedConstructor
    ModifiedClass(float f1) {}          // privateToPackageConstructor
    public ModifiedClass(double d1) {}  // privateToPublicConstructor

    private ModifiedClass(int i1, int i2) {}      // protectedToPrivateConstructor
    protected ModifiedClass(long l1, long l2) {}  // protectedToProtectedConstructor
    ModifiedClass(float f1, float f2) {}          // protectedToPackageConstructor
    public ModifiedClass(double d1, double d2) {} // protectedToPublicConstructor

    private ModifiedClass(int i1, int i2, int i3) {}         // packageToPrivateConstructor
    protected ModifiedClass(long l1, long l2, long l3) {}    // packageToProtectedConstructor
    ModifiedClass(float f1, float f2, float f3) { }          // packageToPackageConstructor
    public ModifiedClass(double d1, double d2, double d3) {} // packageToPublicConstructor

    private ModifiedClass(int i1, int i2, int i3, int i4) {}            // publicToPrivateConstructor
    protected ModifiedClass(long l1, long l2, long l3, long l4) {}      // publicToProtectedConstructor
    ModifiedClass(float f1, float f2, float f3, float f4) { }           // publicToPackageConstructor
    public ModifiedClass(double d1, double d2, double d3, double d4) {} // publicToPublicConstructor

    public ModifiedClass(Object o1) throws Exception {}                       // throwsToSameThrowsConstructor
    public ModifiedClass(Object o1, Object o2) throws NullPointerException {} // throwsToDifferentThrowsConstructor
    
    /*
     * Methods
     */
    
    private void privateToPrivateMethod() {}
    protected void privateToProtectedMethod() {}
    void privateToPackageMethod() {}
    public void privateToPublicMethod() {}

    private void protectedToPrivateMethod() {}
    protected void protectedToProtectedMethod() {}
    void protectedToPackageMethod() {}
    public void protectedToPublicMethod() {}

    private void packageToPrivateMethod() {}
    protected void packageToProtectedMethod() {}
    void packageToPackageMethod() {}
    public void packageToPublicMethod() {}

    private void publicToPrivateMethod() {}
    protected void publicToProtectedMethod() {}
    void publicToPackageMethod() {}
    public void publicToPublicMethod() {}

    public abstract void abstractToAbstractMethod();
    public void abstractToConcreteMethod() {}
    public abstract void concreteToAbstractMethod();
    public void concreteToConcreteMethod() {}

    public static void staticToStaticMethod() {}
    public void staticToNonStaticMethod() {}
    public static void nonStaticToStaticMethod() {}
    public void nonStaticToNonStaticMethod() {}

    public final void finalToFinalMethod() {}
    public void finalToNonFinalMethod() {}
    public final void nonFinalToFinalMethod() {}
    public void nonFinalToNonFinalMethod() {}

    public int returnTypeToSameReturnTypeMethod() {return 1;}
    public float returnTypeToDifferentReturnTypeMethod() {return 1;}

    public void throwsToSameThrowsMethod() throws Exception {}
    public void throwsToDifferentThrowsMethod() throws NullPointerException {}
}
