---
name: java-test-patterns
description: >-
  Skill para criar testes de padronizados com BDD (Behavior Driven Development), e boas práticas.
license: MIT
metadata:
  category: development
  source:
    repository: local
    path: java-test-patterns
---

# Java Test Patterns Skill for Spring Boot Applications

## Overview

This skill provides comprehensive guidelines and patterns for writing effective Java tests in Spring Boot applications, with emphasis on BDD (Behavior Driven Development) principles and best practices observed in production-grade projects.

## Key Testing Framework & Dependencies

- **JUnit 5** - Modern testing framework
- **Spring Boot Test** - Integration testing support
- **Mockito** - Mocking framework
- **Spring Security Test** - Security testing utilities
- **Spring Mock MVC** - Web layer testing
- **H2 Database** - In-memory database for testing
- **JaCoCo** - Code coverage tool

## Testing Architecture Patterns

### 1. Test Structure Organization

#### Package Structure

```
src/test/java/
├── [main-package]/
│   ├── ObjectBuilders.java           # Test data factory
│   ├── controller/
│   │   └── *ControllerTest.java
│   ├── service/
│   │   └── *ServiceTest.java
│   └── repository/
│       └── *RepositoryTest.java
```

#### Test Class Template

```java
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ServiceNameTest {

    @Autowired
    private ServiceUnderTest service;

    @Autowired
    private RepositoryType repository;

    @MockitoBean
    private ExternalService externalService;

    @BeforeEach
    void setUp() {
        // Setup code
    }
}
```

### 2. Object Builders Pattern

#### Centralized Test Data Factory

```java
@Configuration
public class ObjectBuilders {

    // Constants for test data
    public static final String JWT_EMAIL = "test@example.com";
    public static final String JWT_CPF = "12345678900";
    public static final String VALID_CEP = "23565200";

    // JWT Mock setup
    public static final JwtRequestPostProcessor JWT_WITH_ROLE_ADMIN = jwt()
        .jwt(builder -> builder
            .claim("preferred_username", JWT_CPF)
            .claim("email", JWT_EMAIL)
            .subject("test-subject"))
        .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));

    // Entity builders
    public static Pedido pedidoValido() {
        return Pedido.builder()
            .requente(requerenteValido())
            .dataAgendamento(LocalDate.now().plusDays(20))
            .cep(VALID_CEP)
            .build();
    }

    // JSON utilities
    public static String writeJson(Object object) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return mapper.writeValueAsString(object);
    }
}
```

### 3. BDD Testing Patterns

#### Given-When-Then Structure

```java
@Test
void shouldCreateOrderSuccessfully() {
    // Given
    var orderRequest = createValidOrderRequest();
    when(orderService.create(any())).thenReturn(orderResponse());

    // When
    var result = mockMvc.perform(post("/api/orders")
        .with(JWT_WITH_ROLE_USER)
        .contentType(MediaType.APPLICATION_JSON)
        .content(writeJson(orderRequest)))

    // Then
        .andExpect(status().isCreated())
        .andExpect(header().exists("Location"))
        .andExpect(jsonPath("$.id").isNotEmpty());

    verify(orderService, times(1)).create(any());
}
```

#### Exception Testing Pattern

```java
@Test
void shouldThrowExceptionWhenOrderNotFound() {
    // Given
    String orderId = "non-existent-id";
    when(orderService.findById(orderId))
        .thenThrow(new OrderNotFoundException("Order not found"));

    // When & Then
    var result = mockMvc.perform(get("/api/orders/{id}", orderId)
        .with(JWT_WITH_ROLE_USER))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Order not found"));

    verify(orderService, times(1)).findById(orderId);
}
```

### 4. Service Layer Testing Patterns

#### Unit Testing with Mocks

