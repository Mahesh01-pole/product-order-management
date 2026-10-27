Product & Order Management System

This is a simple Product and Order Management System built using Java and Spring Boot. The project allows users to browse products, manage their shopping cart, and place orders. Admins can manage products, update prices and stock, and enable or disable products.

I built this project to practise backend development, REST APIs, database integration, authentication, and basic role-based access control.

The project also includes a simple HTML, CSS, and JavaScript frontend, so it can be used directly in a browser without installing any frontend framework.

Technologies Used

Java 17

Spring Boot 3.3

Spring Data JPA and Hibernate

MySQL (PostgreSQL can also be configured)

Maven

REST APIs

Spring Security with HTTP Basic authentication

HTML, CSS, and JavaScript

Features

Admin

Add new products.

Update product prices and available quantities.

Enable or disable products.

View products, including disabled products.

User

Register a new account.

View available products.

Add products to the cart.

Update or remove cart items.

Place orders using the items in the cart.

View previous orders and order details.

How the Application Works

The project follows a layered architecture to keep the code organised and easier to maintain.

Controller layer: Handles incoming API requests.

Service layer: Contains the main business logic.

Repository layer: Communicates with the database using Spring Data JPA.

Entity classes: Represent the database tables.

DTOs: Used to receive requests and return responses.

Exception handling: Handles errors and returns meaningful API responses.
Default Accounts and Sample Data

The application creates a default admin account when the initial data setup runs:

Email: admin@example.com

Password: admin123
Project Structure

src/main/java/com/example/pms
├── ProductOrderApplication.java
├── config/
├── controller/
├── dto/
├── entity/
├── exception/
├── mapper/
├── repository/
├── service/
└── util/

#Output
<img width="899" height="253" alt="image" src="https://github.com/user-attachments/assets/b682b98f-d2e9-4bc8-8139-c211b28d0b64" />
#Admin_page
<img width="955" height="455" alt="image" src="https://github.com/user-attachments/assets/6f9dfd3a-8699-4a22-b723-372359a2880d" />
#User page
<img width="952" height="412" alt="image" src="https://github.com/user-attachments/assets/14f5354d-b4d9-436d-bb39-5ab89fc14997" />
#addTo_cart
<img width="953" height="256" alt="image" src="https://github.com/user-attachments/assets/1d80005f-6ef5-49c2-8a1d-a8759b714292" />
#my_orders
<img width="959" height="419" alt="image" src="https://github.com/user-attachments/assets/f48a4da3-6e60-4eb6-8d19-428ec46be032" />
#ordering items more than in stock
<img width="950" height="465" alt="image" src="https://github.com/user-attachments/assets/9f7b923e-ef22-4f3e-a22f-b1f939b9f44f" />


