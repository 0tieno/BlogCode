package com.learn.hello.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// ══════════════════════════════════════════════════════════════════════════════
// CONCEPT 1: What is a Controller?
//
// A controller is the "front door" of your application.
// When a request comes in, Spring looks at the URL and HTTP method,
// finds the matching method in a controller, and calls it.
//
// Think of it like a receptionist:
//   "GET /greet/Alice"  → the receptionist says "I'll handle that" and calls greet("Alice")
// ══════════════════════════════════════════════════════════════════════════════

// @RestController = @Controller + @ResponseBody
//   This tells Spring: "This class handles web requests, and its methods return data (not HTML pages)."
//   The returned value is automatically converted to JSON and sent back to the client.
@RestController

// @RequestMapping sets a BASE path for ALL endpoints in this controller.
// Every endpoint here starts with /greet
@RequestMapping("/greet")
public class GreetingController {

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 2: HTTP Methods
    //
    // HTTP has several "verbs" that describe what you want to DO:
    //   GET    → read/fetch data       (like reading a book)
    //   POST   → create new data       (like writing a new book)
    //   PUT    → replace existing data (like replacing a book with a new edition)
    //   PATCH  → update part of data   (like editing one chapter)
    //   DELETE → remove data           (like destroying a book)
    // ══════════════════════════════════════════════════════════════════════════

    // Handles GET /greet
    // Returns a plain string. Spring wraps it in a 200 OK response automatically.
    @GetMapping
    public String helloWorld() {
        return "Hello, World!";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 3: @PathVariable
    //
    // A path variable is a dynamic part of the URL itself.
    // The {name} in the URL is a placeholder. Whatever the client puts there
    // gets injected into the 'name' parameter.
    //
    // Example: GET /greet/Alice  →  name = "Alice"
    //          GET /greet/Bob    →  name = "Bob"
    // ══════════════════════════════════════════════════════════════════════════

    // Handles GET /greet/{name}
    @GetMapping("/{name}")
    public String greetByName(@PathVariable String name) {
        return "Hello, " + name + "!";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 4: @RequestParam
    //
    // A request param comes AFTER the ? in the URL (the "query string").
    // It's used for optional filtering/searching, not identifying a resource.
    //
    // Example: GET /greet/search?name=Alice  →  name = "Alice"
    //          GET /greet/search             →  name = "Stranger" (the default)
    //
    // PathVariable vs RequestParam:
    //   /greet/{name}       → path variable — used to identify a specific resource
    //   /greet/search?name= → request param — used for optional filtering
    // ══════════════════════════════════════════════════════════════════════════

    // Handles GET /greet/search?name=Alice
    @GetMapping("/search")
    public String greetWithParam(
            @RequestParam(defaultValue = "Stranger") String name  // defaultValue means it's optional
    ) {
        return "Hello, " + name + "! (from query param)";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 5: @RequestBody — receiving JSON from the client
    //
    // For POST, PUT, PATCH — the client sends data IN THE BODY of the request.
    // @RequestBody tells Spring: "read the JSON from the request body and
    // convert it into the Java type I specified."
    //
    // The Java record GreetRequest below defines the shape of that JSON.
    // ══════════════════════════════════════════════════════════════════════════

    // Handles POST /greet
    // Body:  { "name": "Alice", "greeting": "Good morning" }
    @PostMapping
    public String customGreet(@RequestBody GreetRequest request) {
        return request.greeting() + ", " + request.name() + "!";
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 6: ResponseEntity — controlling the HTTP response
    //
    // So far our methods just returned data, and Spring defaulted to "200 OK".
    // ResponseEntity lets you control BOTH the body AND the status code.
    //
    // Common HTTP status codes:
    //   200 OK          — success, returning data
    //   201 Created     — success, a new resource was created
    //   400 Bad Request — the client sent invalid data
    //   401 Unauthorized— not logged in
    //   403 Forbidden   — logged in but not allowed
    //   404 Not Found   — the resource doesn't exist
    //   500 Server Error— something crashed on the server
    // ══════════════════════════════════════════════════════════════════════════

    // Handles GET /greet/status
    @GetMapping("/status")
    public ResponseEntity<String> statusDemo() {
        // ResponseEntity.ok() = 200 OK
        // ResponseEntity.status(HttpStatus.CREATED).body("...") = 201 Created
        // ResponseEntity.notFound().build() = 404 Not Found (no body)
        return ResponseEntity
                .status(HttpStatus.OK)             // explicitly set status
                .body("Everything is fine! 200 OK"); // set the response body
    }

    // ══════════════════════════════════════════════════════════════════════════
    // CONCEPT 7: Records — simple, immutable data containers
    //
    // A "record" is a concise way to define a class that just holds data.
    // This one line replaces: a class + 2 fields + 2 getters + constructor + equals + hashCode.
    //
    // In tests, `request.name()` reads the name field.
    // ══════════════════════════════════════════════════════════════════════════

    // This record defines what JSON the POST /greet endpoint expects:
    // { "name": "Alice", "greeting": "Good morning" }
    public record GreetRequest(String name, String greeting) {}
}

