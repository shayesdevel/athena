package com.athena.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for controller integration tests.
 * Provides MockMvc and ObjectMapper for testing REST endpoints.
 *
 * <p>Note: Concrete test classes should use @WebMvcTest to load only the controller layer
 * and mock the service dependencies with @MockBean.</p>
 */
public abstract class AbstractControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;
}
