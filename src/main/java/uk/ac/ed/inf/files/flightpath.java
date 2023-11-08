package uk.ac.ed.inf.files;

/**
 * The second file (flightpath-YYYY-MM-DD.json) records the flightpath of the drone move-by-move
 */
public class flightpath {
    /*
    import com.fasterxml.jackson.databind.ObjectMapper;

    // Create a list to hold flightpath records
    List<Map<String, Object>> flightpathRecords = new ArrayList<>();

    // Calculate and construct JSON objects for each move
    for (Move move : calculatedFlightpaths) {
        Map<String, Object> flightpathRecord = new HashMap<>();
        flightpathRecord.put("orderNo", move.getOrderNo());
        flightpathRecord.put("fromLongitude", move.getFromLongitude());
        flightpathRecord.put("fromLatitude", move.getFromLatitude());
        flightpathRecord.put("angle", move.getAngle());
        flightpathRecord.put("toLongitude", move.getToLongitude());
        flightpathRecord.put("toLatitude", move.getToLatitude());
        flightpathRecords.add(flightpathRecord);
    }

    // Serialize the list of flightpath records to a JSON array
    ObjectMapper objectMapper = new ObjectMapper();
    String flightpathRecordsJson = objectMapper.writeValueAsString(flightpathRecords);

    // Write the JSON array to a file
    try (FileWriter fileWriter = new FileWriter("flightpath-YYYY-MM-DD.json")) {
        fileWriter.write(flightpathRecordsJson);
    }

     */
}
