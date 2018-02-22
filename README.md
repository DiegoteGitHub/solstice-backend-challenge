Assumptions and instructions

1) Birthdate format is UNIX time (epoch).
2) Profile image is the URL where the physical image is stored.
3) H2 Database is used for this example.
4) The server runs on localhost and port 8080 as specified in application.properties.
5) Filters are: email, phoneNumber, state and city and must be sent as request parameters, all are optional
and if you add more than one the records must satisfy all of them. In the case of phoneNumber the application does
(phoneNumber = personalphone OR phoneNumber = workphone).
6) All contact fields are mandatory for creating or updating.
7) Birthdate cannot be current date or ahead of it.
8) To run the application you can import the eclipse maven project. Eclipse must have Sprint tool suite installed
in order to run the application within eclipse. After the import finished go to SolsticeBackendApplication.java right click
and select "Run as" => "Spring boot app". To run from command line type "mvn spring-boot:run" inside the project directory.
9) To run the tests go to "SolsticeBackendApplicationTests" right click and select "Run as" => "Junit test" (application must be
running).

Application endpoints

GET http://localhost:8080/api/contacts => Retrieves all contacts.

GET http://localhost:8080/api/contacts/{contact_id} => Retrieves contact with id "contact_id" if exists.

POST http://localhost:8080/api/contacts => Creates a new contact (The contact must be sent in the body as JSON object).

PUT http://localhost:8080/api/contacts/{contact_id} => Updates contact_id if exists (The contact must be sent in the body as JSON object).

DELETE http://localhost:8080/api/contacts/{contact_id} => Deletes contact_id if exists.