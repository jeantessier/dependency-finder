package testpackage;

public class SourceClass extends TestClass {
	public TestClass source_attribute;

	public TestClass SourceMethod() {
		target_attribute = this;
		super.TestMethod("foobar");
		return this;
	}
}
