package uk.ac.ed.inf.model;


import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.ArrayList;
import java.util.List;

/**
 * Model of the drone
 */
public class Drone {

        private LngLat startPosition;
        private LngLat currentPosition;
        private Map map;
        private List<Move> moves;
        private List<Order> ordersDelivered;

        public boolean orderCompleted;

        public Drone(LngLat startPosition, Map map){
                this.startPosition = startPosition;
                this.currentPosition = new LngLat(startPosition.lng(), startPosition.lat());
                this.map = map;
                this.moves = new ArrayList<>();
                this.ordersDelivered = new ArrayList<>();
                this.orderCompleted = false;
        }


}
