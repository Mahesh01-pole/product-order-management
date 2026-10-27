# Product & Order Management System (Round-1 Assignment)

Backend: **Java 17 + Spring Boot 3.3 + Spring Data JPA/Hibernate + MySQL (PostgreSQL optional) + REST APIs**
Basic frontend: single page at `http://localhost:8081/` (plain HTML/JS, no build step).

## Roles
| Role  | Can do |
|-------|--------|
| ADMIN | Add products, update price & quantity, enable/disable products |
| USER  | View products, add to cart, update/remove cart items, place order |

## Business rules (where they are enforced)
| Rule | Implementation |
|------|----------------|
| Inventory must be available | `CartService.ensureStock` (add/update cart) + `OrderService` (at checkout) |
| Order fails if quantity insufficient | `InsufficientStockException` -> HTTP 409 |
| Inventory reduces after successful order | `ProductRepository.decreaseStock` (atomic `UPDATE ... WHERE quantity >= qty`) |
| Order placement is transactional | `@Transactional` on `OrderService.placeOrder` - any failure rolls back stock, order and cart changes |

## Architecture
```
controller  ->  service  ->  repository  ->  DB
   |              |
  DTOs        entities (never exposed) + EntityMapper
exception: custom exceptions + GlobalExceptionHandler (@RestControllerAdvice)
config:    SecurityConfig (HTTP Basic, role rules), DataInitializer (seed data)
```

## Setup

### 1. Prerequisites
- JDK 17 or newer
- Eclipse IDE for Enterprise Java (with m2e / Maven support - included by default)
- MySQL 8 installed and **running** on localhost:3306

### 2. Import into Eclipse
1. Unzip the project.
2. `File -> Import -> Maven -> Existing Maven Projects` -> select the unzipped `product-order-management` folder -> Finish.
3. Wait for Maven to download dependencies (bottom-right progress bar).
4. Open `src/main/resources/application.properties` and set your MySQL **username/password**
   (database `product_order_db` is created automatically).
5. Right-click `ProductOrderApplication.java` -> `Run As -> Java Application`.
6. Open http://localhost:8081/

### PostgreSQL (optional)
Comment the MySQL lines and uncomment the PostgreSQL lines in `application.properties`.

### Default accounts
- ADMIN (auto-created): `admin@example.com` / `admin123`
- USER: register from the UI or via `POST /api/auth/register`
- 5 sample products are seeded on first start.

## Database schema
```
users        (id PK, name, email UNIQUE, password [BCrypt], role [USER|ADMIN], created_at)
products     (id PK, name, description, price DECIMAL(10,2), quantity INT, enabled BOOLEAN)
cart         (id PK, user_id FK -> users.id UNIQUE)                 -- one cart per user
cart_items   (id PK, cart_id FK -> cart.id, product_id FK -> products.id, quantity INT,
              UNIQUE(cart_id, product_id))
orders       (id PK, user_id FK -> users.id, total_amount DECIMAL(12,2), status, created_at)
order_items  (id PK, order_id FK -> orders.id, product_id FK -> products.id,
              product_name, price DECIMAL(10,2), quantity INT)      -- name/price are snapshots
```
Relations: users 1-1 cart, cart 1-N cart_items, users 1-N orders, orders 1-N order_items, products 1-N cart_items/order_items.
Tables are created automatically by Hibernate (`spring.jpa.hibernate.ddl-auto=update`).

## Authentication
Stateless **HTTP Basic** (`Authorization: Basic base64(email:password)`). Passwords are stored with BCrypt.

