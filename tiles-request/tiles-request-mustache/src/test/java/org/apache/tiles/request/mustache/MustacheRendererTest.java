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
package org.apache.tiles.request.mustache;


import com.github.mustachejava.DefaultMustacheFactory;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.render.Renderer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Pattern;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link MustacheRenderer}.
 *
 * @version $Rev: 1066788 $ $Date: 2011-02-03 11:49:11 +0000 (Thu, 03 Feb 2011) $
 */
final class MustacheRendererTest {

    /**
     * Tests {@link MustacheRenderer#render(String, org.apache.tiles.request.Request)}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testRender() throws IOException {
        Request request = createMock(Request.class);
        StringWriter writer = new StringWriter();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ApplicationResource applicationResource = createMock(ApplicationResource.class);

        Map<String,Object> context = Collections.singletonMap("testKey", "test value");

        expect(applicationContext.getResource(isA(String.class))).andReturn(applicationResource).anyTimes();
        expect(request.getAvailableScopes()).andReturn(Arrays.asList(Request.REQUEST_SCOPE, "session", Request.APPLICATION_SCOPE));
        expect(request.getContext(Request.REQUEST_SCOPE)).andReturn(context);
        expect(request.getContext("session")).andReturn(Collections.emptyMap());
        expect(request.getContext(Request.APPLICATION_SCOPE)).andReturn(Collections.emptyMap());
        expect(request.getWriter()).andReturn(writer).anyTimes();

        replay(request, applicationContext, applicationResource);
        Renderer renderer = new MustacheRenderer(new DefaultMustacheFactory() {
                @Override
                public Reader getReader(String path) {
                    return new InputStreamReader(getClass().getResourceAsStream(path), StandardCharsets.UTF_8);
                }
            });
        renderer.render("/test.html", request);
        verify(request, applicationContext, applicationResource);
        assertEquals("test template with test value", writer.toString());
    }

    /**
     * Tests {@link MustacheRenderer#render(String, org.apache.tiles.request.Request)}.
     */
    @Test
    void testRenderException() {
        Request request = createMock(Request.class);
        replay(request);
        Renderer renderer = new MustacheRenderer();
        try {
            assertThrows(CannotRenderException.class, () -> renderer.render(null, request));
        } finally {
            verify(request);
        }
    }

    /**
     * Test method for
     * {@link MustacheRenderer#isRenderable(String, org.apache.tiles.request.Request)}
     * .
     */
    @Test
    void testIsRenderable() {
        MustacheRenderer renderer = new MustacheRenderer();
        final Pattern pattern = Pattern.compile("/.*");
        renderer.setAcceptPattern(pattern);

        assertTrue(renderer.isRenderable("/my/template.html", null));
        assertTrue(renderer.isRenderable("/my/template.any", null));
        assertFalse(renderer.isRenderable("my/template.html", null));
        assertFalse(renderer.isRenderable(null, null));
    }
}
