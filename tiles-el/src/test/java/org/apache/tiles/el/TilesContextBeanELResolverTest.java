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

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.beans.FeatureDescriptor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.el.ELContext;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link TilesContextBeanELResolver}.
 *
 * @version $Rev$ $Date$
 */
class TilesContextBeanELResolverTest {

    /**
     * The resolver to test.
     */
    private TilesContextBeanELResolver resolver;

    /**
     * Sets up the test.
     */
    @BeforeEach
    public void setUp() {
        resolver = new TilesContextBeanELResolver();
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#getCommonPropertyType(javax.el.ELContext, java.lang.Object)}.
     */
    @Test
    void testGetCommonPropertyType() {
        Class<?> clazz = resolver.getCommonPropertyType(null, null);
        assertEquals(String.class, clazz, "The class is not correct");
        clazz = resolver.getCommonPropertyType(null, "Base object");
        assertNull(clazz, "The class for non root objects must be null");
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#getFeatureDescriptors(javax.el.ELContext, java.lang.Object)}.
     */
    @Test
    void testGetFeatureDescriptors() {
        Map<String, Object> requestScope = new HashMap<String, Object>();
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", new Integer(1));
        applicationScope.put("object3", new Float(2.0));
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(
                applicationScope).anyTimes();
        expect(request.getAvailableScopes()).andReturn(
                Arrays.asList(new String[] { "request", "session", "application" }))
                .anyTimes();
        replay(request, applicationContext);

        ELContext context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);

        List<FeatureDescriptor> expected = new ArrayList<FeatureDescriptor>();
        resolver.collectBeanInfo(requestScope, expected);
        resolver.collectBeanInfo(sessionScope, expected);
        resolver.collectBeanInfo(applicationScope, expected);
        Iterator<FeatureDescriptor> featureIt = resolver.getFeatureDescriptors(
                context, null);
        Iterator<FeatureDescriptor> expectedIt = expected.iterator();
        while (featureIt.hasNext() && expectedIt.hasNext()) {
            FeatureDescriptor expectedDescriptor = expectedIt.next();
            FeatureDescriptor descriptor = featureIt.next();
            assertEquals(expectedDescriptor
                    .getDisplayName(), descriptor.getDisplayName(), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .getName(), descriptor.getName(), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .getShortDescription(), descriptor.getShortDescription(), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .getValue("type"), descriptor.getValue("type"), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .getValue("resolvableAtDesignTime"), descriptor
                    .getValue("resolvableAtDesignTime"), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .isExpert(), descriptor.isExpert(), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .isHidden(), descriptor.isHidden(), "The feature is not the same");
            assertEquals(expectedDescriptor
                    .isPreferred(), descriptor.isPreferred(), "The feature is not the same");
        }
        Assertions.assertTrue(!featureIt.hasNext() && !expectedIt.hasNext(), "The feature descriptors are not of the same size");
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#getType(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testGetType() {
        Map<String, Object> requestScope = new HashMap<String, Object>();
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", new Integer(1));
        applicationScope.put("object3", new Float(2.0));
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(
                applicationScope).anyTimes();
        expect(request.getAvailableScopes()).andReturn(
                Arrays.asList(new String[] { "request", "session", "application" }))
                .anyTimes();
        replay(request, applicationContext);

        ELContext context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);

        assertEquals(String.class, resolver.getType(
                context, null, "object1"), "The type is not correct");
        assertEquals(Integer.class, resolver.getType(
                context, null, "object2"), "The type is not correct");
        assertEquals(Float.class, resolver.getType(
                context, null, "object3"), "The type is not correct");
        assertNull(resolver.getType(context, new Integer(1), "whatever"));
        assertNull(resolver.getType(context, null, "object4"));
        verify(request, applicationContext);
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testGetValue() {
        Map<String, Object> requestScope = new HashMap<String, Object>();
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", new Integer(1));
        applicationScope.put("object3", new Float(2.0));
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(
                applicationScope).anyTimes();
        expect(request.getAvailableScopes()).andReturn(
                Arrays.asList(new String[] { "request", "session", "application" }))
                .anyTimes();
        replay(request, applicationContext);

        ELContext context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);

        assertEquals("value", resolver.getValue(
                context, null, "object1"), "The value is not correct");
        assertEquals(new Integer(1), resolver
                .getValue(context, null, "object2"), "The value is not correct");
        assertEquals(new Float(2.0), resolver
                .getValue(context, null, "object3"), "The value is not correct");
        assertNull(resolver.getValue(context, new Integer(1), "whatever"));
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#isReadOnly(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testIsReadOnlyELContextObjectObject() {
        ELContext context = new ELContextImpl(resolver);
        Assertions.assertTrue(resolver.isReadOnly(context,
                null, null), "The value is not read only");
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#isReadOnly(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testIsReadOnlyNPE() {
        assertThrows(NullPointerException.class, () -> resolver.isReadOnly(null, null, null));
    }

    /**
     * Tests {@link TilesContextBeanELResolver#setValue(ELContext, Object, Object, Object)}.
     */
    @Test
    void testSetValue() {
        // Just to complete code coverage!
        resolver.setValue(null, null, null, null);
    }

    /**
     * Test method for
     * {@link TilesContextBeanELResolver#findObjectByProperty(javax.el.ELContext, java.lang.Object)}.
     */
    @Test
    void testFindObjectByProperty() {
        Map<String, Object> requestScope = new HashMap<String, Object>();
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", new Integer(1));
        applicationScope.put("object3", new Float(2.0));
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(
                applicationScope).anyTimes();
        expect(request.getAvailableScopes()).andReturn(
                Arrays.asList(new String[] { "request", "session", "application" }))
                .anyTimes();
        replay(request, applicationContext);

        ELContext context = new ELContextImpl(resolver);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);

        assertEquals("value", resolver
                .findObjectByProperty(context, "object1"), "The value is not correct");
        assertEquals(new Integer(1), resolver
                .findObjectByProperty(context, "object2"), "The value is not correct");
        assertEquals(new Float(2.0), resolver
                .findObjectByProperty(context, "object3"), "The value is not correct");
    }

    /**
     * Test method for
     * {@link org.apache.tiles.el.TilesContextBeanELResolver#getObject(java.util.Map, java.lang.String)}.
     */
    @Test
    void testGetObject() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("object1", "value");
        assertEquals("value", resolver.getObject(
                map, "object1"), "The value is not correct");
        assertNull(resolver.getObject(map, "object2"), "The value is not null");
        assertNull(resolver.getObject(null, "object1"), "The value is not null");
    }

    /**
     * Tests {@link TilesContextBeanELResolver#collectBeanInfo(Map, List)}.
     */
    @Test
    void testCollectBeanInfoEmpty() {
        resolver.collectBeanInfo(null, null);
    }
}