```java
@SpringBootTest
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private ExternalPaymentService paymentService;

    @Test
    void shouldProcessOrderSuccessfully() {
        // Given
        Order order = createValidOrder();
        when(orderRepository.save(any())).thenReturn(order);
        when(paymentService.processPayment(any())).thenReturn(true);

        // When
        Order result = orderService.processOrder(order);

        // Then
        assertNotNull(result);
        assertEquals(OrderStatus.PROCESSING, result.getStatus());
        verify(orderRepository, times(1)).save(any());
        verify(paymentService, times(1)).processPayment(any());
    }

    @Test
    void shouldThrowExceptionWhenPaymentFails() {
        // Given
        Order order = createValidOrder();
        when(orderRepository.save(any())).thenReturn(order);
        when(paymentService.processPayment(any())).thenReturn(false);

        // When & Then
        assertThrows(PaymentFailedException.class, () -> {
            orderService.processOrder(order);
        });
    }
}
```

#### Integration Testing Pattern

```java
@SpringBootTest
@Transactional
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldPersistOrderInDatabase() {
        // Given
        Order order = createValidOrder();

        // When
        Order savedOrder = orderService.createOrder(order);

        // Then
        assertNotNull(savedOrder.getId());
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElse(null);
        assertNotNull(foundOrder);
        assertEquals(order.getCustomer().getId(), foundOrder.getCustomer().getId());
    }
}
```

### 5. Controller Testing Patterns

