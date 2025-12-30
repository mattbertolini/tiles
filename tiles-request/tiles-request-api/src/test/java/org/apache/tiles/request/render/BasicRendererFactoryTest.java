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

import org.apache.tiles.request.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Basic renderer factory implementation.
 *
 * @version $Rev$ $Date$
 */
class BasicRendererFactoryTest {

    /**
     * The renderer factory.
     */
    private BasicRendererFactory rendererFactory;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        rendererFactory = new BasicRendererFactory();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        replay(applicationContext);
    }

    /**
     * Tests execution and
     * {@link BasicRendererFactory#getRenderer(String)}.
     */
    @Test
    void testInitAndGetRenderer() {
        Renderer renderer1 = createMock(Renderer.class);
        Renderer renderer2 = createMock(Renderer.class);
        Renderer renderer3 = createMock(Renderer.class);
        Renderer renderer4 = createMock(Renderer.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(renderer1, renderer2, renderer3, renderer4, applicationContext);
        rendererFactory.registerRenderer("string", renderer1);
        rendererFactory.registerRenderer("test", renderer2);
        rendererFactory.registerRenderer("test2", renderer3);
        rendererFactory.setDefaultRenderer(renderer4);
        Renderer renderer = rendererFactory.getRenderer("string");
        assertSame(renderer1, renderer);
        renderer = rendererFactory.getRenderer("test");
        assertSame(renderer2, renderer);
        renderer = rendererFactory.getRenderer("test2");
        assertSame(renderer3, renderer);
        renderer = rendererFactory.getRenderer(null);
        assertSame(renderer4, renderer);
        verify(renderer1, renderer2, renderer3, renderer4, applicationContext);
    }

    /**
     * Tests execution and
     * {@link BasicRendererFactory#getRenderer(String)}.
     */
    @Test
    void testGetRendererException() {
        Renderer renderer1 = createMock(Renderer.class);
        Renderer renderer2 = createMock(Renderer.class);
        Renderer renderer3 = createMock(Renderer.class);
        Renderer renderer4 = createMock(Renderer.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        replay(renderer1, renderer2, renderer3, renderer4, applicationContext);
        rendererFactory.registerRenderer("string", renderer1);
        rendererFactory.registerRenderer("test", renderer2);
        rendererFactory.registerRenderer("test2", renderer3);
        rendererFactory.setDefaultRenderer(renderer4);
        try {
            assertThrows(NoSuchRendererException.class, () -> rendererFactory.getRenderer("nothing"));
        } finally {
            verify(renderer1, renderer2, renderer3, renderer4, applicationContext);
        }
    }

    /**
     * Tests {@link BasicRendererFactory#initializeRenderer(AttributeRenderer)}.
     */
    @Test
    void testInitializeRenderer() {
        // TODO This will be removed in future, only named renderers should be available.
    }
}
