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

import jakarta.servlet.ServletContext;
import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ApplicationScopeExtractor}.
 *
 * @version $Rev$ $Date$
 */
class ApplicationScopeExtractorTest {

    /**
     * The servlet context.
     */
    private ServletContext context;

    /**
     * The extractor to test.
     */
    private ApplicationScopeExtractor extractor;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        context = createMock(ServletContext.class);
        extractor = new ApplicationScopeExtractor(context);
    }

    /**
     * Test method for {@link ApplicationScopeExtractor#setValue(String, Object)}.
     */
    @Test
    void testSetValue() {
        context.setAttribute("attribute", "value");

        replay(context);
        extractor.setValue("attribute", "value");
        verify(context);
    }

    /**
     * Test method for {@link ApplicationScopeExtractor#removeValue(String)}.
     */
    @Test
    void testRemoveValue() {
        context.removeAttribute("attribute");

        replay(context);
        extractor.removeValue("attribute");
        verify(context);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.extractor.ApplicationScopeExtractor#getKeys()}.
     */
    @Test
    void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);
        expect(context.getAttributeNames()).andReturn(keys);

        replay(context, keys);
        assertEquals(keys, extractor.getKeys());
        verify(context, keys);
    }

    /**
     * Test method for {@link ApplicationScopeExtractor#getValue(String)}.
     */
    @Test
    void testGetValue() {
        expect(context.getAttribute("attribute")).andReturn("value");

        replay(context);
        assertEquals("value", extractor.getValue("attribute"));
        verify(context);
    }

}
