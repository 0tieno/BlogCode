package com.blogcode.ecommerce.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogcode.ecommerce.dto.PageResponse;
import com.blogcode.ecommerce.dto.ProductDto;
import com.blogcode.ecommerce.service.ProductService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

/**
 * Web-layer slice test for {@link ProductController}.
 *
 * <p><strong>Why this class exists:</strong> {@code @WebMvcTest} boots only
 * the Spring MVC infrastructure (controllers, {@code @ControllerAdvice},
 * JSON serialization) for the given controller, wiring in
 * {@link MockMvc} thanks to the {@code spring-boot-starter-webmvc-test}
 * dependency, without starting a full application context, a real
 * database, or a real Redis connection. The {@link ProductService}
 * collaborator is replaced with a Mockito mock via {@code @MockitoBean}, so
 * this test verifies HTTP request/response mapping in isolation from
 * business logic - a fast, focused complement to full integration tests.
 */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    /**
     * Verifies that {@code GET /api/v1/products/{id}} returns {@code 200 OK}
     * with the JSON representation of the product supplied by the (mocked)
     * service layer.
     *
     * @throws Exception if MockMvc fails to dispatch the simulated request
     */
    @Test
    void getById_returnsProduct_whenProductExists() throws Exception {
        ProductDto product = new ProductDto(
                1L,
                "Mechanical Keyboard",
                "A clicky keyboard",
                new BigDecimal("99.99"),
                10,
                null,
                Set.of(),
                Instant.now(),
                Instant.now());
        when(productService.getById(1L)).thenReturn(product);

        mockMvc.perform(get("/api/v1/products/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"));
    }

    /**
     * Verifies that {@code GET /api/v1/products} returns {@code 200 OK}
     * with a paginated envelope built from the (mocked) service layer.
     *
     * @throws Exception if MockMvc fails to dispatch the simulated request
     */
    @Test
    void search_returnsPagedProducts() throws Exception {
        ProductDto product = new ProductDto(
                2L, "Wireless Mouse", "Ergonomic mouse", new BigDecimal("29.99"), 50, null, Set.of(),
                Instant.now(), Instant.now());
        PageResponse<ProductDto> page = new PageResponse<>(List.of(product), 0, 20, 1, 1, true);
        when(productService.search(any(), any(), any(), any(), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/products").param("name", "mouse"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Wireless Mouse"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    /**
     * Verifies that submitting an invalid product payload (blank name,
     * negative price) is rejected with {@code 400 Bad Request} before ever
     * reaching {@link ProductService}, proving the Bean Validation
     * annotations on {@code ProductRequest} are enforced.
     *
     * @throws Exception if MockMvc fails to dispatch the simulated request
     */
    @Test
    void create_returnsBadRequest_whenPayloadIsInvalid() throws Exception {
        String invalidPayload = """
                {
                  "name": "",
                  "price": -5,
                  "stockQuantity": -1,
                  "categoryIds": []
                }
                """;

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPayload))
                .andExpect(status().isBadRequest());
    }
}
