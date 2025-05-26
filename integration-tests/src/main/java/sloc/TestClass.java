package sloc;

import java.util.stream.*;

/*
 *  This class has a total of 4 useful lines.
 *  This class has a hidden default constructor for 1 line.
 */
public class TestClass extends TestAbstractClass {
    /*
     *  This method has 2 useful lines.
     */
    public void method2() {
        System.out.println("foobar");
    }

    /*
     *  This method has 3 useful lines.
     *  We add empty lines so that SLOC, raw method length, and effective
     *  method length measurements are all different from one another.
     *
     *  Because the "return" keyword is on the first useful line, that line
     *  will appear twice in the method's line number table.  The SLOC
     *  measurement counts the entries in the table and adds 1 for the
     *  declaration, so that will be 4 + 1 = 5.  The raw method length
     *  measurement looks at the line numbers in the table and does a simple
     *  max - min + 1 = 7.  The effective method length measurement counts
     *  the distinct line numbers in the table and should be 3 here.
     */
    public int method3() {

        return Stream.of("abc", "def", "ghi")


                .mapToInt(String::length)


                .sum();

    }
}
