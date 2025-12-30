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
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import org.junit.jupiter.api.Test;

import java.io.Writer;
import java.util.HashMap;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link FreemarkerUtil}.
 *
 * @version $Rev$ $Date$
 */
class FreemarkerUtilTest {

    /**
     * Test method for {@link FreemarkerUtil#getAsObject(TemplateModel, Class, Object)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetAsObject() throws TemplateModelException {
        TemplateNumberModel model = createMock(TemplateNumberModel.class);
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Writer out = createMock(Writer.class);

        expect(model.getAsNumber()).andReturn(Integer.valueOf(42));
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();

        replay(template, rootDataModel, out, configuration);
        new Environment(template, rootDataModel, out);

        replay(model);
        assertEquals(Integer.valueOf(42), FreemarkerUtil.getAsObject(model, Integer.class, Integer.valueOf(1)));
        verify(template, rootDataModel, out, model, configuration);
    }

    /**
     * Test method for {@link FreemarkerUtil#getAsObject(TemplateModel, Class, Object)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetAsObjectDefault() throws TemplateModelException {
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Writer out = createMock(Writer.class);

        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();

        replay(template, rootDataModel, out, configuration);
        new Environment(template, rootDataModel, out);

        assertEquals(Integer.valueOf(1), FreemarkerUtil.getAsObject(null, Integer.class, Integer.valueOf(1)));
        verify(template, rootDataModel, out, configuration);
    }

    /**
     * Test method for {@link FreemarkerUtil#getAsObject(TemplateModel, Class, Object)}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testGetAsObjectException() throws TemplateModelException {
        TemplateNumberModel model = createMock(TemplateNumberModel.class);
        Template template = createMock(Template.class);
        TemplateHashModel rootDataModel = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Writer out = createMock(Writer.class);

        expect(model.getAsNumber()).andThrow(new TemplateModelException());
        expect(template.getMacros()).andReturn(new HashMap<String, Macro>()).anyTimes();
        expect(template.getConfiguration()).andReturn(configuration).anyTimes();
        expect(template.getLocale()).andReturn(null).anyTimes();
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS).anyTimes();

        replay(template, rootDataModel, out, configuration);
        new Environment(template, rootDataModel, out);

        replay(model);
        try {
            assertThrows(FreemarkerAutotagException.class, () ->
                    assertEquals(Integer.valueOf(42), FreemarkerUtil.getAsObject(model, Integer.class, Integer.valueOf(1))));
        } finally {
            verify(template, rootDataModel, out, model, configuration);
        }
    }

}
