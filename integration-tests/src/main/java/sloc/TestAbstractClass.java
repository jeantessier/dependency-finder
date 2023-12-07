package sloc;

/*
 *  This class has a total of 16 useful lines.
 *  This class has a hidden default constructor for 1 line.
 *  This class has a hidden abstract method for Method() for 1 line.
 */
public abstract class TestAbstractClass implements TestInterface {
    /*
     *  The method has 13 useful lines.
     *  All 'case X:' statements are folded in the switch()
     *  instruction.  The last break is ignored.
     */
    public void method1() {
        switch (5) {
            case 1:
                System.out.println(1);
                break;
            case 2:
                System.out.println(1);
                break;
            case 3:
                System.out.println(1);
                break;
            case 4:
                System.out.println(1);
                break;
            case 5:
                System.out.println(1);
                break;
            default:
                System.out.println("default");
                break;
        }
    }
}
