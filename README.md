# Ecommerce Platform

A Spring Boot-based Ecommerce Platform with authentication, product management, cart, orders, and payments.  
Built with **Spring Boot**, **Spring Security (JWT)**, **MySQL**, and **Lombok**.

---

## **Features**

### Authentication & Authorization
- Role-based access control (Admin/User)
- Register & Login with JWT authentication
- Access protected endpoints based on roles

### User Management
- Fetch current user profile
- Admin can fetch all users or specific users

### Product Management
- Admins can create, update, delete products
- All users can view products

### Cart Management
- Add/remove products to cart
- Update product quantity in cart
- Fetch current user’s cart

### Orders
- Place orders from cart
- Fetch all orders by user
- Checkout with payment mode

---

## **Tech Stack**

- **Backend:** Spring Boot, Spring MVC, Spring Data JPA, Spring Security
- **Database:** MySQL
- **Build Tool:** Maven
- **Testing:** JUnit 5, Mockito, MockMvc
- **Logging:** SLF4J with Logback
- **Other:** Lombok for reducing boilerplate

---

## **Project Structure**

src/main/java/com/ecommerce
├── controller/ # REST Controllers (Auth, Product, Cart, Order, User)
├── entity/ # JPA Entities (User, Product, Cart, Order, CartItem)
├── service/ # Service layer (business logic)
├── repository/ # Spring Data JPA Repositories
├── config/ # Security configuration
├── exception/ # Custom exception handlers
└── EcommerceApplication.java # Main entry point

yaml
Copy code

---

## **Setup Instructions**

1. Clone the repository:

```bash
git clone https://github.com/<your-username>/<your-repo>.git
cd <your-repo>
Build the project:

bash
Copy code
mvn clean install
Run the application:

bash
Copy code
mvn spring-boot:run
The application runs at: http://localhost:8080

Authentication
Register: POST /api/auth/register

Login: POST /api/auth/login → returns JWT token

Use the token for all protected endpoints:

makefile
Copy code
Authorization: Bearer <your_jwt_token>
API Endpoints
Users
GET /api/users/me – Get current logged-in user

GET /api/users – Admin only: get all users

GET /api/users/{id} – Admin only: get specific user

Products
POST /api/products – Create (Admin)

PUT /api/products/{id} – Update (Admin)

DELETE /api/products/{id} – Delete (Admin)

GET /api/products – List all products

GET /api/products/{id} – Get product by ID

Cart
GET /api/cart – Get current cart

POST /api/cart/add/{productId}?quantity=1 – Add product to cart

PUT /api/cart/update/{cartItemId}?quantity=2 – Update quantity

DELETE /api/cart/remove/{cartItemId} – Remove product

DELETE /api/cart/clear – Clear cart

Orders
POST /api/orders/checkout?paymentMode=CARD – Place an order

GET /api/orders – Get all orders of current user

GET /api/orders/{id} – Get order by ID

Testing
Run all tests:

bash
Copy code
mvn test
We use:

JUnit 5 for unit and integration tests

Mockito for mocking services

MockMvc for controller tests

spring-security-test for authentication/authorization

Logging
Logs user registration and login

Logs cart updates and order placements

Logs admin actions on products