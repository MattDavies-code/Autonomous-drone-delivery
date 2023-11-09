package uk.ac.ed.inf.model;

import uk.ac.ed.inf.ilp.data.LngLat;

/**
 * Model for a single move from one node to another
 * Used for recording the drone flight path move by move to be used in flightpath json file
 */
public class Move {

    String orderNo;
    double fromLongitude;
    double fromLatitude;
    int angle;
    double toLongitude;
    double toLatitude;

    /**
     * Constructor for Move
     *
     * @param orderNo order number
     * @param fromLongitude start longitude
     * @param fromLatitude start latitude
     * @param angle current angle
     * @param toLongitude end longitude
     * @param toLatitude end latitude
     */
    public Move(String orderNo, double fromLongitude, double fromLatitude, int angle, double toLongitude, double toLatitude){
        this.orderNo = orderNo;
        this.fromLongitude = fromLongitude;
        this.fromLatitude = fromLatitude;
        this.angle = angle;
        this.toLongitude = toLongitude;
        this.toLatitude = toLatitude;

    }
}