## API list
| Method | URL | Role | Description |
|--------|-----|------|-------------|
| POST | `/api/auth/register` | public | Register a USER |
| GET | `/api/auth/me` | any | Current user (login check) |
| GET | `/api/products` | any | USER: enabled products only; ADMIN: all products |
| GET | `/api/products/{id}` | any | Single product |
| POST | `/api/admin/products` | ADMIN | Add product |
| PUT | `/api/admin/products/{id}` | ADMIN | Update price & quantity |
| PATCH | `/api/admin/products/{id}/status` | ADMIN | Enable / disable |
| GET | `/api/cart` | USER | View cart |
| POST | `/api/cart/items` | USER | Add item `{productId, quantity}` |
| PUT | `/api/cart/items/{productId}` | USER | Update quantity `{quantity}` |
| DELETE | `/api/cart/items/{productId}` | USER | Remove item |
| DELETE | `/api/cart` | USER | Clear cart |
| POST | `/api/orders` | USER | Place order from cart (transactional) |
| GET | `/api/orders` | USER | My orders |
| GET | `/api/orders/{id}` | USER | One of my orders |

Error format (all errors):
```json
{ "timestamp": "2026-10-27T12:00:00", "status": 409, "error": "Conflict",
  "message": "Insufficient stock for 'Webcam 1080p': requested 5, available 3" }
```
Validation errors also include `fieldErrors: { "price": "Price must be at least 0.01" }`.
Status codes: 400 validation/bad request, 401 not logged in, 403 wrong role, 404 not found, 409 insufficient stock / duplicate email.

## Sample requests (curl)
```bash
# --- register a user ---
curl -X POST http://localhost:8081/api/auth/register -H "Content-Type: application/json" \
  -d '{"name":"Mahi","email":"mahi@example.com","password":"secret123"}'

# --- ADMIN: add product ---
curl -u admin@example.com:admin123 -X POST http://localhost:8081/api/admin/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Monitor 24in","description":"Full HD IPS","price":8999.00,"quantity":10}'

# --- ADMIN: update price & quantity ---
curl -u admin@example.com:admin123 -X PUT http://localhost:8081/api/admin/products/1 \
  -H "Content-Type: application/json" -d '{"price":649.00,"quantity":60}'

# --- ADMIN: disable / enable ---
curl -u admin@example.com:admin123 -X PATCH http://localhost:8081/api/admin/products/1/status \
  -H "Content-Type: application/json" -d '{"enabled":false}'

# --- USER: view products ---
curl -u mahi@example.com:secret123 http://localhost:8081/api/products

# --- USER: add to cart ---
curl -u mahi@example.com:secret123 -X POST http://localhost:8081/api/cart/items \
  -H "Content-Type: application/json" -d '{"productId":2,"quantity":2}'

# --- USER: update / remove cart item ---
curl -u mahi@example.com:secret123 -X PUT http://localhost:8081/api/cart/items/2 \
  -H "Content-Type: application/json" -d '{"quantity":3}'
curl -u mahi@example.com:secret123 -X DELETE http://localhost:8081/api/cart/items/2

# --- USER: view cart / place order / my orders ---
curl -u mahi@example.com:secret123 http://localhost:8081/api/cart
curl -u mahi@example.com:secret123 -X POST http://localhost:8081/api/orders
curl -u mahi@example.com:secret123 http://localhost:8081/api/orders
```

### Test the "insufficient stock" rule
Product 5 (Webcam) is seeded with quantity 3. Add 5 to cart -> `409 Conflict`. Add 2, place the order -> stock becomes 1.
Add 2 more and order again -> `409`, and nothing changes (rolled back).

## Project structure
```
src/main/java/com/example/pms
  ProductOrderApplication.java
  config/      SecurityConfig, DataInitializer
  controller/  AuthController, ProductController, AdminProductController, CartController, OrderController
  service/     AuthService, ProductService, CartService, OrderService
  repository/  UserRepository, ProductRepository, CartRepository, OrderRepository
  entity/      User, Product, Cart, CartItem, Order, OrderItem, Role, OrderStatus
  dto/         request/response records with validation annotations, ApiError
  exception/   custom exceptions + GlobalExceptionHandler
  mapper/      EntityMapper
src/main/resources
  application.properties, static/index.html (UI)
```
