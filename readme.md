You'll need the IntelliJ Lombok plugin installed.

You'll also need to do: 
https://github.com/mplushnikov/lombok-intellij-plugin

REQUIRES MAVEN 3.3.9 - NOT KIDDING!

run with:
 
 `mvn lagom:runAll`
 
 Then hit the following url to get data:
 (Note the port number may be dynamically generated so check the log for the port that the service is listening for requests on).
 http://127.0.0.1:61407/api/vehicle
 
 Adding a vehicle
 ---
 POST
 http://127.0.0.1:61407/api/vehicle/
 Headers: Content-Type: application/json
 Content:
 {"chassisNumber":"1234","make":"Bussy", "model":"McBus", "colour":"Red", "capacity":22}
 
 Return: <UUID of the newly created record>
 
 Retrieve a vehicle
 ---
 GET
 http://127.0.0.1:61407/api/vehicle/<UUID>
 Headers: Content-Type: application/json
 Content:
 None
  
 Return: JSON of vehicle record, for example:
 
 { "chassisNumber": "1234", "make": "Bussy", "model": "McBus", "colour": "Red", "capacity": 22}
 
 Update a vehicle
 ---
 PUT
 http://127.0.0.1:61407/api/vehicle/<UUID>
  Headers: Content-Type: application/json
  Content:
  {"chassisNumber":"1234","make":"Bussy", "model":"McBus", "colour":"Blue", "capacity":22}
  
  Return: 'Done: true'response.
  
  
  
  
  Adding a Position
  ---
  To add a position for a vehicle, we use the position service.
  
  POST
  http://127.0.0.1:64580/api/position/live/<vehicle-id>   (last component is a previously created vehicle id)
  Headers: Content-Type: application/json
    Content:
    {"chassisNumber":"1234","latitude":"22.5", "longitude":"33.5", "speed":55, "headingDegrees":90, "timestamp":"2014-12-03T10:15:30.00Z"}
    
 
 Connecting to Cassandra
 ---
 Depending on what version of CQLSH you have running, you may need to force the protocol version when you connect
 to the Lagom Cassandra development instance.  This can be done using:
 
 `cqlsh 127.0.0.1 4000  --cqlversion="3.3.1"`
  
  To show keyspaces use: `describe keyspaces;`
  To show tables use: 
 
    `use <keyspace>;`
    `describe tables;`

 Generating the Project
 ---
 To generate the project from the command line:
  mvn archetype:generate -DarchetypeGroupId=com.lightbend.lagom -DarchetypeArtifactId=maven-archetype-lagom-java -DarchetypeVersion=1.2.0