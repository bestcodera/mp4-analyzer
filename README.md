# Reactive MP4 Analyzer

This Spring Boot application analyzes MP4 files and provides information about the MP4 boxes contained within them. It uses Netty and Project Reactor for non-blocking IO.

## How to Build and Run the Application

### Prerequisites

Before you begin, ensure you have met the following requirements:

- [Java](https://www.java.com/) (version 21)
- [Spring Boot](https://spring.io/projects/spring-boot) (version 3.1.4)

To build and run the application, follow these steps:

1. Clone the repository:

    ```shell
    git clone https://github.com/bestcodera/mp4-analyzer.git
    ```
   
2. Build the project using Maven:

    ```bash
    mvn clean install
    ```

3. Run the Spring Boot application:

    ```shell
    mvn spring-boot:run
    ```

The application should now be running on `http://localhost:8080`.

### How to Make a Sample Request with curl
Use the following curl command to make a sample request to analyze the test MP4 file:

```shell
curl -X GET "http://localhost:8080/analyze?url=https://demo.castlabs.com/tmp/text0.mp4"
```
For other files replace `https://demo.castlabs.com/tmp/text0.mp4` with the URL of the MP4 file you want to analyze.

## API Endpoint
The application exposes a single endpoint for MP4 analysis:

#### Endpoint: /analyze
+ **Method:** GET
+ **Query Parameter:**
url (required): The URL of the MP4 file to analyze.
The response will contain information about the MP4 boxes in the specified file in JSON format.

Example response:
````
[
    {
     "size": 181,
     "type": "moof",
     "subBoxes": [
                    {
                     "size": 16,
                     "type": "mfhd",
                     "subBoxes": []
                    },
                    {
                     "size": 157,
                     "type": "traf",
                     "subBoxes": [
                                    {
                                     "size": 24,
                                     "type": "tfhd",
                                     "subBoxes": []
                                    },
                                    {
                                     "size": 20,
                                     "type": "trun",
                                     "subBoxes": []
                                    },
                                    {
                                     "size": 44,
                                     "type": "uuid",
                                     "subBoxes": []
                                    },
                                    {
                                     "size": 61,
                                     "type": "uuid",
                                     "subBoxes": []
                                    }
                                 ]
                    }
                ]
    },
    {
     "size": 17908,
     "type": "mdat",
     "subBoxes": []
    }
]
````