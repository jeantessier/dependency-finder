package testpackage;

public class SourceClass extends TestClass {
    public TestClass sourceAttribute;

    public TestClass sourceMethod() {
        targetAttribute = this;
        super.testMethod("foobar");
        return this;
    }
}
