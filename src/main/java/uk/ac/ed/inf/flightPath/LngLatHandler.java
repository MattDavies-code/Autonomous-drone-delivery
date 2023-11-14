package uk.ac.ed.inf.flightPath;

import uk.ac.ed.inf.ilp.constant.SystemConstants;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;

public class LngLatHandler implements LngLatHandling {
    /**
     * get the distance between two positions
     * @param startPosition is where the start is
     * @param endPosition is where the end is
     * @return the euclidean distance between the positions
     */
    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition){
        double changeInX = endPosition.lng() - startPosition.lng();
        double changeInY = endPosition.lat() - startPosition.lat();
        return Math.sqrt((Math.pow(changeInX, 2)) + (Math.pow(changeInY, 2)));
    }
    /**
     * check if two positions are close (< than SystemConstants.DRONE_IS_CLOSE_DISTANCE)
     * @param startPosition is the starting position
     * @param otherPosition is the position to check
     * @return if the positions are close
     */
    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        double distance = distanceTo(startPosition, otherPosition);
        return distance < SystemConstants.DRONE_IS_CLOSE_DISTANCE; //changed to < according to Spec as it is a strict inequality
    }

    /**
     * check if the @position is in the @region (includes the border)
     * @param position to check
     * @param region as a closed polygon
     * @return if the position is inside the region (including the border)
     */
    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
        LngLat[] vertices = region.vertices();
        // Ray Casting Algorithm
        // Casts a ray from the point towards any direction and counting the number of times the ray intersects with the edges of the polygon.
        // If the number of intersections is odd the point is inside the polygon
        int numOfIntersections = 0;
        for (int i = 0; i < vertices.length; i++) {
            LngLat vertex1 = vertices[i];
            // Will account for the edge connecting the last and first vertex
            LngLat vertex2 = vertices[(i + 1) % vertices.length];

            // Check if the ray from the point intersects with the edge through the two conditions
            boolean condition1 = vertex1.lat() > position.lat() != vertex2.lat() > position.lat();
            boolean condition2 = position.lng() < (vertex2.lng() - vertex1.lng()) * (position.lat() - vertex1.lat()) /
                    (vertex2.lat() - vertex1.lat()) + vertex1.lng();
            if (condition1 && condition2) {
                numOfIntersections++;
            }
        }
        // If the count is odd the point is inside the polygon
        return numOfIntersections % 2 == 1;
    }

    /**
     * find the next position if an @angle is applied to a @startPosition
     * @param startPosition is where the start is
     * @param angle is the angle to use in degrees
     * @return the new position after the angle is used
     */
    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        // Tells the drone to hover in place when provided the special value
        if (angle == 999) {return startPosition;}

        double angleInRadians = Math.toRadians(angle);
        double newLng = startPosition.lng() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.cos(angleInRadians));
        double newLat = startPosition.lat() + (SystemConstants.DRONE_MOVE_DISTANCE * Math.sin(angleInRadians));
        return new LngLat(newLng, newLat);
    }
}
