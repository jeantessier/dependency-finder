package ModifiedPackage;

/** @level public */
public class ModifiedClass {
	/** @deprecated */
	public int deprecatedField;

	/** @level private */
	public int undocumentedField;
	
	public float modifiedField;

	/** @level public */
	public int documentedField;

	public int undeprecatedField;

	public int newField;

	/**
	 *  Deprecated Constructor
	 *  @deprecated
	 */
	public ModifiedClass(int i) {
	}

	/**
	 *  Undocumented Constructor
	 *  @level private
	 */
	public ModifiedClass(int i, int j) {
	}

	/**
	 *  Modified Constructor
	 */
	private ModifiedClass(int i, int j, int k) {
	}
	
	/**
	 *  Documented Constructor
	 *  @level public
	 */
	public ModifiedClass(int i, int j, int k, int m) {
	}

	/**
	 *  Deprecated Constructor
	 */
	public ModifiedClass(int i, int j, int k, int m, int n) {
	}

	/**
	 *  New Constructor
	 */
	public ModifiedClass(int i, int j, int k, int m, int n, int p) {
	}

	/** @deprecated */
	public void deprecatedMethod() {
	}

	/** @level private */
	public void undocumentedMethod() {
	}
	
	public boolean modifiedMethod() {
		return false;
	}

	/** @level public */
	public void documentedMethod() {
	}

	public void undeprecatedMethod() {
	}
	
	public void newMethod() {
	}
}
