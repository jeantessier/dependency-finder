package ModifiedPackage;

/** @level public */
public class ModifiedClass {
	public int removed_field;

	public int deprecated_field;

	/** @level public */
	public int undocumented_field;
	
	public int modified_field;

	/** @level private */
	public int documented_field;

	/** @deprecated */
	public int undeprecated_field;

	/**
	 *  Removed Constructor
	 */
	public ModifiedClass() {
	}

	/**
	 *  Deprecated Constructor
	 */
	public ModifiedClass(int i) {
	}

	/**
	 *  Undocumented Constructor
	 *  @level public
	 */
	public ModifiedClass(int i, int j) {
	}

	/**
	 *  Modified Constructor
	 */
	public ModifiedClass(int i, int j, int k) {
	}
	
	/**
	 *  Documented Constructor
	 *  @level private
	 */
	public ModifiedClass(int i, int j, int k, int m) {
	}

	/**
	 *  Deprecated Constructor
	 *  @deprecated
	 */
	public ModifiedClass(int i, int j, int k, int m, int n) {
	}
	
	public void RemovedMethod() {
	}

	public void DeprecatedMethod() {
	}

	/** @level public */
	public void UndocumentedMethod() {
	}
	
	public void ModifiedMethod() {
	}

	/** @level private */
	public void DocumentedMethod() {
	}

	/** @deprecated */
	public void UndeprecatedMethod() {
	}
}
