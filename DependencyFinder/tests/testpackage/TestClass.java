package testpackage;

public class TestClass implements TargetInterface {
    public Object    testAttribute;

    public TestClass sourceAttribute;
    public TestClass targetAttribute;
    
    public TestClass sourceMethod() {
        testMethod("foobar");
        return this;
    }

    public void targetMethod() {
    }
    
    public void testMethod(String text) {
        targetAttribute = this;
        targetMethod();

        TargetClass target = new TargetClass();
        target.targetMethod();
        target.targetAttribute = new Object();
    }
}
