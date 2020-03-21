/**
 * @invar | 10 < true
 * @invar | 10 < y
 * @invar | 10 < x
 * @invar | 10 < getX()
 */
public class invariants_resolve_error {
	
	/**
	 * @invar | 10 < true
	 * @invar | 10 < y
	 */
	private int x;
	
	/**
	 * @invar | 10 < true
	 * @invar | 10 < x
	 */
	int getX() { return x; }
	
}