#### REST API Testing

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldCreateOrderReturn201() throws Exception {
        // Given
        OrderRequest request = createValidOrderRequest();
        OrderResponse response = createOrderResponse();
        when(orderService.create(any())).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(JWT_WITH_ROLE_USER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(writeJson(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andExpect(jsonPath("$.id").value(response.getId()))
            .andExpect(jsonPath("$.status").value(response.getStatus().name()));
    }

    @Test
    void shouldReturn400WhenInvalidRequest() throws Exception {
        // Given
        OrderRequest invalidRequest = createInvalidOrderRequest();

        // When & Then
        mockMvc.perform(post("/api/orders")
            .with(JWT_WITH_ROLE_USER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(writeJson(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.errors").isArray());
    }
}
```

#### Security Testing Pattern

```java
@Test
void shouldRequireAuthentication() throws Exception {
    mockMvc.perform(get("/api/orders"))
        .andExpect(status().isUnauthorized());
}

@Test
void shouldAllowAdminAccess() throws Exception {
    mockMvc.perform(get("/api/admin/orders")
        .with(JWT_WITH_ROLE_ADMIN))
        .andExpect(status().isOk());
}

@Test
void shouldDenyUserAccessToAdminEndpoint() throws Exception {
    mockMvc.perform(get("/api/admin/orders")
        .with(JWT_WITH_ROLE_USER))
        .andExpect(status().isForbidden());
}
```

### 6. Repository Testing Patterns

#### Data Layer Testing

```java
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void shouldFindOrderById() {
        // Given
        Order order = createValidOrder();
        Order savedOrder = entityManager.persist(order);

        // When
        Order foundOrder = orderRepository.findById(savedOrder.getId()).orElse(null);

        // Then
        assertNotNull(foundOrder);
        assertEquals(savedOrder.getId(), foundOrder.getId());
    }

    @Test
    void shouldFindOrdersByCustomer() {
        // Given
        Customer customer = createValidCustomer();
        Order order1 = createOrder(customer);
        Order order2 = createOrder(customer);
        entityManager.persist(order1);
        entityManager.persist(order2);

        // When
        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        // Then
        assertEquals(2, orders.size());
    }
}
```

### 7. BDD-Specific Patterns

#### Cucumber Integration Pattern

```java
// Step definitions
@SpringBootTest
@AutoConfigureMockMvc
public class OrderStepDefinitions {

    @Autowired
    private MockMvc mockMvc;

    private ResultActions result;
    private OrderRequest request;
    private OrderResponse response;

    @Given("a valid order request")
    public void givenValidOrderRequest() {
        request = createValidOrderRequest();
    }

    @When("the client creates the order")
    public void whenClientCreatesOrder() throws Exception {
        result = mockMvc.perform(post("/api/orders")
            .with(JWT_WITH_ROLE_USER)
            .contentType(MediaType.APPLICATION_JSON)
            .content(writeJson(request)));
    }

    @Then("the order should be created successfully")
    public void thenOrderShouldBeCreated() throws Exception {
        result.andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").isNotEmpty());
    }
}
```

#### Behavior Specification Pattern

```java
@Test
void shouldHandleOrderCancellationWhenOrderIsInProcessing() {
    // Given
    Order order = createOrderInStatus(OrderStatus.PROCESSING);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    // When
    Order cancelledOrder = orderService.cancelOrder(order.getId());

    // Then
    assertEquals(OrderStatus.CANCELLED, cancelledOrder.getStatus());
    assertNotNull(cancelledOrder.getCancelledAt());
}

@Test
void shouldNotAllowCancellationWhenOrderIsAlreadyShipped() {
    // Given
    Order order = createOrderInStatus(OrderStatus.SHIPPED);
    when(orderRepository.findById(any())).thenReturn(Optional.of(order));

    // When & Then
    assertThrows(OrderCannotBeCancelledException.class, () -> {
        orderService.cancelOrder(order.getId());
    });
}
```

### 8. Performance Testing Patterns

#### Load Testing Pattern

```java
@SpringBootTest
@AutoConfigureMockMvc
class OrderPerformanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldHandleConcurrentOrderCreation() throws InterruptedException {
        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> {
                try {
                    mockMvc.perform(post("/api/orders")
                        .with(JWT_WITH_ROLE_USER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(writeJson(createValidOrderRequest())))
                        .andExpect(status().isCreated());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
    }
}
```

### 9. Test Data Management

#### Test Data Setup Pattern

```java
@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    private Customer testCustomer;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // Create test data
        testCustomer = createValidCustomer();
        testCustomer = customerRepository.save(testCustomer);

        testOrder = createOrder(testCustomer);
        testOrder = orderRepository.save(testOrder);
    }

    @Test
    void shouldFindOrdersByCustomer() {
        List<Order> orders = orderRepository.findByCustomerId(testCustomer.getId());
        assertFalse(orders.isEmpty());
    }
}
```

### 10. Best Practices

#### Test Naming Conventions

- Use descriptive test names that explain the behavior
- Follow the pattern: `should[Action]When[Condition]Then[Result]`
- Use BDD-style naming when appropriate

#### Test Organization

- Group related tests in inner classes or use `@Nested` classes
- Use `@Tag` annotations to categorize tests (e.g., `@Tag("integration")`, `@Tag("security")`)
- Separate unit tests from integration tests

#### Mocking Best Practices

- Mock external dependencies and collaborators
- Verify interactions only when necessary
- Use `@MockitoBean` for Spring-managed beans
- Avoid mocking the class under test

#### Assertion Best Practices

- Use specific assertions rather than generic ones
- Assert the most important outcomes first
- Use Hamcrest matchers for better readability
- Include meaningful failure messages

#### Performance Considerations

- Use `@Transactional` to avoid database pollution
- Clean up test data after each test
- Use in-memory databases for faster tests
- Parallelize independent tests when possible

### 11. Common Anti-Patterns to Avoid

#### Don't Mock Everything

```java
// Anti-pattern: Mocking the class under test
@Mock
private OrderService orderService; // Wrong!

// Correct: Use real implementation for unit tests
@Autowired
private OrderService orderService; // Right!
```

#### Don't Use Magic Strings

```java
// Anti-pattern: Hardcoded values
result.andExpect(jsonPath("$.message").value("Order not found"));

// Better: Use constants
private static final String ORDER_NOT_FOUND = "Order not found";
result.andExpect(jsonPath("$.message").value(ORDER_NOT_FOUND));
```

#### Don't Skip Setup

```java
// Anti-pattern: No setup
@Test
void testSomething() {
    // No setup, unclear test data
}

// Better: Clear setup
@BeforeEach
void setUp() {
    // Clear and prepare test data
}
```

This comprehensive skill provides a solid foundation for writing effective Java tests in Spring Boot applications, incorporating BDD principles and industry best practices observed in production environments.
