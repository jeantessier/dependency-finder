package otherpackage;

public class SourceClass extends testpackage.TestClass {
	public testpackage.TestClass source_attribute;

	public testpackage.TestClass SourceMethod() {
		target_attribute = this;
		super.TestMethod("foobar");
		return this;
	}
}
