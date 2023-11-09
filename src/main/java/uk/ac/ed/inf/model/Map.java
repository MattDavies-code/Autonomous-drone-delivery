package uk.ac.ed.inf.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ed.inf.controller.RestController;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;

import java.util.List;

/**
 * Model of the map for the drone to utilise
 */
@Component
public class Map {

//    // constants
//    // This is the LngLat of Appleton Tower
//    private LngLat startPosition = new LngLat(-3.186874, 55.944494);
//
//
//    // create instance of rest controller
//    @Autowired
//    private RestController restController;
//    List<Order> orders = restController.fetchOrders();
//    List<NamedRegion> noFlyZones = restController.fetchNoFlyZones();
//    NamedRegion centralArea = restController.fetchCentralArea();
//
//    /**
//     * Constructor for the map
//     *
//     * @throws JsonProcessingException
//     */
//    public Map() throws JsonProcessingException {
//        this.noFlyZones = noFlyZones;
//        this.centralArea = centralArea;
//        this.startPosition = startPosition;
//        createMap();
//
//    }
//
//    /**
//     *  Method to add no-fly zones and central area to the map using GeoJson
//     */
//    public void createMap() {
//        // add no-fly zones to map
//        for (NamedRegion noFlyZone : noFlyZones) {
//            // add no-fly zone to map
//
//        }
//
//
//    }

}
