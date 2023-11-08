package uk.ac.ed.inf.files;

/**
 * The first file (deliveries-YYYY-MM-DD.json) records both the deliveries and non-deliveries made by the drone
 */
public class deliveries {
    /*
    import com.fasterxml.jackson.databind.ObjectMapper;

    // Create a list to hold delivery records
    List<Map<String, Object>> deliveryRecords = new ArrayList<>();

    // Process and construct JSON objects for each order
    for (Order order : validOrdersForTheDay) {
        Map<String, Object> deliveryRecord = new HashMap<>();
        deliveryRecord.put("orderNo", order.getOrderNo());
        deliveryRecord.put("orderStatus", order.getOrderStatus().toString());
        deliveryRecord.put("orderValidationCode", order.getOrderValidationCode().toString());
        deliveryRecord.put("costInPence", order.getCostInPence());
        deliveryRecords.add(deliveryRecord);
    }

    // Serialize the list of delivery records to a JSON array
    ObjectMapper objectMapper = new ObjectMapper();
    String deliveryRecordsJson = objectMapper.writeValueAsString(deliveryRecords);

// Write the JSON array to a file
try (FileWriter fileWriter = new FileWriter("deliveries-YYYY-MM-DD.json")) {
        fileWriter.write(deliveryRecordsJson);
    }
*/
}
