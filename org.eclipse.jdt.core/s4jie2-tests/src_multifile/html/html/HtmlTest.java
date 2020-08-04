package html;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class HtmlTest {

	@Test
	void test() {
		Node html = new ElementNode("html");
		Node head = new ElementNode("head");
		html.addChild(head);
		Node title = new ElementNode("title");
		head.addChild(title);
		Node titleText = new TextNode("JLearner");
		title.addChild(titleText);
		Node script = new ElementNode("script");
		head.addChild(script);
		//head.removeChild(script);
		script.remove();
		head.addChild(script);
		Node scriptText = new TextNode("alert('Hello world!')");
		script.addChild(scriptText);
		Node nonsense = new ElementNode("nonsense");
		head.addChild(nonsense);
		//head.removeChild(nonsense);
		nonsense.remove();
		
		assertEquals(
				"<html>" + 
				"<head>" + 
				"<title>JLearner</title>" + 
				"<script>alert('Hello world!')</script>" +
				"</head>" + 
				"</html>",
				html.toString());
	}

}
