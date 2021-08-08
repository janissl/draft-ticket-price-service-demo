draft-ticket-price-service-demo
===============================

A simple Web service example.

Calculates prices for each ticket as well as a total price of a draft ticket.
Uses external Web services for getting a base price for destination and applicable tax rates.
A demo version of such services can be found at [web-api-demo](https://github.com/janissl/web-api-demo).


Requirements:
-------------
* OpenJDK 8+
* Spring Boot
* Gradle


Build
-----

Execute:

On Windows:

`.\gradlew build`

On UNIX:

`./gradlew build`

Usage
-----
1. Start external Web services for getting base prices and applicable taxes.
    URLs of external services as well as a server port of this application has been defined in __application.properties__.
   
1. _cd_ to the directory of the JAR file and start the __Draft Ticket Price Service__.
   
   On Windows:
   ```powershell
    cd ${PROJECT_ROOT}\build\libs
    java -jar .\draft-ticket-price-service-x.y.z[-SNAPSHOT].jar
   ```
   
   On UNIX:
   ```powershell
    cd ${PROJECT_ROOT}/build/libs
    java -jar draft-ticket-price-service-x.y.z[-SNAPSHOT].jar
   ```
  
1. Input data are received from a web client by a GET request with `http://localhost:9090/draftprice` as a URL and
   a list of JSON objects in the Message Body.
    
   An example in Python:
      ```python
        #!/usr/bin/env python3
        
        import sys
        import requests
        import json
        
        
        def main():
            url = 'http://localhost:9090/draftprice'
            data =
            [
                {'passengerType': 'ADULT', 'destination': 'Vilnius', 'itemsOfLuggage': 2},
                {'passengerType': 'CHILD', 'destination': 'Vilnius', 'itemsOfLuggage': 1}
            ]
            headers = {'Content-Type': 'application/json'}
            
            resp = requests.get(url, headers=headers, data=json.dumps(data))
            print(resp.content.decode('utf-8'))
        
        
        if __name__ == '__main__':
            sys.exit(main()) or 0
      ```

1. A response to the Web client is also sent in JSON format.
   
    An example of a response content received by the Web client:
    ```json
    {
      "tickets":[
        {
          "ticketType":  "ADULT",
          "count": 1,
          "price": 12.10
        }, {
          "ticketType": "LUGGAGE",
          "count": 2,
          "price": 7.26
        }, {
          "ticketType": "CHILD",
          "count": 1,
          "price": 6.05
        }, {
          "ticketType": "LUGGAGE",
          "count": 1,
          "price": 3.63
        }
      ],
      "totalPrice": 29.04
   }
    ```
