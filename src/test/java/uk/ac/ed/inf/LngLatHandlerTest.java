package uk.ac.ed.inf;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.*;

public class LngLatHandlerTest {

    @Test
    public void testDistanceTo(){
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Create LngLat instances for testing
        LngLat startPosition = new LngLat(0.0, 0.0);
        LngLat endPosition = new LngLat(3.0, 4.0);

        // Calculate the expected distance manually (using Pythagorean theorem)
        double expectedDistance = 5.0;

        // Calculate the distance using the LngLatHandler
        double actualDistance = lngLatHandler.distanceTo(startPosition, endPosition);

        // Check if the calculated distance matches the expected distance
        assertEquals(expectedDistance, actualDistance, 0.0001);
    }

    @Test
    public void testIsCloseTo() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Create LngLat instances for testing
        LngLat startPosition = new LngLat(0.0, 0.0);

        // Case 1: Another position within the close distance
        LngLat closePosition = new LngLat(0.0, 0.0);
        assertTrue(lngLatHandler.isCloseTo(startPosition, closePosition));

        // Case 2: Another position outside the close distance
        LngLat farPosition = new LngLat(10.0, 10.0);
        assertFalse(lngLatHandler.isCloseTo(startPosition, farPosition));
    }

    @Test
    public void testIsInCentralArea() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Create central area
        LngLat[] centralAreaVertices = {
                new LngLat(0.0, 1.0), // Top-left
                new LngLat(0.0, 0.0), // Bottom-left
                new LngLat(1.0, 0.0),  // Bottom-right
                new LngLat(1.0, 1.0) // Top-right
        };
        NamedRegion centralArea = new NamedRegion(SystemConstants.CENTRAL_REGION_NAME, centralAreaVertices);

        // Case 1: Point inside the central area
        LngLat insidePoint = new LngLat(0.2, 0.2);
        assertTrue(lngLatHandler.isInCentralArea(insidePoint, centralArea));

        // Case 2: Point outside the central area
        LngLat outsidePoint = new LngLat(2.0, 2.0);
        assertFalse(lngLatHandler.isInCentralArea(outsidePoint, centralArea));
    }

    @Test
    public void testIsInRegion() {
        LngLatHandler lngLatHandler = new LngLatHandler();

        // Create a region as a square with vertices at (0, 0), (1, 0), (1, 1), (0, 1)
        LngLat[] squareVertices = {
                new LngLat(0.0, 0.0),
                new LngLat(1.0, 0.0),
                new LngLat(1.0, 1.0),
                new LngLat(0.0, 1.0)
        };
        NamedRegion squareRegion = new NamedRegion("SquareRegion", squareVertices);

        // Case 1: Point inside the square region
        LngLat insidePoint = new LngLat(0.5, 0.5);
        assertTrue(lngLatHandler.isInRegion(insidePoint, squareRegion));

        // Case 2: Point outside the square region
        LngLat outsidePoint = new LngLat(2.0, 2.0);
        assertFalse(lngLatHandler.isInRegion(outsidePoint, squareRegion));

        // Case 3: Point on the border of the square region
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
