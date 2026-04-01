# Smart Clinic Management System Architecture

## Section 1: Architecture Summary

The Smart Clinic Management System is designed as a three-tier Spring Boot application with a clear separation between the presentation, application, and data layers. 

In the presentation layer, the system uses Thymeleaf-based MVC pages for the Admin and Doctor dashboards, while other modules such as appointments, patient dashboards, and patient records are accessed through REST APIs that return JSON data.
This mixed approach allows the application to support both server-rendered web pages and API-based communication for more flexible frontend integration. In the application layer, both MVC controllers and REST controllers handle incoming requests and pass them to a shared service layer where the main business logic is implemented. The service layer coordinates validation, scheduling rules, and data processing before calling the appropriate repositories.
In the data layer, MySQL is used for structured relational data such as Admin, Doctor, Patient, and Appointment entities through Spring Data JPA, while MongoDB is used for flexible prescription records through Spring Data MongoDB. This architecture makes the system scalable, maintainable, and suitable for future deployment with Docker and CI/CD pipelines.

## Section 2: Numbered Flow

1. The user interacts with the system either through Thymeleaf dashboard pages such as AdminDashboard and DoctorDashboard or through frontend/API modules such as Appointments, PatientDashboard, and PatientRecord.

2. The request is sent to the Spring Boot backend, where it is handled by the appropriate controller. Thymeleaf controllers manage requests for server-rendered HTML pages, while REST controllers handle JSON-based API requests.

3. The controller forwards the request to the service layer, which contains the main business logic of the application.

4. The service layer processes validations and business rules, then calls the correct repository depending on the type of data being requested or stored.

5. For structured application data such as patients, doctors, admins, and appointments, the MySQL repositories communicate with the MySQL database using Spring Data JPA.

6. For flexible prescription data, the MongoDB repository communicates with the MongoDB database using Spring Data MongoDB.

7. The retrieved or updated data is mapped to application models and returned back through the controller, either as rendered HTML in a Thymeleaf view or as a JSON response for REST API clients.
