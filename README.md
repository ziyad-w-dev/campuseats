# CampusEats

CampusEats is a small campus food-ordering app. A student browses vendors, opens a vendor's menu,
and adds dishes to their cart (order 1) or removes them without reloading the page. The server
enforces the rules: a sold-out dish cannot be added (409), and a single dish cannot go above a
quantity of 10 (409).

**Implemented in Spring Boot instead of Rails with the instructor's approval.**

Stack: Java 21, Spring Boot, Spring MVC, Spring Data JPA, Bean Validation, Thymeleaf, Flyway,
H2 (in-memory), Lombok, vanilla JavaScript.

## Requirements

- Java 21 or newer (JDK). Check with `java -version`.
- Internet access on the first run, to download dependencies.
- No database or Maven install needed: H2 runs in memory and the Maven wrapper is included.

Author: Ziyad Alqassim (202303311), SENG 361 Course Assignment (Version B).
Sources: SENG 361 course slides (Modules 1 to 7). I used Claude (Anthropic) for this assignment.

## How to run

```bash
./mvnw spring-boot:run        # on Windows: mvnw.cmd spring-boot:run
```

Then open <http://localhost:8080/vendors/1/menu-items>.

The H2 database console is at <http://localhost:8080/h2-console>:

| Setting  | Value                     |
|----------|---------------------------|
| JDBC URL | `jdbc:h2:mem:campuseats`  |
| User     | `sa`                      |
| Password | *(empty)*                 |

The database lives in memory. Every time the app starts, Flyway rebuilds it from
`V1__create_tables.sql` and `V2__seed_data.sql`, so the cart is empty after each restart.
There is no login: order 1 is used as the student's cart.

## Routes

| Method | URI                              | Controller action             | Returns              |
|--------|----------------------------------|-------------------------------|----------------------|
| GET    | `/vendors`                       | `VendorController.index`      | Vendors page (HTML)  |
| GET    | `/vendors/{vendorId}/menu-items` | `MenuItemController.index`    | Menu + cart (HTML)   |
| GET    | `/orders/{orderId}`              | `OrderController.show`        | Cart page (HTML)     |
| GET    | `/orders/{orderId}/items`        | `OrderItemController.index`   | Cart items (JSON)    |
| POST   | `/orders/{orderId}/items`        | `OrderItemController.create`  | `CartResponse`, 201  |
| DELETE | `/orders/{orderId}/items/{id}`   | `OrderItemController.destroy` | `CartResponse`, 200  |

Errors are returned as JSON `{"status": 409, "message": "<message>", "timestamp": "..."}`:
404 (not found), 409 (sold out or quantity limit), 400 (invalid request body).

Example:

```bash
curl -i -X POST http://localhost:8080/orders/1/items \
     -H "Content-Type: application/json" \
     -d '{"menuItemId": 1, "quantity": 1}'
```

## Rails to Spring Boot mapping

| Rails                                                         | Spring Boot (this project)                                              |
|---------------------------------------------------------------|-------------------------------------------------------------------------|
| `config/routes.rb` nested `resources`                         | `@RequestMapping("/orders/{orderId}/items")` + `@GetMapping`/`@PostMapping`/`@DeleteMapping` |
| `params[:order_id]`                                           | `@PathVariable Long orderId`                                            |
| `before_action :set_order`                                    | `@ModelAttribute("order")` method in `OrderItemController`              |
| Strong parameters `params.require(...).permit(...)`           | `@Valid @RequestBody AddItemRequest` (a record with only the allowed fields) |
| `validates :quantity, numericality: { in: 1..10 }`            | `@Min(1) @Max(10)` (Bean Validation)                                    |
| ActiveRecord model                                            | JPA `@Entity` (`Vendor`, `MenuItem`, `Order`, `OrderItem`)              |
| `belongs_to` / `has_many`                                     | `@ManyToOne` / `@OneToMany(mappedBy = ...)`                             |
| `has_many ..., dependent: :destroy`                           | `cascade = CascadeType.ALL, orphanRemoval = true`                       |
| `Model.find(id)` raising `RecordNotFound`                     | `repository.findById(id).orElseThrow(() -> new NotFoundException(...))` |
| `Vendor.all`, `MenuItem.where(vendor_id: ...)`                | `JpaRepository.findAll()`, derived query `findByVendorId`              |
| `includes(:menu_item)` (avoid N+1)                            | `JOIN FETCH oi.menuItem` in `OrderItemRepository`                       |
| `ActiveRecord::Base.transaction do ... end` / service object  | `@Service` + `@Transactional` (`OrderItemService`)                      |
| `rescue_from` in `ApplicationController`                      | `@RestControllerAdvice` (`GlobalExceptionHandler`)                      |
| `render json: ..., status: :created`                          | `@RestController` return value + `@ResponseStatus(HttpStatus.CREATED)`  |
| ERB views `app/views/...`                                     | Thymeleaf templates `src/main/resources/templates/...`                  |
| Partial `_order_item.html.erb` / `render order_item`          | `th:fragment="row(item)"` in `fragments/order_item.html` / `th:replace` |
| `db/migrate/*.rb`                                             | Flyway `db/migration/V1__create_tables.sql`                             |
| `db/seeds.rb`                                                 | Flyway `V2__seed_data.sql`                                              |
| `app/javascript` (Stimulus / fetch)                           | `static/js/cart.js` (vanilla JS)                                        |
| `rails server`                                                | `./mvnw spring-boot:run`                                                |
| `rails dbconsole`                                             | H2 console at `/h2-console`                                             |

## Who drove which part

Individual submission: Ziyad Alqassim (ID 202303311) wrote and tested all parts, A to D.

| Part | Driver | Navigator |
|------|--------|-----------|
| A. Requirements (BDD, user stories, estimation) | Ziyad | none (individual) |
| B. Spring Boot back end | Ziyad | none (individual) |
| C. JavaScript client | Ziyad | none (individual) |
| D. Reflection | Ziyad | none (individual) |