package ModifiedPackage;

public abstract class ModifiedClass {

    /*
     * Fields
     */
    
    private int privateToPrivateField;
    private int privateToProtectedField;
    private int privateToPackageField;
    private int privateToPublicField;

    protected int protectedToPrivateField;
    protected int protectedToProtectedField;
    protected int protectedToPackageField;
    protected int protectedToPublicField;

    int packageToPrivateField;
    int packageToProtectedField;
    int packageToPackageField;
    int packageToPublicField;

    public int publicToPrivateField;
    public int publicToProtectedField;
    public int publicToPackageField;
    public int publicToPublicField;

    public static int staticToStaticField;
    public static int staticToNonStaticField;
    public int nonStaticToStaticField;
    public int nonStaticToNonStaticField;

    public final int finalToFinalField = 1;
    public final int finalToNonFinalField = 1;
    public int nonFinalToFinalField = 1;
    public int nonFinalToNonFinalField = 1;

    public int typeToSameTypeField;
    public int typeToDifferentTypeField;
    
    /*
     * Constructors
     */

    private ModifiedClass(int i1) {}    // privateToPrivateConstructor
    private ModifiedClass(long l1) {}   // privateToProtectedConstructor
    private ModifiedClass(float f1) {}  // privateToPackageConstructor
    private ModifiedClass(double d1) {} // privateToPublicConstructor

    protected ModifiedClass(int i1, int i2) {}       // protectedToPrivateConstructor
    protected ModifiedClass(long l1, long l2) {}     // protectedToProtectedConstructor
    protected ModifiedClass(float f1, float f2) {}   // protectedToPackageConstructor
    protected ModifiedClass(double d1, double d2) {} // protectedToPublicConstructor

    ModifiedClass(int i1, int i2, int i3) {}          // packageToPrivateConstructor
    ModifiedClass(long l1, long l2, long l3) {}       // packageToProtectedConstructor
    ModifiedClass(float f1, float f2, float f3) { }   // packageToPackageConstructor
    ModifiedClass(double d1, double d2, double d3) {} // packageToPublicConstructor

    public ModifiedClass(int i1, int i2, int i3, int i4) {}             // publicToPrivateConstructor
    public ModifiedClass(long l1, long l2, long l3, long l4) {}         // publicToProtectedConstructor
    public ModifiedClass(float f1, float f2, float f3, float f4) { }    // publicToPackageConstructor
    public ModifiedClass(double d1, double d2, double d3, double d4) {} // publicToPublicConstructor

    public ModifiedClass(Object o1) throws Exception {}            // throwsToSameThrowsConstructor
    public ModifiedClass(Object o1, Object o2) throws Exception {} // throwsToDifferentThrowsConstructor

    /*
     * Methods
     */
    
    private void privateToPrivateMethod() {}
    private void privateToProtectedMethod() {}
    private void privateToPackageMethod() {}
    private void privateToPublicMethod() {}

    protected void protectedToPrivateMethod() {}
    protected void protectedToProtectedMethod() {}
    protected void protectedToPackageMethod() {}
    protected void protectedToPublicMethod() {}

    void packageToPrivateMethod() {}
    void packageToProtectedMethod() {}
    void packageToPackageMethod() {}
    void packageToPublicMethod() {}

    public void publicToPrivateMethod() {}
    public void publicToProtectedMethod() {}
    public void publicToPackageMethod() {}
    public void publicToPublicMethod() {}

    public abstract void abstractToAbstractMethod();
    public abstract void abstractToConcreteMethod();
    public void concreteToAbstractMethod() {}
    public void concreteToConcreteMethod() {}

    public static void staticToStaticMethod() {}
    public static void staticToNonStaticMethod() {}
    public void nonStaticToStaticMethod() {}
    public void nonStaticToNonStaticMethod() {}

    public final void finalToFinalMethod() {}
    public final void finalToNonFinalMethod() {}
    public void nonFinalToFinalMethod() {}
    public void nonFinalToNonFinalMethod() {}

    public int returnTypeToSameReturnTypeMethod() {return 1;}
    public int returnTypeToDifferentReturnTypeMethod() {return 1;}

    public void throwsToSameThrowsMethod() throws Exception {}
    public void throwsToDifferentThrowsMethod() throws Exception {}
}
