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
package org.apache.tiles.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link AbstractClientRequest}.
 *
 * @version $Rev$ $Date$
 */
class AbstractClientRequestTest {

    /**
     * The request to test.
     */
    private AbstractClientRequest request;

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The application scope.
     */
    private Map<String, Object> applicationScope;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        applicationScope = new HashMap<String, Object>();
        request = createMockBuilder(AbstractClientRequest.class)
                .withConstructor(applicationContext).createMock();

        expect(applicationContext.getApplicationScope()).andReturn(applicationScope).anyTimes();
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#dispatch(java.lang.String)}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testDispatch() throws IOException {
        Map<String, Object> requestScope = new HashMap<String, Object>();

        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(requestScope).anyTimes();
        request.doForward("/my/path.html");
        request.doInclude("/my/path2.html");

        replay(request, applicationContext);
        request.dispatch("/my/path.html");
        request.dispatch("/my/path2.html");
        verify(request, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#include(java.lang.String)}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testInclude() throws IOException {
        Map<String, Object> requestScope = new HashMap<String, Object>();

        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(requestScope).anyTimes();
        request.doInclude("/my/path2.html");

        replay(request, applicationContext);
        request.include("/my/path2.html");
        assertTrue((Boolean)request.getContext(Request.REQUEST_SCOPE).get(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME));
        verify(request, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#getApplicationContext()}.
     */
    @Test
    void testGetApplicationContext() {
        replay(request, applicationContext);
        assertEquals(applicationContext, request.getApplicationContext());
        verify(request, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#getContext(java.lang.String)}.
     */
    @SuppressWarnings("unchecked")
    @Test
    void testGetContext() {
        Map<String, Object> scope = createMock(Map.class);

        expect(request.getContext("myScope")).andReturn(scope);

        replay(request, applicationContext, scope);
        assertEquals(scope, request.getContext("myScope"));
        verify(request, applicationContext, scope);
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#getAvailableScopes()}.
     */
    @Test
    void testGetAvailableScopes() {
        String[] scopes = new String[] {"one", "two", "three"};

        expect(request.getAvailableScopes()).andReturn(Arrays.asList(scopes));

        replay(request, applicationContext);
        assertArrayEquals(scopes, request.getAvailableScopes().toArray());
        verify(request, applicationContext);
    }

    /**
     * Test method for {@link org.apache.tiles.request.AbstractClientRequest#getApplicationScope()}.
     */
    @Test
    void testGetApplicationScope() {
        replay(request, applicationContext);
        assertEquals(applicationScope, request.getApplicationScope());
        verify(request, applicationContext);
    }

}
