package uk.ac.ed.inf;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.inf.cw1.LngLatHandler;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

/**
 * Unit test for LngLatHandler.
 */
public class LngLatHandlerTest {

    @Test
    public void testDistanceTo(){
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat startPosition = new LngLat(0.0, 0.0);
        LngLat endPosition = new LngLat(3.0, 4.0);

        double expectedDistance = 5.0;

        double actualDistance = lngLatHandler.distanceTo(startPosition, endPosition);

        // Check if the calculated distance matches the expected distance
        assertEquals(expectedDistance, actualDistance, 0.0001);
    }

    @Test
    public void testIsCloseTo() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        LngLat startPosition = new LngLat(0.0, 0.0);

        // Check if another position within the close distance
        LngLat closePosition = new LngLat(0.0, 0.0);
        assertTrue(lngLatHandler.isCloseTo(startPosition, closePosition));

        // Check if another position outside the close distance
        LngLat farPosition = new LngLat(10.0, 10.0);
        assertFalse(lngLatHandler.isCloseTo(startPosition, farPosition));
    }

    @Test
    public void testIsInRegion() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Create a region as a square with vertices
        LngLat[] squareVertices = {
                new LngLat(0.0, 0.0),
                new LngLat(1.0, 0.0),
                new LngLat(1.0, 1.0),
                new LngLat(0.0, 1.0)
        };
        NamedRegion squareRegion = new NamedRegion("SquareRegion", squareVertices);

        // Check if point inside the square region
        LngLat insidePoint = new LngLat(0.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(insidePoint, squareRegion));

        // Check if point outside the square region
        LngLat outsidePoint = new LngLat(2.0, 2.0);
        assertFalse(lngLatHandler.isInRegion(outsidePoint, squareRegion));

        // Check if point on the border of the square region
        LngLat borderPoint = new LngLat(0.0, 0.0);
        assertTrue(lngLatHandler.isInRegion(borderPoint, squareRegion));
    }

    @Test
    public void testNextPosition() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Test moving east (0 degrees)
        LngLat startPositionEast = new LngLat(0.0, 0.0);
        LngLat expectedPositionEast = new LngLat(SystemConstants.DRONE_MOVE_DISTANCE, 0.0);
        assertEquals(expectedPositionEast, lngLatHandler.nextPosition(startPositionEast, 0.0));

        // Test moving north (90 degrees)
        LngLat startPositionNorth = new LngLat(0.0, 0.0);
        LngLat expectedPositionNorth = new LngLat(9.184850993605148E-21, SystemConstants.DRONE_MOVE_DISTANCE);
        assertEquals(expectedPositionNorth, lngLatHandler.nextPosition(startPositionNorth, 90.0));
    }
}
