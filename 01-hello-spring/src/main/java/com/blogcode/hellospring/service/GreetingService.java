package com.blogcode.hellospring.service;

import com.blogcode.hellospring.model.Greeting;

/**
 * Contract for producing {@link Greeting} instances.
 *
 * <p>This interface exists to teach the "program to an interface, not an
 * implementation" principle that underpins almost all professional Spring
 * applications. The {@link com.blogcode.hellospring.controller.GreetingController}
 * depends only on this interface - it has no idea that
 * {@link com.blogcode.hellospring.service.impl.GreetingServiceImpl} is the
 * concrete class doing the work. Benefits of this separation:
 * <ul>
 *   <li><b>Testability</b> - controller tests can substitute a mock/stub
 *       implementation of this interface without touching real business
 *       logic.</li>
 *   <li><b>Replaceability</b> - the implementation could later be swapped
 *       (e.g. for one that calls a database or an external API) without any
 *       change to the controller.</li>
 *   <li><b>Clear boundaries</b> - the interface documents exactly what the
 *       service layer promises to do, independent of how it does it.</li>
 * </ul>
 */
public interface GreetingService {

    /**
     * Builds a default greeting addressed to "World".
     *
     * @return a fully populated {@link Greeting} with a generated id, the
     *         default message and the current server timestamp.
     */
    Greeting createDefaultGreeting();

    /**
     * Builds a personalised greeting addressed to the given name.
     *
     * @param name the recipient's name to embed in the greeting message;
     *             callers are expected to have already validated that this
     *             is non-blank before invoking this method.
     * @return a fully populated {@link Greeting} containing a message
     *         personalised with {@code name}.
     */
    Greeting createGreetingFor(String name);
}
