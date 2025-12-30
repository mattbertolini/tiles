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

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.beans.FeatureDescriptor;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jakarta.el.ELContext;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


/**
 * Tests {@link ScopeELResolver}.
 *
 * @version $Rev$ $Date$
 */
class ScopeELResolverTest {

    /**
     * The resolver to test.
     */
    private ScopeELResolver resolver;

    /** {@inheritDoc} */
    @BeforeEach
    void setUp() {
        resolver = new ScopeELResolver();
    }

    /**
     * Tests {@link ScopeELResolver#getCommonPropertyType(ELContext, Object)}.
     */
    @Test
    void testGetCommonPropertyType() {
        ELContext elContext = createMock(ELContext.class);

        replay(elContext);
        Assertions.assertNull(resolver.getCommonPropertyType(elContext, new Integer(1)));
        Assertions.assertEquals(Map.class, resolver.getCommonPropertyType(elContext, null));
        verify(elContext);
    }

    /**
     * Tests {@link ScopeELResolver#getFeatureDescriptors(ELContext, Object)}.
     */
    @Test
    void testGetFeatureDescriptors() {
        ELContext elContext = createMock(ELContext.class);
        Request request = createMock(Request.class);

        expect(elContext.getContext(Request.class)).andReturn(request);
        expect(request.getAvailableScopes()).andReturn(Arrays.asList(new String[] {"one", "two"}));

        replay(elContext, request);
        Assertions.assertFalse(resolver.getFeatureDescriptors(elContext, new Integer(1)).hasNext());
        Iterator<FeatureDescriptor> descriptors = resolver.getFeatureDescriptors(elContext, null);
        FeatureDescriptor descriptor = descriptors.next();
        Assertions.assertEquals("oneScope", descriptor.getName());
        descriptor = descriptors.next();
        Assertions.assertEquals("twoScope", descriptor.getName());
        Assertions.assertFalse(descriptors.hasNext());
        verify(elContext, request);
    }

    /**
     * Test method for
     * {@link ScopeELResolver#getType(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testGetType() {
        Request request = createMock(Request.class);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        ELContext context = new ELContextImpl(resolver);
        replay(request, applicationContext);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);
        Assertions.assertNull(resolver.getType(context, new Integer(1), "whatever"));
        Assertions.assertEquals(Map.class, resolver.getType(context, null, "requestScope"), "The requestScope object is not a map.");
        Assertions.assertEquals(Map.class, resolver.getType(context, null, "sessionScope"), "The sessionScope object is not a map.");
        Assertions.assertEquals(Map.class, resolver.getType(context, null, "applicationScope"), "The applicationScope object is not a map.");
    }

    /**
     * Test method for
     * {@link ScopeELResolver#getValue(javax.el.ELContext, java.lang.Object, java.lang.Object)}.
     */
    @Test
    void testGetValue() {
        Map<String, Object> requestScope = new HashMap<String, Object>();
        requestScope.put("objectKey", "objectValue");
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        sessionScope.put("sessionObjectKey", "sessionObjectValue");
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        applicationScope.put("applicationObjectKey", "applicationObjectValue");
        Request request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope);
        expect(request.getContext("session")).andReturn(sessionScope);
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        expect(request.getContext("application")).andReturn(applicationScope);
        ELContext context = new ELContextImpl(resolver);
        replay(request, applicationContext);
        context.putContext(Request.class, request);
        context.putContext(ApplicationContext.class, applicationContext);
        Assertions.assertNull(resolver.getValue(context, new Integer(1), "whatever"));
        Assertions.assertEquals(requestScope, resolver.getValue(context, null, "requestScope"), "The requestScope map does not correspond");
        Assertions.assertEquals(sessionScope, resolver.getValue(context, null, "sessionScope"), "The sessionScope map does not correspond");
        Assertions.assertEquals(applicationScope, resolver.getValue(context, null,
                "applicationScope"), "The applicationScope map does not correspond");
    }

    /**
     * Tests {@link ScopeELResolver#isReadOnly(ELContext, Object, Object)}.
     */
    @Test
    void testIsReadOnly() {
        ELContext elContext = createMock(ELContext.class);

        replay(elContext);
        Assertions.assertTrue(resolver.isReadOnly(elContext, null, "whatever"));
        verify(elContext);
    }

    /**
     * Tests {@link ScopeELResolver#isReadOnly(ELContext, Object, Object)}.
     */
    @Test
    void testIsReadOnlyNPE() {
        assertThrows(NullPointerException.class, () -> resolver.isReadOnly(null, null, "whatever"));
    }

    /**
     * Tests {@link ScopeELResolver#setValue(ELContext, Object, Object, Object)}.
     */
    @Test
    void testSetValue() {
        // Just to complete code coverage!
        resolver.setValue(null, null, null, null);
    }
}
