# Controller Layer Transcript


video is [here](https://simon-java-training2026.s3.us-east-2.amazonaws.com/ControllerDesignVideo.MP4)

```java
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/employees")// + URL on method
@Tag(name = "Employee API", description = "Employee Management REST APIs")
public class EmployeeController2 {
    private EmployeeService employeeService;
    public void setEmployeeService(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

```

Now I am going to explain my EmployeeController.

First, I use the @Slf4j annotation from Lombok. This annotation automatically creates a logger object for the class, so I can write log messages without manually creating a Logger instance. It is useful for monitoring application behavior and troubleshooting issues in production.

Next, I use the @Validated annotation. This annotation enables validation for method parameters such as @PathVariable and @RequestParam. It works together with validation annotations like @Min, @Max, and @NotNull to ensure that incoming requests contain valid data.

Then I use @RequiredArgsConstructor. This Lombok annotation automatically generates a constructor for all required fields. It helps implement constructor-based dependency injection and reduces boilerplate code.

Next is @RestController. This is one of the most important Spring MVC annotations. It combines @Controller and @ResponseBody. It tells Spring that this class handles REST API requests and returns JSON data directly to the client.

After that, I use @RequestMapping with the path “/api/v1/employees”. This defines the base URL for all endpoints inside this controller. API versioning is also included in the URL, which makes it easier to support future API versions.

I also added the @Tag annotation. This annotation is used by Swagger or OpenAPI documentation tools. It groups related APIs together and provides a description, making the generated API documentation easier to read.

Inside the class, I declare an EmployeeService object. The controller layer is responsible for handling HTTP requests, while the service layer contains business logic. Therefore, the controller delegates operations to the EmployeeService.

I also provide a setter method for EmployeeService. This allows dependency injection through setter injection, although in most modern Spring Boot applications constructor injection is usually preferred because it makes dependencies explicit and easier to test.

---

```java
//Build Add Employee REST API
    @Operation(summary = "Create a new employee")
    @PostMapping
    public ResponseEntity<EmployeeDto> createEmployee(@Valid
                                                      @RequestBody EmployeeDto employeeDto,
                                                      @RequestHeader(value = "User-Agent", required = false)
                                                      String userAgent) {
        log.info("Create request from: {}", userAgent);
        EmployeeDto savedEmployee = employeeService.createEmployee(employeeDto);
        return new ResponseEntity<>(savedEmployee, HttpStatus.CREATED);
    }
```

Next, I will explain the createEmployee method.

This method is used to create a new employee record. It is a REST API endpoint that handles HTTP POST requests.

First, I use the @Operation annotation. This annotation is used by Swagger or OpenAPI to generate API documentation. The summary clearly describes the purpose of this endpoint, which is creating a new employee.

Then I use the @PostMapping annotation. This tells Spring Boot that this method should handle HTTP POST requests. Since the class-level URL is “/api/v1/employees”, the complete endpoint URL is also “/api/v1/employees”.

The method accepts an EmployeeDto object as input. The @RequestBody annotation tells Spring to convert the JSON request body into an EmployeeDto object automatically.

I also use the @Valid annotation. This enables Bean Validation before the request reaches the business logic layer. If any validation rule defined in the EmployeeDto class is violated, Spring will return an error response automatically.

After receiving a valid request, the controller calls the createEmployee method in the service layer. The controller itself does not contain business logic. Its responsibility is to receive requests and delegate processing to the service layer.

The service layer creates the employee record and returns the saved EmployeeDto object.

Finally, I return a ResponseEntity containing the saved employee information and an HTTP status code of CREATED, which corresponds to status code 201. This status code indicates that a new resource has been successfully created in the system.

---

```java
//Build Get Employee REST API
    @Operation(summary = "Get employee by ID")
    @GetMapping("/{id}") // /api/employees/{id}"
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long employeeId,
                                                       @RequestHeader(value = "User-Agent", required = false)
                                                        String userAgent) {
        log.info("Request from: {}", userAgent);
        log.info("Fetching employee with id: {}", employeeId);
        EmployeeDto employeeDto = employeeService.getEmployeeById(employeeId);
        return ResponseEntity.ok(employeeDto);
    }

```

Next, I will explain the getEmployeeById method.

This method is used to retrieve a specific employee based on the employee ID. It is a REST API endpoint that handles HTTP GET requests.

First, I use the @Operation annotation to provide a description for the API documentation generated by Swagger. The summary indicates that this endpoint retrieves an employee by ID.

Then I use the @GetMapping annotation with the path “/{id}”. This means the endpoint accepts a dynamic path variable. Combined with the class-level mapping, the complete URL becomes “/api/v1/employees/{id}”.

The method parameter uses the @PathVariable annotation. This tells Spring Boot to extract the ID value from the URL and bind it to the employeeId parameter.

Before calling the service layer, I add a log statement using [log.info](http://log.info/). This records the employee ID being requested. Logging is useful for monitoring application behavior, troubleshooting issues, and auditing API requests in production environments.

Next, the controller calls the getEmployeeById method in the service layer. The service layer contains the business logic and retrieves the employee information from the database.

The returned EmployeeDto object contains the employee data requested by the client.

Finally, I return the result using ResponseEntity.ok(). This creates a response with HTTP status code 200 OK and includes the employee information in the response body.

The overall flow is: the client sends a GET request with an employee ID, Spring extracts the path variable, the controller delegates the request to the service layer, the service retrieves the employee data, and the controller returns the result with a successful HTTP response.

---

```java
//Build Search Employee REST API
    @Operation(summary = "Search employee by name")
    @GetMapping("/search")
    public ResponseEntity<String> searchEmployee(
            @RequestParam String name,@RequestHeader(value = "User-Agent", required = false) String userAgent) {
        log.info("Request of searching from: {}", userAgent);
        log.info("Searching employee with name: {}", name);
        return ResponseEntity.ok(
                "Searching employee: " + name);
    }
```

Next, I will explain the searchEmployee method.

This method demonstrates how to handle query parameters in a REST API. It is designed to search for employees based on a name provided by the client.

First, I use the @Operation annotation to document the endpoint in Swagger. The summary indicates that this API is used to search employees by name.

Then I use the @GetMapping annotation with the path “/search”. Combined with the class-level mapping, the complete endpoint URL becomes “/api/v1/employees/search”.

The method parameter uses the @RequestParam annotation. This tells Spring Boot to extract the value of the “name” parameter from the URL query string and bind it to the name variable.

For example, if the client sends the following request:

GET /api/v1/employees/search?name=John

Spring Boot will automatically assign the value “John” to the name parameter.

I also add a log statement using [log.info](http://log.info/) to record the search request. Logging helps track user activity and makes debugging easier in production environments.

Currently, this method is a demonstration endpoint, so it does not perform an actual database search. Instead, it simply returns a message containing the search keyword.

Finally, I return the result using ResponseEntity.ok(), which sends an HTTP 200 OK response back to the client.

In a real-world application, this endpoint would typically call the service layer and repository layer to search the database and return a list of matching employees instead of a simple text message.

---

```java
//Build Get All Employee REST API
    @Operation(summary = "Get all employees")
    @GetMapping
    public ResponseEntity<List<EmployeeDto>> getAllEmployees(@RequestHeader(value = "User-Agent", required = false)
                                                                 String userAgent) {
        log.info("Request of getting all from: {}", userAgent);
        log.info("Fetching all employees");
        List<EmployeeDto> employees = employeeService.getAllEmployees();
        return ResponseEntity.ok(employees);
    }
```

Next, I will explain the getAllEmployees method.

This method is used to retrieve all employee records from the system. It is a REST API endpoint that handles HTTP GET requests.

First, I use the @Operation annotation to provide documentation for Swagger. The summary indicates that this endpoint retrieves all employees.

Then I use the @GetMapping annotation without specifying an additional path. Since the class-level mapping is “/api/v1/employees”, this endpoint responds to GET requests sent directly to “/api/v1/employees”.

Before processing the request, I add a log statement using [log.info](http://log.info/). This records that a request has been made to retrieve all employee records. Logging is useful for monitoring API usage and troubleshooting issues.

Next, the controller calls the getAllEmployees method in the service layer. The controller is responsible for handling HTTP requests, while the service layer contains the business logic and coordinates data access.

The service layer retrieves all employee records and returns them as a List of EmployeeDto objects. Each EmployeeDto represents an employee and contains the data that will be returned to the client.

Finally, I use ResponseEntity.ok() to return the list of employees along with an HTTP 200 OK status code.

The overall flow is: the client sends a GET request, the controller receives the request, delegates it to the service layer, the service retrieves employee data from the database, and the controller returns the list of employees in the response body.

In a real-world application, when the number of records becomes very large, this endpoint would typically be enhanced with pagination and sorting to improve performance and reduce the amount of data returned in a single request.

---

```java
//Build Update Employee REST API
    @Operation(summary = "Update employee by id")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeDto> updateEmployee(@PathVariable Long employeeId,
                                                      @RequestBody EmployeeDto updatedEmployeeDto
                                                        ,@RequestHeader(value = "User-Agent", required = false)
                                                      String userAgent) {
        log.info("Request updating from: {}", userAgent);
        log.info("Updating employee with id: {}", employeeId);
        EmployeeDto employeeDto = employeeService.updateEmployee(employeeId, updatedEmployeeDto);
        return ResponseEntity.ok(employeeDto);
    }

```

Next, I will explain the updateEmployee method.

This method is used to update an existing employee record. It is a REST API endpoint that handles HTTP PUT requests.

First, I use the @Operation annotation to document the endpoint in Swagger. The summary indicates that this API updates an employee by ID.

Then I use the @PutMapping annotation with the path “/{id}”. Combined with the class-level mapping, the complete endpoint URL becomes “/api/v1/employees/{id}”.

The method accepts two parameters. The first parameter uses the @PathVariable annotation. Spring Boot extracts the employee ID from the URL and binds it to the employeeId variable. This ID identifies which employee record should be updated.

The second parameter uses the @RequestBody annotation. Spring Boot automatically converts the JSON request body into an EmployeeDto object. This object contains the updated employee information provided by the client.

Before calling the service layer, I add a log statement using [log.info](http://log.info/) to record which employee is being updated. Logging is useful for monitoring API activity and troubleshooting issues.

Next, the controller delegates the update request to the service layer by calling the updateEmployee method. The service layer contains the business logic and performs the actual update operation.

The service layer updates the employee record in the database and returns the updated EmployeeDto object.

Finally, I return the updated employee information using ResponseEntity.ok(). This sends an HTTP 200 OK response back to the client, indicating that the update operation was completed successfully.

The overall flow is: the client sends a PUT request containing an employee ID and updated employee information, Spring Boot extracts the path variable and request body, the controller delegates the request to the service layer, the service updates the database record, and the controller returns the updated employee data in the response.

---

```java
 //Build Delete Employee REST API
    @Operation(summary = "Delete employee by id")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployee(@PathVariable("id") Long employeeId,
                                                 @RequestHeader(value = "User-Agent", required = false)
                                                String userAgent) {
        log.info("Request deleting from: {}", userAgent);
        log.info("Deleting employee with id: {}", employeeId);
        employeeService.deleteEmployee(employeeId);
        log.info("Employee deleted successfully. id={}", employeeId);
        return ResponseEntity.ok("Employee deleted successfully");
    }
}
```

Next, I will explain the deleteEmployee method.

This method is used to delete an employee record from the system. It is a REST API endpoint that handles HTTP DELETE requests.

First, I use the @Operation annotation to document the endpoint in Swagger. The summary indicates that this API deletes an employee by ID.

Then I use the @DeleteMapping annotation with the path “/{id}”. Combined with the class-level mapping, the complete endpoint URL becomes “/api/v1/employees/{id}”.

The method parameter uses the @PathVariable annotation. Spring Boot extracts the employee ID from the URL and binds it to the employeeId variable. This ID identifies which employee record should be deleted.

Before performing the delete operation, I add a log statement using [log.info](http://log.info/) to record the employee ID being deleted. Logging helps monitor system activity and provides useful information for debugging and auditing.

Next, the controller delegates the request to the service layer by calling the deleteEmployee method. The service layer contains the business logic and is responsible for deleting the employee record from the database.

After the deletion is completed, I add another log statement to confirm that the operation was successful.

Finally, I return a success message using ResponseEntity.ok(). This sends an HTTP 200 OK response back to the client along with a confirmation message indicating that the employee was deleted successfully.

The overall flow is: the client sends a DELETE request with an employee ID, Spring Boot extracts the path variable, the controller delegates the request to the service layer, the service removes the employee record from the database, and the controller returns a success response.

It is worth noting that DELETE is an idempotent HTTP method. This means that executing the same delete request multiple times should produce the same final result, which is that the employee record no longer exists in the system.
