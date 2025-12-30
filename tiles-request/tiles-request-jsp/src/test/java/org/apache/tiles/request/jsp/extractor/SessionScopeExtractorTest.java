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
package org.apache.tiles.request.jsp.extractor;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.http.HttpSession;
import jakarta.servlet.jsp.PageContext;
import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link SessionScopeExtractor}.
 *
 * @version $Rev$ $Date$
 */
class SessionScopeExtractorTest {

    /**
     * The page context.
     */
    private PageContext context;

    /**
     * The session.
     */
    private HttpSession session;

    /**
     * The extracto to test.
     */
    private SessionScopeExtractor extractor;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        context = createMock(PageContext.class);
        session = createMock(HttpSession.class);
        extractor = new SessionScopeExtractor(context);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#removeValue(java.lang.String)}.
     */
    @Test
    void testRemoveValue() {
        expect(context.getSession()).andReturn(session);
        context.removeAttribute("key", PageContext.SESSION_SCOPE);

        replay(context, session);
        extractor.removeValue("key");
        verify(context, session);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeys() {
        expect(context.getSession()).andReturn(session);
        Enumeration<String> keys = createMock(Enumeration.class);
        expect(context.getAttributeNamesInScope(PageContext.SESSION_SCOPE)).andReturn(keys);

        replay(context, session);
        assertEquals(keys, extractor.getKeys());
        verify(context, session);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValue() {
        expect(context.getSession()).andReturn(session);
        expect(context.getAttribute("key", PageContext.SESSION_SCOPE)).andReturn("value");

       replay(context, session);
       assertEquals("value", extractor.getValue("key"));
       verify(context, session);
    }

    /**
     * Test method for {@link ScopeExtractor#setValue(String, Object)}.
     */
    @Test
    void testSetValue() {
        expect(context.getSession()).andReturn(session);
        context.setAttribute("key", "value", PageContext.SESSION_SCOPE);

        replay(context, session);
        extractor.setValue("key", "value");
        verify(context, session);
    }


    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#removeValue(java.lang.String)}.
     */
    @Test
    void testRemoveValueNoSession() {
        expect(context.getSession()).andReturn(null);

        replay(context, session);
        extractor.removeValue("key");
        verify(context, session);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeysNoSession() {
        expect(context.getSession()).andReturn(null);

        replay(context, session);
        assertNull(extractor.getKeys());
        verify(context, session);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValueNoSession() {
       expect(context.getSession()).andReturn(null);

       replay(context, session);
       assertNull(extractor.getValue("key"));
       verify(context, session);
    }

    /**
     * Test method for {@link ScopeExtractor#setValue(String, Object)}.
     */
    @Test
    void testSetValueNoSession() {
        expect(context.getSession()).andReturn(null);

        replay(context, session);
        extractor.setValue("key", "value");
        verify(context, session);
    }
}
