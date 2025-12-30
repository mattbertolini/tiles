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
package org.apache.tiles.request.freemarker.render;

import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.ext.jakarta.servlet.HttpRequestParametersHashModel;
import freemarker.ext.jakarta.servlet.ServletContextHashModel;
import freemarker.template.ObjectWrapper;
import freemarker.template.TemplateHashModel;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.render.CannotRenderException;
import org.apache.tiles.request.servlet.ServletApplicationContext;
import org.apache.tiles.request.servlet.ServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.Locale;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.createMockBuilder;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link FreemarkerRenderer}.
 *
 * @version $Rev$ $Date$
 */
class FreemarkerRendererTest {

    /**
     * The attribute name of the application model.
     */
    private static final String ATTR_APPLICATION_MODEL =
        ".freemarker.Application";

    /**
     * The attribute name of the JSP taglibs model.
     */
    private static final String ATTR_JSP_TAGLIBS_MODEL =
        ".freemarker.JspTaglibs";

    /**
     * The attribute name of the request model.
     */
    private static final String ATTR_REQUEST_MODEL = ".freemarker.Request";

    /**
     * The attribute name of the request parameters model.
     */
    private static final String ATTR_REQUEST_PARAMETERS_MODEL =
        ".freemarker.RequestParameters";

    /**
     * The renderer to test.
     */
    private FreemarkerRenderer renderer;

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The servlet context.
     */
    private ServletContext servletContext;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() throws IOException {
        applicationContext = createMock(ServletApplicationContext.class);
        servletContext = createMock(ServletContext.class);

        expect(applicationContext.getContext()).andReturn(servletContext).anyTimes();
        expect(servletContext.getRealPath(isA(String.class))).andReturn(null).anyTimes();
        expect(servletContext.getResource(isA(String.class))).andReturn(null).anyTimes();

        replay(applicationContext, servletContext);
        renderer = FreemarkerRendererBuilder.createInstance()
                .setApplicationContext(applicationContext)
                .setParameter("TemplatePath", "/")
                .setParameter("NoCache", "true")
                .setParameter("ContentType", "text/html")
                .setParameter("template_update_delay", "0")
                .setParameter("default_encoding", "ISO-8859-1")
                .setParameter("number_format", "0.##########").build();
    }

