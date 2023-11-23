Informatics Large Practical - PizzaDronz
PizzaDronz offers a innovative solution to students facing sustenance challenges during intense coding sessions. The proposed system implements an algorithm to control the flight of a drone, ensuring efficient deliveries while adhering to constraints on drone movement. This system seamlessly integrates with an online platform developed by the School of Informatics, allowing for the tracking of pizza orders. By addressing the nuanced process of pizza ordering and delivery, PizzaDronz enhances the overall experience and well-being of students.

Overview
This coursework involves the creation of a runnable application, which can be started using the following command:

bash
Copy code
java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net

Application Features

Read Orders: Fetch orders for the specified day, including restaurant information and other relevant data, from the REST-Server whose URL is provided as a parameter.

Validate Orders: Ensure the validity of the received orders.

Calculate Flightpaths: Determine the optimal flightpaths for all valid orders in the exact sequence they were received.

Result Files: Write the three result files in a folder named resultfiles (create the folder if it does not exist).

Usage
Clone the repository:

bash
Copy code
git clone https://github.com/your-username/PizzaDronz.git
Navigate to the project directory:

bash
Copy code
cd PizzaDronz
Run the application with the specified command:

bash
Copy code
java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net
Note
Orders retrieved for a day will only contain data for that specific day, as the REST-Server delivers filtered data when queried by date.

Ensure that the resultfiles folder exists to store the generated result files.
