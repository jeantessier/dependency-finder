package ModifiedPackage;

/** @level public */
public interface ModifiedInterface {
	public int removed_field = 1;

	public int deprecated_field = 1;

	/** @level public */
	public int undocumented_field = 1;
	
	public int modified_field = 1;

	/** @level private */
	public int documented_field = 1;

	/** @deprecated */
	public int undeprecated_field = 1;

	public void RemovedMethod();

	public void DeprecatedMethod();

	/** @level public */
	public void UndocumentedMethod();
	
	public void ModifiedMethod();

	/** @level private */
	public void DocumentedMethod();

	/** @deprecated */
	public void UndeprecatedMethod();
}