    /**
     * Tests {@link FreemarkerRenderer#render(String, org.apache.tiles.request.Request)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testWrite() throws IOException, ServletException {
        ApplicationContext applicationContext = createMock(ServletApplicationContext.class);
        ServletContext servletContext = createMock(ServletContext.class);
        GenericServlet servlet = createMockBuilder(GenericServlet.class).createMock();
        ServletConfig servletConfig = createMock(ServletConfig.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);

        expect(servletConfig.getServletContext()).andReturn(servletContext);

        replay(servlet, servletConfig);
        servlet.init(servletConfig);
        ServletContextHashModel servletContextHashModel = new ServletContextHashModel(servlet, objectWrapper);

        expect(applicationContext.getContext()).andReturn(servletContext).anyTimes();
        expect(servletContext.getRealPath(isA(String.class))).andReturn(null).anyTimes();
        URL resource = getClass().getResource("/test.ftl");
        expect(servletContext.getResource(isA(String.class))).andReturn(resource).anyTimes();
        expect(servletContext.getAttribute(ATTR_APPLICATION_MODEL)).andReturn(servletContextHashModel).anyTimes();
        expect(servletContext.getAttribute(ATTR_JSP_TAGLIBS_MODEL)).andReturn(null).anyTimes();
        servletContext.setAttribute(eq(ATTR_APPLICATION_MODEL), isA(ServletContextHashModel.class));
        servletContext.setAttribute(eq(ATTR_JSP_TAGLIBS_MODEL), isA(TemplateHashModel.class));
        expect(servletContext.getAttribute("org.eclipse.jakarta.servlet.jsp.jstl.fmt.localizationContext")).andReturn(null).anyTimes();
        expect(servletContext.getAttribute("org.eclipse.jetty.server.webapp.ContainerIncludeJarPattern")).andReturn(null).anyTimes();

        replay(applicationContext, servletContext, objectWrapper);

        FreemarkerRenderer renderer = FreemarkerRendererBuilder
                .createInstance().setApplicationContext(applicationContext)
                .setParameter("TemplatePath", "/")
                .setParameter("NoCache", "true")
                .setParameter("ContentType", "text/html")
                .setParameter("template_update_delay", "0")
                .setParameter("default_encoding", "ISO-8859-1")
                .setParameter("number_format", "0.##########").build();

        ServletRequest request = createMock(ServletRequest.class);
        HttpServletRequest httpRequest = createMock(HttpServletRequest.class);
        HttpServletResponse response = createMock(HttpServletResponse.class);
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(httpRequest, response, objectWrapper);
        HttpRequestParametersHashModel parametersModel = new HttpRequestParametersHashModel(httpRequest);

        expect(request.getRequest()).andReturn(httpRequest);
        expect(request.getResponse()).andReturn(response);
        expect(request.getPrintWriter()).andReturn(printWriter);
        expect(httpRequest.getLocale()).andReturn(Locale.ENGLISH).anyTimes();
        expect(httpRequest.getSession(false)).andReturn(null);
        expect(httpRequest.getAttribute(ATTR_REQUEST_MODEL)).andReturn(requestModel);
        expect(httpRequest.getAttribute(ATTR_REQUEST_PARAMETERS_MODEL)).andReturn(parametersModel);
        expect(httpRequest.getAttribute("org.eclipse.jakarta.servlet.jsp.jstl.fmt.localizationContext")).andReturn(null).anyTimes();
        expect(response.getContentType()).andReturn(null).anyTimes();
        response.setContentType("text/html; charset=ISO-8859-1");
        response.setHeader(eq("Cache-Control"), isA(String.class));
        response.setHeader("Pragma", "no-cache");
        response.setHeader(eq("Expires"), isA(String.class));

        replay(request, httpRequest, response);
        renderer.render("hello", request);
        stringWriter.close();
        assertTrue(stringWriter.toString().startsWith("Hello!"));
        verify(applicationContext, servletContext, request, httpRequest,
                response, servlet, servletConfig, objectWrapper);
    }

    /**
     * Tests {@link FreemarkerRenderer#render(String, org.apache.tiles.request.Request)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testRenderException1() throws IOException, ServletException {
        ApplicationContext applicationContext = createMock(ServletApplicationContext.class);
        ServletContext servletContext = createMock(ServletContext.class);
        GenericServlet servlet = createMockBuilder(GenericServlet.class).createMock();
        ServletConfig servletConfig = createMock(ServletConfig.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);

        replay(servlet, servletConfig);
        servlet.init(servletConfig);

        expect(applicationContext.getContext()).andReturn(servletContext).anyTimes();
        expect(servletContext.getRealPath(isA(String.class))).andReturn(null).anyTimes();
        URL resource = getClass().getResource("/test.ftl");
        expect(servletContext.getResource(isA(String.class))).andReturn(resource).anyTimes();

        replay(applicationContext, servletContext, objectWrapper);

        FreemarkerRenderer renderer = FreemarkerRendererBuilder
                .createInstance().setApplicationContext(applicationContext)
                .setParameter("TemplatePath", "/")
                .setParameter("NoCache", "true")
                .setParameter("ContentType", "text/html")
                .setParameter("template_update_delay", "0")
                .setParameter("default_encoding", "ISO-8859-1")
                .setParameter("number_format", "0.##########").build();

        ServletRequest request = createMock(ServletRequest.class);

        replay(request);
        try {
            assertThrows(CannotRenderException.class, () -> renderer.render(null, request));
        } finally {
            verify(applicationContext, servletContext, request, servlet,
                    servletConfig, objectWrapper);
        }
    }

    /**
     * Test method for
     * {@link FreemarkerRenderer
     * #isRenderable(Object, org.apache.tiles.Attribute, org.apache.tiles.context.TilesRequestContext)}
     * .
     */
    @Test
    void testIsRenderable() {
        assertTrue(renderer.isRenderable("/my/template.ftl", null));
        assertFalse(renderer.isRenderable("my/template.ftl", null));
        assertFalse(renderer.isRenderable("/my/template.jsp", null));
        verify(applicationContext, servletContext);
    }

}
