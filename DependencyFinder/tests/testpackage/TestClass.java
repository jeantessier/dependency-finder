package testpackage;

import java.io.*;

public class TestClass implements TargetInterface {
	public Object    test_attribute;

	public TestClass source_attribute;
	public TestClass target_attribute;
	
	public TestClass SourceMethod() {
		TestMethod("foobar");
		return this;
	}

	public void TargetMethod() {
	}
	
	public void TestMethod(String text) {
		target_attribute = this;
		
		TargetMethod();

		TargetClass target = new TargetClass();
		target.TargetMethod();

		target.target_attribute = new Object();

		target_attribute = this;
	}
}
