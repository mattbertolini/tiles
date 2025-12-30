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

import jakarta.servlet.jsp.JspContext;
import jakarta.servlet.jsp.PageContext;
import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ScopeExtractor}.
 *
 * @version $Rev$ $Date$
 */
class ScopeExtractorTest {

    /**
     * The JSP context.
     */
    private JspContext context;

    /**
     * The extractor to test.
     */
    private ScopeExtractor extractor;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        context = createMock(JspContext.class);
        extractor = new ScopeExtractor(context, PageContext.PAGE_SCOPE);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#removeValue(java.lang.String)}.
     */
    @Test
    void testRemoveValue() {
        context.removeAttribute("key", PageContext.PAGE_SCOPE);

        replay(context);
        extractor.removeValue("key");
        verify(context);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);
        expect(context.getAttributeNamesInScope(PageContext.PAGE_SCOPE)).andReturn(keys);

        replay(context);
        assertEquals(keys, extractor.getKeys());
        verify(context);
    }

    /**
     * Test method for {@link org.apache.tiles.request.jsp.extractor.ScopeExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValue() {
        expect(context.getAttribute("key", PageContext.PAGE_SCOPE)).andReturn("value");

        replay(context);
        assertEquals("value", extractor.getValue("key"));
        verify(context);
    }

    /**
     * Test method for {@link ScopeExtractor#setValue(String, Object)}.
     */
    @Test
    void testSetValue() {
        context.setAttribute("key", "value", PageContext.PAGE_SCOPE);

        replay(context);
        extractor.setValue("key", "value");
        verify(context);
    }

}
