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
package org.apache.tiles.ognl;

import java.util.Arrays;
import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import ognl.OgnlException;
import ognl.OgnlRuntime;
import ognl.PropertyAccessor;

import org.apache.tiles.Attribute;
import org.apache.tiles.Expression;
import org.apache.tiles.evaluator.EvaluationException;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link OGNLAttributeEvaluator}.
 *
 * @version $Rev$ $Date$$
 */
class OGNLAttributeEvaluatorTest {

    /**
     * The evaluator to test.
     */
    private OGNLAttributeEvaluator evaluator;

    /**
     * The request object to use.
     */
    private Request request;

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * Sets up the test.
     *
     * @throws OgnlException If something goes wrong.
     */
    @BeforeEach
    void setUp() throws OgnlException {
        PropertyAccessor objectPropertyAccessor = OgnlRuntime.getPropertyAccessor(Object.class);
        PropertyAccessor applicationContextPropertyAccessor =
            new NestedObjectDelegatePropertyAccessor<Request>(
                new TilesApplicationContextNestedObjectExtractor(),
                objectPropertyAccessor);
        PropertyAccessor anyScopePropertyAccessor = new AnyScopePropertyAccessor();
        PropertyAccessor scopePropertyAccessor = new ScopePropertyAccessor();
        PropertyAccessorDelegateFactory<Request> factory = new TilesContextPropertyAccessorDelegateFactory(
                objectPropertyAccessor, applicationContextPropertyAccessor,
                anyScopePropertyAccessor, scopePropertyAccessor);
        PropertyAccessor tilesRequestAccessor = new DelegatePropertyAccessor<Request>(factory);
        OgnlRuntime.setPropertyAccessor(Request.class, tilesRequestAccessor);
        evaluator = new OGNLAttributeEvaluator();
        Map<String, Object> requestScope = new HashMap<String, Object>();
        Map<String, Object> sessionScope = new HashMap<String, Object>();
        Map<String, Object> applicationScope = new HashMap<String, Object>();
        requestScope.put("object1", "value");
        sessionScope.put("object2", new Integer(1));
        applicationScope.put("object3", new Float(2.0));
        requestScope.put("paulaBean", new PaulaBean());
        request = createMock(Request.class);
        expect(request.getContext("request")).andReturn(requestScope)
                .anyTimes();
        expect(request.getContext("session")).andReturn(sessionScope)
                .anyTimes();
        expect(request.getContext("application")).andReturn(applicationScope)
                .anyTimes();
        expect(request.getAvailableScopes()).andReturn(
                Arrays.asList(new String[] { "request", "session", "application" })).anyTimes();
        applicationContext = createMock(ApplicationContext.class);
        expect(request.getApplicationContext()).andReturn(
                applicationContext).anyTimes();
        expect(applicationContext.getApplicationScope()).andReturn(
                applicationScope).anyTimes();
        replay(request, applicationContext);
    }

    /**
     * Tears down the test.
     */
    @AfterEach
    void tearDown() {
        verify(request, applicationContext);
    }

    /**
     * Tests
     * {@link OGNLAttributeEvaluator#evaluate(Attribute, Request)}.
     */
    @Test
    public void testEvaluate() {
        Attribute attribute = new Attribute();
        attribute.setExpressionObject(new Expression("requestScope.object1"));
        assertEquals("value", evaluator.evaluate(
                attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("sessionScope.object2"));
        assertEquals(new Integer(1), evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("applicationScope.object3"));
        assertEquals(new Float(2.0), evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("object1"));
        assertEquals("value", evaluator.evaluate(
                attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("object2"));
        assertEquals(new Integer(1), evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("object3"));
        assertEquals(new Float(2.0), evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("paulaBean.paula"));
        assertEquals("Brillant", evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setExpressionObject(new Expression("'String literal'"));
        assertEquals("String literal", evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setValue(new Integer(2));
        assertEquals(new Integer(2), evaluator
                .evaluate(attribute, request), "The value is not correct");
        attribute.setValue("object1");
        assertEquals("object1", evaluator
                .evaluate(attribute, request), "The value has been evaluated");
    }

    /**
     * Tests {@link OGNLAttributeEvaluator#evaluate(String, Request)}.
     */
    @Test
    void testEvaluateString() {
        String expression = "requestScope.object1";
        assertEquals("value", evaluator.evaluate(
                expression, request), "The value is not correct");
        expression = "sessionScope.object2";
        assertEquals(new Integer(1), evaluator
                .evaluate(expression, request), "The value is not correct");
        expression = "applicationScope.object3";
        assertEquals(new Float(2.0), evaluator
                .evaluate(expression, request), "The value is not correct");
        expression = "object1";
        assertEquals("value", evaluator.evaluate(
                expression, request), "The value is not correct");
        expression = "object2";
        assertEquals(new Integer(1), evaluator
                .evaluate(expression, request), "The value is not correct");
        expression = "object3";
        assertEquals(new Float(2.0), evaluator
                .evaluate(expression, request), "The value is not correct");
        expression = "paulaBean.paula";
        assertEquals("Brillant", evaluator
                .evaluate(expression, request), "The value is not correct");
        expression = "'String literal'";
        assertEquals("String literal", evaluator
                .evaluate(expression, request), "The value is not correct");
    }

    /**
     * Tests {@link OGNLAttributeEvaluator#evaluate(String, Request)}.
     */
    @Test
    void testEvaluateNull() {
        assertThrows(IllegalArgumentException.class, () -> evaluator.evaluate((String) null, request));
    }

    /**
     * Tests {@link OGNLAttributeEvaluator#evaluate(String, Request)}.
     */
    @Test
    void testEvaluateOgnlException() {
        assertThrows(EvaluationException.class, () -> evaluator.evaluate("wrong|||!!!!yes###", request));
    }

    /**
     * This is The Brillant Paula Bean (sic) just like it was posted to:
     * http://thedailywtf.com/Articles/The_Brillant_Paula_Bean.aspx I hope that
     * there is no copyright on it.
     */
    public static class PaulaBean {

        /**
         * Paula is brillant, really.
         */
        private String paula = "Brillant";

        /**
         * Returns brillant.
         *
         * @return "Brillant".
         */
        public String getPaula() {
            return paula;
        }
    }
}
