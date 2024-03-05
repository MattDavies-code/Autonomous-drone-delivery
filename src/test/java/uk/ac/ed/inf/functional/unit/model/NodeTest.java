package uk.ac.ed.inf.functional.unit.model;

import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.model.Node;

import static org.junit.Assert.*;

public class NodeTest {

    @Test
    public void testEquals() {
        LngLat position1 = new LngLat(1.0, 2.0);
        LngLat position2 = new LngLat(1.0, 2.0);
        LngLat position3 = new LngLat(3.0, 4.0);

        Node node1 = new Node(position1);
        Node node2 = new Node(position2);
        Node node3 = new Node(position3);

        assertEquals(node1, node2);
        assertNotEquals(node1, node3);
    }

    @Test
    public void testHashCode() {
        LngLat position1 = new LngLat(1.0, 2.0);
        LngLat position2 = new LngLat(1.0, 2.0);
        LngLat position3 = new LngLat(3.0, 4.0);

        Node node1 = new Node(position1);
        Node node2 = new Node(position2);
        Node node3 = new Node(position3);

        assertEquals(node1.hashCode(), node2.hashCode());
        assertNotEquals(node1.hashCode(), node3.hashCode());
    }

    @Test
    public void testCompareTo() {
        LngLat position1 = new LngLat(1.0, 2.0);
        LngLat position2 = new LngLat(3.0, 4.0);

        Node node1 = new Node(position1);
        Node node2 = new Node(position2);

        node1.setF(3.0);
        node2.setF(5.0);

        assertTrue(node1.compareTo(node2) < 0);
        assertTrue(node2.compareTo(node1) > 0);

        node2.setF(3.0);

        assertEquals(0, node1.compareTo(node2));
    }
}
