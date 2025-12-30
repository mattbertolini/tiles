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
package org.apache.tiles.request.velocity.autotag;

import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.velocity.VelocityRequest;
import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTBlock;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.Node;
import org.apache.velocity.tools.view.ViewToolContext;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.Writer;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

class VelocityAutotagRuntimeTest {
    @Test
    void testCreateRequest() {
        InternalContextAdapter context = createMock(InternalContextAdapter.class);
        Writer writer = createMock(Writer.class);
        Node node = createMock(Node.class);
        ViewToolContext viewContext = createMock(ViewToolContext.class);
        HttpServletRequest request = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        ServletContext servletContext = createMock(ServletContext.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        expect(context.getInternalUserContext()).andReturn(viewContext);
        expect(viewContext.getRequest()).andReturn(request);
        expect(viewContext.getResponse()).andReturn(response);
        expect(viewContext.getServletContext()).andReturn(servletContext);
        expect(servletContext.getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(applicationContext);

        replay(context, writer, node, viewContext, request, response, servletContext, applicationContext);
        VelocityAutotagRuntime runtime = new VelocityAutotagRuntime();
        runtime.render(context, writer, node);
        Request velocityRequest = runtime.createRequest();
        assertInstanceOf(VelocityRequest.class, velocityRequest);
        verify(context, writer, node, viewContext, request, response, servletContext, applicationContext);
    }

    @Test
    void testCreateModelBody() {
        InternalContextAdapter context = createMock(InternalContextAdapter.class);
        Writer writer = createMock(Writer.class);
        Node node = createMock(Node.class);
        ASTBlock block = createMock(ASTBlock.class);
        expect(node.jjtGetChild(1)).andReturn(block);
        replay(context, writer, node, block);
        VelocityAutotagRuntime runtime = new VelocityAutotagRuntime();
        runtime.render(context, writer, node);
        ModelBody modelBody = runtime.createModelBody();
        assertInstanceOf(VelocityModelBody.class, modelBody);
        verify(context, writer, node, block);
    }

    @Test
    void testGetParameter() {
        InternalContextAdapter context = createMock(InternalContextAdapter.class);
        Writer writer = createMock(Writer.class);
        Node node = createMock(Node.class);
        ASTMap astMap = createMock(ASTMap.class);
        Map<String, Object> params = createMock(Map.class);
        expect(node.jjtGetChild(0)).andReturn(astMap);
        expect(astMap.value(context)).andReturn(params);
        expect(params.get(eq("notnullParam"))).andReturn(new Integer(42)).anyTimes();
        expect(params.get(eq("nullParam"))).andReturn(null).anyTimes();
        replay(context, writer, node, astMap, params);
        VelocityAutotagRuntime runtime = new VelocityAutotagRuntime();
        runtime.render(context, writer, node);
        Object notnullParam = runtime.getParameter("notnullParam", Object.class, null);
        Object nullParam = runtime.getParameter("nullParam", Object.class, null);
        int notnullParamDefault = runtime.getParameter("notnullParam", Integer.class, new Integer(24));
        int nullParamDefault = runtime.getParameter("nullParam", Integer.class, new Integer(24));
        assertEquals(42, notnullParam);
        assertNull(nullParam);
        assertEquals(42, notnullParamDefault);
        assertEquals(24, nullParamDefault);
        verify(context, writer, node, astMap, params);
    }
}
