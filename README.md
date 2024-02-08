
**Overview**

The proposed system implements an algorithm to control the flight of a drone, ensuring efficient deliveries while adhering to constraints on drone movement.
The runnable application is started using the following command:

java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net

**Application Features**

Read Orders: Fetch orders for the specified day, including restaurant information and other relevant data, from the REST-Server whose URL is provided as a parameter.

Validate Orders: Ensure the validity of the received orders.

Calculate Flightpaths: Determine the optimal flightpaths for all valid orders in the exact sequence they were received.

Result Files: Write the three result files in a folder named resultfiles (create the folder if it does not exist).

**Usage**

Clone the repository:

Navigate to the project directory:

Run the application with the specified command:

java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net

**Note**

Orders retrieved for a day will only contain data for that specific day, as the REST-Server delivers filtered data when queried by date.
