/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.request.velocity.render;

import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ApplicationContextJeeConfig}.
 *
 * @version $Rev$ $Date$
 */
class ApplicationContextJeeConfigTest {

    /**
     * The configuration to test.
     */
    private ApplicationContextJeeConfig config;

    /**
     * The application context.
     */
    private ServletApplicationContext applicationContext;

    /**
     * The servlet context.
     */
    private ServletContext servletContext;

    /**
     * Custom parameters.
     */
    private Map<String, String> params;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        servletContext = createMock(ServletContext.class);
        applicationContext = new ServletApplicationContext(servletContext);
    }

    /**
     * Tears down the test.
     */
    @AfterEach
    void tearDown() {
        verify(servletContext);
    }

    /**
     * Tests {@link ApplicationContextJeeConfig#getInitParameter(String)}.
     */
    @Test
    void testGetInitParameter() {
        params = new HashMap<String, String>();
        params.put("one", "value1");
        config = new ApplicationContextJeeConfig(applicationContext, params);
        replay(servletContext);
        assertEquals("value1", config.getInitParameter("one"));
    }

    /**
     * Tests {@link ApplicationContextJeeConfig#findInitParameter(String)}.
     */
    @Test
    void testFindInitParameter() {
        params = new HashMap<String, String>();
        params.put("one", "value1");
        config = new ApplicationContextJeeConfig(applicationContext, params);
        replay(servletContext);
        assertEquals("value1", config.findInitParameter("one"));
    }

    /**
     * Tests {@link ApplicationContextJeeConfig#getInitParameterNames()}.
     */
    @Test
    void testGetInitParameterNames() {
        params = new HashMap<String, String>();
        params.put("one", "value1");
        config = new ApplicationContextJeeConfig(applicationContext, params);
        replay(servletContext);
        Enumeration<String> names = config.getInitParameterNames();
        assertTrue(names.hasMoreElements());
        assertEquals("one", names.nextElement());
        assertFalse(names.hasMoreElements());
    }

    /**
     * Tests {@link ApplicationContextJeeConfig#getName()}.
     */
    @Test
    void testGetName() {
        params = new HashMap<String, String>();
        params.put("one", "value1");
        config = new ApplicationContextJeeConfig(applicationContext, params);
        replay(servletContext);
        assertEquals("Application Context JEE Config", config.getName());
    }

    /**
     * Tests {@link ApplicationContextJeeConfig#getServletContext()}.
     */
    @Test
    void testGetServletContext() {
        params = new HashMap<String, String>();
        params.put("one", "value1");
        config = new ApplicationContextJeeConfig(applicationContext, params);
        replay(servletContext);
        assertEquals(servletContext, config.getServletContext());
    }

}
