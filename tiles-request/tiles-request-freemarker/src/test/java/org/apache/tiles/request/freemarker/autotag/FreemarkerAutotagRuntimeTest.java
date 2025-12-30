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
package org.apache.tiles.request.freemarker.autotag;

import freemarker.core.Environment;
import freemarker.core.Macro;
import freemarker.ext.jakarta.servlet.FreemarkerServlet;
import freemarker.ext.jakarta.servlet.HttpRequestHashModel;
import freemarker.ext.jakarta.servlet.ServletContextHashModel;
import freemarker.template.Configuration;
import freemarker.template.ObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import jakarta.servlet.GenericServlet;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.request.ApplicationAccess;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.freemarker.FreemarkerRequest;
import org.junit.jupiter.api.Test;

import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link FreemarkerAutotagRuntime}.
 *
 * @version $Rev$ $Date$
 */
class FreemarkerAutotagRuntimeTest {

    @Test
    void testCreateRequest() throws TemplateModelException {
        Map<String, TemplateModel> params = createMock(Map.class);
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Writer out = createMock(Writer.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        GenericServlet servlet = createMock(GenericServlet.class);
        ObjectWrapper wrapper = createMock(ObjectWrapper.class);
        ServletContext servletContext = createMock(ServletContext.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        HttpServletRequest httpServletRequest = createMock(HttpServletRequest.class);
        HttpServletResponse httpServletResponse = createMock(HttpServletResponse.class);
        Configuration configuration = createMock(Configuration.class);

        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();
        expect(servlet.getServletContext()).andReturn(servletContext)
                .anyTimes();
        expect(
                servletContext
                        .getAttribute(ApplicationAccess.APPLICATION_CONTEXT_ATTRIBUTE))
                .andReturn(applicationContext);

        replay(servlet, wrapper, servletContext, applicationContext,
                httpServletRequest, httpServletResponse, configuration);
        ServletContextHashModel servletContextHashModel = new ServletContextHashModel(
                servlet, wrapper);
        HttpRequestHashModel httpRequestHashModel = new HttpRequestHashModel(
                httpServletRequest, httpServletResponse, wrapper);

        expect(rootDataModel.get(FreemarkerServlet.KEY_APPLICATION)).andReturn(
                servletContextHashModel);
        expect(rootDataModel.get(FreemarkerServlet.KEY_REQUEST)).andReturn(
                httpRequestHashModel);

        replay(template, rootDataModel, out);
        Environment env = new Environment(template, rootDataModel, out);

        replay(params, body);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();

        runtime.execute(env, params, new TemplateModel[0], body);
        Request request = runtime.createRequest();
        assertInstanceOf(FreemarkerRequest.class, request);
        verify(servlet, wrapper, servletContext, applicationContext,
                httpServletRequest, httpServletResponse,
                template, rootDataModel, out,
                params, body);
    }

    @Test
    void testCreateModelBody() {
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Writer out = createMock(Writer.class);
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();
        replay(template, rootDataModel, out, configuration);
        Environment env = new Environment(template, rootDataModel, out);
        Map<String, TemplateModel> params = createMock(Map.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        replay(params, body);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        ModelBody modelBody = runtime.createModelBody();
        assertInstanceOf(FreemarkerModelBody.class, modelBody);
        verify(template, rootDataModel, out, params, body, configuration);
    }

    @Test
    void testGetParameter() throws TemplateModelException {
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Writer out = createMock(Writer.class);
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();
        replay(template, rootDataModel, out, configuration);
        Environment env = new Environment(template, rootDataModel, out);
        TemplateNumberModel model = createMock(TemplateNumberModel.class);
        expect(model.getAsNumber()).andReturn(Integer.valueOf(42)).anyTimes();
        Map<String, TemplateModel> params = createMock(Map.class);
        TemplateDirectiveBody body = createMock(TemplateDirectiveBody.class);
        expect(params.get(eq("notnullParam"))).andReturn(model).anyTimes();
        expect(params.get(eq("nullParam"))).andReturn(null).anyTimes();
        replay(model, params, body);
        FreemarkerAutotagRuntime runtime = new FreemarkerAutotagRuntime();
        runtime.execute(env, params, new TemplateModel[0], body);
        Object notnullParam = runtime.getParameter("notnullParam", Object.class, null);
        Object nullParam = runtime.getParameter("nullParam", Object.class, null);
        int notnullParamDefault = runtime.getParameter("notnullParam", Integer.class, Integer.valueOf(24));
        int nullParamDefault = runtime.getParameter("nullParam", Integer.class, Integer.valueOf(24));
        assertEquals(42, notnullParam);
        assertNull(nullParam);
        assertEquals(42, notnullParamDefault);
        assertEquals(24, nullParamDefault);
        verify(template, rootDataModel, out, model, params, body, configuration);
    }
}
