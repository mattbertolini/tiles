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
package org.apache.tiles.renderer;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link DefinitionRenderer}.
 *
 * @version $Rev$ $Date$
 */
class DefinitionRendererTest {

    /**
     * The renderer.
     */
    private DefinitionRenderer renderer;

    /**
     * The container.
     */
    private TilesContainer container;

    /** {@inheritDoc} */
    @BeforeEach
    void setUp() {
        container = createMock(TilesContainer.class);
        renderer = new DefinitionRenderer(container);
    }

    /**
     * Tests
     * {@link DefinitionRenderer#render(String, Request)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    void testWrite() throws IOException {
        Request requestContext = createMock(Request.class);
        container.render("my.definition", requestContext);
        replay(requestContext, container);
        renderer.render("my.definition", requestContext);
        verify(requestContext, container);
    }

    /**
     * Tests
     * {@link DefinitionRenderer#render(String, Request)}.
     *
     * @throws IOException If something goes wrong during rendition.
     */
    @Test
    void testRenderException() throws IOException {
        Request requestContext = createMock(Request.class);
        replay(requestContext, container);
        assertThrows(CannotRenderException.class, () -> renderer.render(null, requestContext));
        verify(requestContext, container);
    }

    /**
     * Tests
     * {@link DefinitionRenderer#isRenderable(String, Request)}
     * .
     */
    @Test
    void testIsRenderable() {
        Request requestContext = createMock(Request.class);
        expect(container.isValidDefinition("my.definition", requestContext)).andReturn(Boolean.TRUE);
        replay(requestContext, container);
        assertTrue(renderer.isRenderable("my.definition", requestContext));
        assertFalse(renderer.isRenderable(null, requestContext));
        verify(requestContext, container);
    }
}
