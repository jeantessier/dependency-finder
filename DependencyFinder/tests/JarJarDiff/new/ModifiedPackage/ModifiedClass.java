package ModifiedPackage;

/** @level public */
public class ModifiedClass {
	/** @deprecated */
	public int deprecated_field;

	/** @level private */
	public int undocumented_field;
	
	public float modified_field;

	/** @level public */
	public int documented_field;

	public int undeprecated_field;

	public int new_field;

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
	public void DeprecatedMethod() {
	}

	/** @level private */
	public void UndocumentedMethod() {
	}
	
	public boolean ModifiedMethod() {
		return false;
	}

	/** @level public */
	public void DocumentedMethod() {
	}

	public void UndeprecatedMethod() {
	}
	
	public void NewMethod() {
	}
}
