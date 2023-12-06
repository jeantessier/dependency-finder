package ModifiedPackage;

public class DifferentConstantPool {
    /*
     * We add calls so the order of symbols in the
     * constant pool is different.
     */

    public void callingNewMethodRefInfo() {
	newMethodRefInfo();
    }

    public void newMethodRefInfo() {
    }

    public void callingMovedMethodRefInfo() {
	movedMethodRefInfo();
    }

    public void movedMethodRefInfo() {
    }
}
