package other;

public class SourceClass extends test.TestClass {
	public test.TestClass source_attribute;

	public test.TestClass SourceMethod() {
		target_attribute = this;
		TestMethod("foobar");
		return this;
	}
}
