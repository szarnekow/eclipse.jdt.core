package networks.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Set;

import org.junit.jupiter.api.Test;

import networks.Node;

class NodesTest {

	@Test
	void test() {
		Node node1 = new Node();
		assertEquals(Set.of(), node1.getNeighbors());
		
		Node node2 = new Node();
		node1.linkTo(node2);
		assertEquals(Set.of(node2), node1.getNeighbors());
		assertEquals(Set.of(node1), node2.getNeighbors());
		
		Node node3 = new Node();
		node1.linkTo(node3);
		assertEquals(Set.of(node2, node3), node1.getNeighbors());
		assertEquals(Set.of(node1), node2.getNeighbors());
		assertEquals(Set.of(node1), node3.getNeighbors());
		
		node2.unlinkFrom(node1);
		assertEquals(Set.of(node3), node1.getNeighbors());
		assertEquals(Set.of(), node2.getNeighbors());
		assertEquals(Set.of(node1), node3.getNeighbors());
	}

}
