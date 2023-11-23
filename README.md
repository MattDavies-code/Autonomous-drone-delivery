**Informatics Large Practical
**
"PizzaDronz offers a solution to students grappling with sustenance challenges during intense coding sessions. The system being proposed will implement an algorithm to control the flight of the drone as it makes its deliveries while respecting the constraints on drone movement and integrating with an online system developed by the School of Informatics, that will track the placement and tracking of pizza orders. By addressing the nuanced process of pizza ordering and delivery, PizzaDronz enhances the overall experience and well-being of students."

In general, This cooursework is about creating a runnable application, which can be started like:java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net

The Application:
  • Read orders for the specified day (and only the day) and restaurants plus otherrelevant data from the REST-Server whose URL is passed in (see the spec)o Orders retrieved for a day will only contain data for that day as the REST-Serveronly delivers filtered data if queried by date. 
  • Validate orders
  • Calculate the flightpaths for all valid orders in the exact sequence you received them. 
  • Write the 3 result files in a folder resultfiles (create if not exists)
