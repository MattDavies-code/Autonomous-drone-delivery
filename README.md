# Autonomous Drone Delivery
The proposed system implements an algorithm to control the flight of a drone, ensuring efficient deliveries while adhering to constraints on drone movement.

Run by: java -jar PizzaDronz-1.0-SNAPSHOT.jar 2023-11-15 https://ilp-rest.azurewebsites.net

### Table of Contents

1. [Project Motivation](#motivation)
2. [File Descriptions](#files)
3. [Testing](#testing)
4. [Results](#results)
5. [Licensing, Authors, and Acknowledgements](#licensing)

## Project Motivation<a name="motivation"></a>
This Project was created for my Informatics Large Practical Module. I achieved a mark of 87%.

## File Descriptions <a name="files"></a>

RestController.java: Fetches data from the Rest Server.  
FlightPaths.java: Calculates flightpaths for all orders in a day.  
LngLatHandler.java: Handles LngLat objects in the PathFinder class.  
PathFinder.java: Calculates the flight path for a single order using the A* algorithm.  
Main.java: Handles input and runs the application.  
Move.java:  Model for a single move from one node to another. Used for recording the drone flight path move by move to be used in flightpath json file.  
Node.java: Model of a node. Utilised in A* pathfinding algorithm
CreateFiles.java: Creates a directory for the result files and writes the result files to the directory. Handles conversion of JSON
SimplifiedOrder.java: Model of simplified order used for creating files
ArguementValidator.java: Validates arguements given to the application.
OrderValidator.java: Validates orders retrieved to fit standard.

![image](https://github.com/MattDavies-code/Autonomous-drone-delivery/assets/54101905/6d406a40-be8c-471c-be01-f773d6714a00)

## Testing <a name="testing">

Tests are found in the /src/tests.  

Tests include: 
Stress tests, 
Unit tests, 
Integration tests, 
System tests, 
Timing tests, 
Scenario tests, 

## Results<a name="results"></a>

Example results for the orders placed on 2023-11-15: 
![AutoDrone](https://github.com/MattDavies-code/Autonomous-drone-delivery/assets/54101905/b509fb45-f134-4345-bdc8-8c5b8e0edf3b)


## Licensing, Authors, Acknowledgements<a name="licensing"></a>
Matthew Davies

**Note**

Orders retrieved for a day will only contain data for that specific day, as the REST-Server delivers filtered data when queried by date.
