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
package org.apache.tiles.request.freemarker;

import freemarker.core.Environment;
import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.ext.jakarta.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link FreemarkerRequestUtil}.
 *
 */
class FreemarkerRequestUtilTest {

    /**
     * A string writer.
     */
    private StringWriter writer;

    /**
     * The FreeMarker environment.
     */
    private Environment env;

    /**
     * The locale object.
     */
    private Locale locale;

    /**
     * The template.
     */
    private Template template;

    /**
     * The template model.
     */
    private TemplateHashModel model;

    /**
     * Sets up the model.
     */
    @BeforeEach
    void setUp() {
        template = createMock(Template.class);
        model = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        writer = new StringWriter();
        expect(template.getMacros()).andReturn(new HashMap<>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(Locale.ITALY).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();
        replay(configuration);
    }

    /**
     * Test method for {@link FreemarkerRequestUtil#getRequestHashModel(freemarker.core.Environment)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetRequestHashModel() throws TemplateModelException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);
        HttpRequestHashModel requestModel = new HttpRequestHashModel(request, objectWrapper);

        expect(model.get("Request")).andReturn(requestModel);

        replay(template, model, request, objectWrapper);
        env = new Environment(template, model, writer);
        locale = Locale.ITALY;
        env.setLocale(locale);
        assertEquals(requestModel, FreemarkerRequestUtil.getRequestHashModel(env));
        verify(template, model, request, objectWrapper);
    }

    /**
     * Test method for {@link FreemarkerRequestUtil#getRequestHashModel(freemarker.core.Environment)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetRequestHashModelException() throws TemplateModelException {
        HttpServletRequest request = createMock(HttpServletRequest.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);

        expect(model.get("Request")).andThrow(new TemplateModelException());

        replay(template, model, request, objectWrapper);
        try {
            env = new Environment(template, model, writer);
            locale = Locale.ITALY;
            env.setLocale(locale);
            assertThrows(NotAvailableFreemarkerServletException.class, () -> FreemarkerRequestUtil.getRequestHashModel(env));
        } finally {
            verify(template, model, request, objectWrapper);
        }
    }


    /**
     * Test method for {@link FreemarkerRequestUtil
     * #getServletContextHashModel(freemarker.core.Environment)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetServletContextHashModel() throws TemplateModelException {
        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);
        expect(servlet.getServletContext()).andReturn(servletContext);
        replay(servlet, objectWrapper);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);

        expect(model.get("Application")).andReturn(servletContextModel);

        replay(template, model, servletContext);
        env = new Environment(template, model, writer);
        locale = Locale.ITALY;
        env.setLocale(locale);
        assertEquals(servletContextModel, FreemarkerRequestUtil.getServletContextHashModel(env));
        verify(template, model, servlet, servletContext, objectWrapper);
    }

    /**
     * Test method for {@link FreemarkerRequestUtil
     * #getServletContextHashModel(freemarker.core.Environment)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetServletContextHashModelException() throws TemplateModelException {
        GenericServlet servlet = createMock(GenericServlet.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);
        replay(servlet, objectWrapper);

        expect(model.get("Application")).andThrow(new TemplateModelException());

        replay(template, model);
        try {
            env = new Environment(template, model, writer);
            locale = Locale.ITALY;
            env.setLocale(locale);
            assertThrows(NotAvailableFreemarkerServletException.class, () -> FreemarkerRequestUtil.getServletContextHashModel(env));
        } finally {
            verify(template, model, servlet, objectWrapper);
        }
    }

    /**
     * Test method for {@link FreemarkerRequestUtil
     * #getApplicationContext(Environment)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetApplicationContext() throws TemplateModelException {
        GenericServlet servlet = createMock(GenericServlet.class);
        ServletContext servletContext = createMock(ServletContext.class);
        ObjectWrapper objectWrapper = createMock(ObjectWrapper.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        expect(servlet.getServletContext()).andReturn(servletContext).times(2);
        expect(servletContext.getAttribute(ApplicationAccess
                .APPLICATION_CONTEXT_ATTRIBUTE)).andReturn(applicationContext);

        replay(servlet, objectWrapper);
        ServletContextHashModel servletContextModel = new ServletContextHashModel(servlet, objectWrapper);

        expect(model.get("Application")).andReturn(servletContextModel);

        replay(template, model, servletContext);
        env = new Environment(template, model, writer);
        locale = Locale.ITALY;
        env.setLocale(locale);
        assertEquals(applicationContext, FreemarkerRequestUtil.getApplicationContext(env));
        verify(template, model, servlet, servletContext, objectWrapper);
    }
}
