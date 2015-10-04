package otherpackage;

public class SourceClass extends testpackage.TestClass {
    public testpackage.TestClass sourceAttribute;

    public testpackage.TestClass sourceMethod() {
        targetAttribute = this;
        super.testMethod("foobar");
        return this;
    }
}
