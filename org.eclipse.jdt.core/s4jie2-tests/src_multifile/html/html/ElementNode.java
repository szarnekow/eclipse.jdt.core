package html;

public class ElementNode extends Node {
	
	/** @invar | tag != null */
	private String tag;
	
	public String getTag() {
		return tag;
	}
	
	/** @pre | tag != null */
	public ElementNode(String tag) {
		this.tag = tag;
	}

}
