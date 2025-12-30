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
import java.util.Enumeration;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests {@link ParameterExtractor}.
 *
 * @version $Rev$ $Date$
 */
class ParameterExtractorTest {

    /**
     * The request.
     */
    private HttpServletRequest request;

    /**
     * The extractor to test.
     */
    private ParameterExtractor extractor;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        request = createMock(HttpServletRequest.class);
        extractor = new ParameterExtractor(request);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.extractor.ParameterExtractor#getKeys()}.
     */
    @Test
    void testGetKeys() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(request.getParameterNames()).andReturn(keys);

        replay(request, keys);
        assertEquals(keys, extractor.getKeys());
        verify(request, keys);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.extractor.ParameterExtractor#getValue(java.lang.String)}.
     */
    @Test
    void testGetValue() {
        expect(request.getParameter("name")).andReturn("value");

        replay(request);
        assertEquals("value", extractor.getValue("name"));
        verify(request);
    }

}
