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
package org.apache.tiles.request.render;

import org.apache.tiles.request.DispatchRequest;
import org.apache.tiles.request.Request;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link DispatchRenderer}.
 *
 * @version $Rev$ $Date$
 */
class DispatchRendererTest {

    /**
     * The renderer.
     */
    private DispatchRenderer renderer;

    /** {@inheritDoc} */
    @BeforeEach
    void setUp() {
        renderer = new DispatchRenderer();
    }

    /**
     * Tests
     * {@link DispatchRenderer#render(String, DispatchRequest)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    void testWrite() throws IOException {
        DispatchRequest requestContext = createMock(DispatchRequest.class);
        requestContext.dispatch("/myTemplate.jsp");
        replay(requestContext);
        renderer.render("/myTemplate.jsp", requestContext);
        verify(requestContext);
    }

    /**
     * Tests
     * {@link DispatchRenderer#render(String, DispatchRequest)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    public void testWriteNull() throws IOException {
        DispatchRequest requestContext = createMock(DispatchRequest.class);
        replay(requestContext);
        assertThrows(CannotRenderException.class, () -> renderer.render(null, requestContext));
        verify(requestContext);
    }

    /**
     * Tests
     * {@link DispatchRenderer#isRenderable(String, DispatchRequest)}.
     */
    @Test
    void testIsRenderable() {
        Request requestContext = createMock(DispatchRequest.class);
        replay(requestContext);
        assertTrue(renderer.isRenderable("/myTemplate.jsp", requestContext));
        assertFalse(renderer.isRenderable(null, requestContext));
        verify(requestContext);
    }
}
