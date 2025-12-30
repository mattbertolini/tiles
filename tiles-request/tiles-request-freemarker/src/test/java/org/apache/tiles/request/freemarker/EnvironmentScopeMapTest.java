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
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateHashModelEx;
import freemarker.template.TemplateModelException;
import org.junit.jupiter.api.Test;

import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Set;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link EnvironmentScopeMap}.
 *
 * @version $Rev$ $Date$
 */
class EnvironmentScopeMapTest {

    /**
     * Test method for {@link org.apache.tiles.request.freemarker.EnvironmentScopeMap#keySet()}.
     */
    @Test
    void testKeySet() {
        Template template = createMock(Template.class);
        TemplateHashModel model = createMock(TemplateHashModel.class);
        Configuration configuration = createMock(Configuration.class);
        Set<String> names = createMock(Set.class);
        Writer writer = new StringWriter();

        expect(template.getMacros()).andReturn(new HashMap<>());
        expect(template.getConfiguration()).andReturn(configuration);
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.VERSION_2_3_30).anyTimes();
        expect(configuration.getSharedVariableNames()).andReturn(names);

        replay(template, model, configuration, names);
        Environment env = new Environment(template, model, writer);
        EnvironmentScopeMap map = new EnvironmentScopeMap(env);
        assertEquals(names, map.keySet());
        verify(template, model, configuration, names);
    }


    /**
     * Test method for {@link org.apache.tiles.request.freemarker.EnvironmentScopeMap#keySet()}.
     * @throws TemplateModelException If something goes wrong.
     */
    @Test
    void testKeySetException() throws TemplateModelException {
        Template template = createMock(Template.class);
        TemplateHashModelEx model = createMock(TemplateHashModelEx.class);
        Configuration configuration = createMock(Configuration.class);
        Set<String> names = createMock(Set.class);
        Writer writer = new StringWriter();

        expect(template.getMacros()).andReturn(new HashMap<>());
        expect(template.getConfiguration()).andReturn(configuration);
        expect(configuration.getIncompatibleImprovements()).andReturn(Configuration.VERSION_2_3_30).anyTimes();
        expect(model.keys()).andThrow(new TemplateModelException());
        expect(configuration.getSharedVariableNames()).andReturn(names);

        try {
            replay(template, model, configuration, names);
            Environment env = new Environment(template, model, writer);
            EnvironmentScopeMap map = new EnvironmentScopeMap(env);
            assertThrows(FreemarkerRequestException.class, map::keySet);
        } finally {
            verify(template, model, configuration, names);
        }
    }
}
