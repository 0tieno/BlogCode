package com.blogcode.hellospring.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.blogcode.hellospring.model.Greeting;
import com.blogcode.hellospring.service.GreetingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.webmvc.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Web-layer test for {@link GreetingController}.
 *
 * <p>{@link WebMvcTest} boots only the Spring MVC infrastructure needed to
 * exercise controllers (argument resolvers, message converters, exception
 * handling, etc.) instead of the entire application context - this makes the
 * test fast and focused purely on the HTTP contract exposed by
 * {@code GreetingController}. Because {@link WebMvcTest} deliberately does
 * <b>not</b> instantiate {@code @Service} beans, we replace the real
 * {@link GreetingService} with a Mockito-backed {@link MockBean}, letting us
 * control exactly what the "service layer" returns for each test case.
 *
 * <p>This class is provided as a teaching reference for how to test a
 * Spring MVC endpoint with {@link MockMvc}; running it is optional but
 * recommended once students are comfortable with the main code.
 */
@WebMvcTest(GreetingController.class)
class GreetingControllerTest {

    /**
     * Injected by Spring's test support; drives simulated HTTP requests
     * against the {@code DispatcherServlet} without starting a real network
     * server, which keeps the test fast and deterministic.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Used to serialize the {@link GreetingController.GreetingRequest}
     * request body sent in {@link #createGreeting_returnsPersonalisedGreeting()}.
     */
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Mockito mock registered as a Spring bean, replacing the real
     * {@code GreetingServiceImpl} for the duration of this test class. Uses
     * {@link MockitoBean}, the modern replacement for the deprecated
     * {@code @MockBean} annotation, to have Spring's test context override
     * the real bean with a Mockito mock.
     */
    @MockitoBean
    private GreetingService greetingService;

    /**
     * Verifies that {@code GET /api/v1/greetings} returns the greeting
     * produced by the service layer, serialized as JSON, with a
     * {@code 200 OK} status.
     */
    @Test
    void getDefaultGreeting_returnsGreetingFromService() throws Exception {
        Greeting stubbed = new Greeting(1L, "Hello, World!", Instant.now());
        org.mockito.Mockito.when(greetingService.createDefaultGreeting()).thenReturn(stubbed);

        mockMvc.perform(get("/api/v1/greetings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is("Hello, World!")));
    }

    /**
     * Verifies that {@code POST /api/v1/greetings} accepts a JSON body,
     * forwards the name to the service layer, and returns
     * {@code 201 Created} with the personalised greeting.
     */
    @Test
    void createGreeting_returnsPersonalisedGreeting() throws Exception {
        Greeting stubbed = new Greeting(2L, "Hello, Ada!", Instant.now());
        org.mockito.Mockito.when(greetingService.createGreetingFor("Ada")).thenReturn(stubbed);

        var requestBody = new GreetingController.GreetingRequest("Ada");

        mockMvc.perform(post("/api/v1/greetings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", is("Hello, Ada!")));
    }
}
