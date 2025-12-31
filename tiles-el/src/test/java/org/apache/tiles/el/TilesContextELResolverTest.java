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
package org.apache.tiles.el;

import jakarta.el.ELContext;
import jakarta.el.ELResolver;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.reflect.ClassUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.beans.FeatureDescriptor;
import java.beans.PropertyDescriptor;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link TilesContextELResolver}.
 *
 * @version $Rev$ $Date$
 */
class TilesContextELResolverTest {

    /**
     * The bean resolver.
     */
    private ELResolver beanElResolver;

    /**
     * The resolver to test.
     */
    private TilesContextELResolver resolver;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        beanElResolver = createMock(ELResolver.class);
        resolver = new TilesContextELResolver(beanElResolver);
    }

    /**
     * Test method for
     * {@link TilesContextELResolver#getCommonPropertyType(jakarta.el.ELContext, java.lang.Object)}.
     */
    @Test
    void testGetCommonPropertyTypeELContextObject() {
        replay(beanElResolver);
        Class<?> clazz = resolver.getCommonPropertyType(null, null);
        assertEquals(String.class, clazz, "The class is not correct");
        clazz = resolver.getCommonPropertyType(null, "Base object");
        assertNull(clazz, "The class for non root objects must be null");
        verify(beanElResolver);
    }

    /**
     * Test method for
     * {@link TilesContextELResolver#getFeatureDescriptors(jakarta.el.ELContext, java.lang.Object)}.
     */
    @Test
    void testGetFeatureDescriptorsELContextObject() {
        replay(beanElResolver);
        assertNull(resolver.getFeatureDescriptors(null, 1));
        Map<String, PropertyDescriptor> expected = new LinkedHashMap<String, PropertyDescriptor>();
        ClassUtil.collectBeanInfo(Request.class, expected);
        ClassUtil.collectBeanInfo(ApplicationContext.class, expected);
        Iterator<FeatureDescriptor> featureIt = resolver.getFeatureDescriptors(
                null, null);
        Iterator<? extends FeatureDescriptor> expectedIt = expected.values().iterator();
        while (featureIt.hasNext() && expectedIt.hasNext()) {
            assertEquals(expectedIt.next(), featureIt.next(), "The feature is not the same");
        }
        assertTrue(!featureIt.hasNext() && !expectedIt.hasNext(), "The feature descriptors are not of the same size");
        verify(beanElResolver);
    }

    /**
     * Tests {@link TilesContextBeanELResolver#getType(ELContext, Object, Object)}.
     */
    @SuppressWarnings("rawtypes")
    @Test
    void testGetType() {
        ELContext elContext = createMock(ELContext.class);
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);

        expect(elContext.getContext(Request.class)).andReturn(request);
        expect(elContext.getContext(ApplicationContext.class)).andReturn(applicationContext);
        expect(beanElResolver.getType(elContext, request, "responseCommitted")).andReturn((Class) Boolean.class);
        expect(beanElResolver.getType(elContext, applicationContext, "initParams")).andReturn((Class) Map.class);
        elContext.setPropertyResolved(true);
        expectLastCall().times(2);

        replay(beanElResolver, elContext, request, applicationContext);
        assertNull(resolver.getType(elContext, 1, "whatever"));
        assertEquals(Boolean.class, resolver.getType(elContext, null, "responseCommitted"));
        assertEquals(Map.class, resolver.getType(elContext, null, "initParams"));
        verify(beanElResolver, elContext, request, applicationContext);
    }

    /**
     * Tests {@link TilesContextBeanELResolver#getValue(ELContext, Object, Object)}.
     */
    @Test
    void testGetValue() {
        ELContext elContext = createMock(ELContext.class);
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        @SuppressWarnings("rawtypes")
        Map map = createMock(Map.class);

        expect(elContext.getContext(Request.class)).andReturn(request);
        expect(elContext.getContext(ApplicationContext.class)).andReturn(applicationContext);
        expect(beanElResolver.getValue(elContext, request, "responseCommitted")).andReturn(true);
        expect(beanElResolver.getValue(elContext, applicationContext, "initParams")).andReturn(map);
        elContext.setPropertyResolved(true);
        expectLastCall().times(2);

        replay(beanElResolver, elContext, request, applicationContext, map);
        assertNull(resolver.getValue(elContext, 1, "whatever"));
        assertEquals(true, resolver.getValue(elContext, null, "responseCommitted"));
        assertEquals(map, resolver.getValue(elContext, null, "initParams"));
        verify(beanElResolver, elContext, request, applicationContext, map);
    }

    /**
     * Test method for
     * {@link TilesContextELResolver#isReadOnly(jakarta.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testIsReadOnly() {
        replay(beanElResolver);
        ELContext context = new ELContextImpl(resolver);
        assertTrue(resolver.isReadOnly(context,
                null, null), "The value is not read only");
        verify(beanElResolver);
    }

    /**
     * Test method for
     * {@link TilesContextELResolver#isReadOnly(jakarta.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testIsReadOnlyNPE() {
        replay(beanElResolver);
        try {
            assertThrows(NullPointerException.class, () -> resolver.isReadOnly(null, null, null));
        } finally {
            verify(beanElResolver);
        }
    }

    /**
     * Tests {@link TilesContextELResolver#setValue(ELContext, Object, Object, Object)}.
     */
    @Test
    void testSetValue() {
        // Just to complete code coverage!
        resolver.setValue(null, null, null, null);
    }
}
