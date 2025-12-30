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
package org.apache.tiles.request.servlet.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link SessionScopeExtractor}.
 *
 * @version $Rev$ $Date$
 */
class SessionScopeExtractorTest {

    /**
     * The request.
     */
    private HttpServletRequest request;

    /**
     * The session.
     */
    private HttpSession session;

    /**
     * The extractot to test.
     */
    private SessionScopeExtractor extractor;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        request = createMock(HttpServletRequest.class);
        session = createMock(HttpSession.class);
        extractor = new SessionScopeExtractor(request);
    }

    /**
     * Test method for {@link SessionScopeExtractor#setValue(java.lang.String, java.lang.Object)}.
     */
    @Test
    void testSetValue() {
        expect(request.getSession()).andReturn(session);
        session.setAttribute("name", "value");

        replay(request, session);
        extractor.setValue("name", "value");
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#removeValue(java.lang.String)}.
     */
    @Test
    void testRemoveValue() {
        expect(request.getSession(false)).andReturn(session);
        session.removeAttribute("name");

        replay(request, session);
        extractor.removeValue("name");
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(request.getSession(false)).andReturn(session);
        expect(session.getAttributeNames()).andReturn(keys);

        replay(request, session, keys);
        assertEquals(keys, extractor.getKeys());
        verify(request, session, keys);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeysNoSession() {
        expect(request.getSession(false)).andReturn(null);

        replay(request, session);
        Enumeration<String> keys = extractor.getKeys();
        assertNotNull(keys);
        assertFalse(keys.hasMoreElements());
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValue() {
        expect(request.getSession(false)).andReturn(session);
        expect(session.getAttribute("name")).andReturn("value");

        replay(request, session);
        assertEquals("value", extractor.getValue("name"));
        verify(request, session);
    }

    /**
     * Test method for {@link SessionScopeExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValueNoSession() {
        expect(request.getSession(false)).andReturn(null);

        replay(request, session);
        assertNull(extractor.getValue("name"));
        verify(request, session);
    }

}
