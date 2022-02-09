package lists;

public class ArrayList extends List {
	
	/**
	 * @invar | elements != null
	 */
	private Object[] elements = new Object[0];
	
	public Object[] getElements() {
		return elements.clone();
	}
	
}