Tour Agency API Documentation 
(!!tourism agency servers https://github.com/MehmetKARADANA/Api2.0 tourism agency
You should use the servers in the repository)
Overview 

This documentation provides details on how to interact with the endpoints available in the Tour Agency API. The API includes endpoints for fetching room data from various services. 

 

Base URLs 

Kotlin Service: http://localhost:8082 

Java Service: http://localhost:8081 

Go Service: http://localhost:8083 

 

Endpoints 

1. Get Rooms (Kotlin Service) 

URL: http://localhost:8082/api/getRooms 

Method: GET 

Example Request: 

http 

Kodu kopyala 

GET /api/getRooms HTTP/1.1 
Host: localhost:8082 
 

 

2. Get Rooms (Java Service) 

URL: http://localhost:8081/api/getRooms 

Method: GET 

Example Request: 

http 

Kodu kopyala 

GET /api/getRooms HTTP/1.1 
Host: localhost:8081 
 

 

3. Get Rooms (Go Service) 

URL: http://localhost:8083/api/getRooms 

Method: GET 

Example Request: 

http 

Kodu kopyala 

GET /api/getRooms HTTP/1.1 
Host: localhost:8083 
 

 

Notes 

For testing, tools like Postman or curl can be used to send requests and verify responses. 

All endpoints require appropriate services to be running locally. 
