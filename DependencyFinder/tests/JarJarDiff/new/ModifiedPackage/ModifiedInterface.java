package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
	/** @deprecated */
	public int deprecated_field = 1;

	/** @level private */
	public int undocumented_field = 1;
	
	public float modified_field = 1;

	/** @level public */
	public int documented_field = 1;

	public int undeprecated_field = 1;

	public int new_field = 1;

	/** @deprecated */
	public void DeprecatedMethod();

	/** @level private */
	public void UndocumentedMethod();
	
	public boolean ModifiedMethod();

	/** @level public */
	public void DocumentedMethod();

	public void UndeprecatedMethod();

	public void NewMethod();
}
