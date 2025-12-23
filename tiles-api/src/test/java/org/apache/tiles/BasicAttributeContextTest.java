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
package org.apache.tiles;

import static org.junit.jupiter.api.Assertions.*;
import static org.easymock.EasyMock.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

/**
 * Tests <code>BasicAttributeContext</code>.
 *
 * @version $Rev$ $Date$
 */
class BasicAttributeContextTest {

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext()}.
     */
    @Test
    void testBasicAttributeContext() {
        AttributeContext context = new BasicAttributeContext();
        assertNull(context
                .getLocalAttributeNames(), "There are some spurious attributes");
        assertNull(context
                .getCascadedAttributeNames(), "There are some spurious attributes");
    }

    /**
     * Tests {@link BasicAttributeContext#BasicAttributeContext(Map)}.
     */
    @Test
    void testBasicAttributeContextMapOfStringAttribute() {
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        AttributeContext context = new BasicAttributeContext(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(AttributeContext)}.
     */
    @Test
    void testBasicAttributeContextAttributeContext() {
        Set<String> localAttributes = new LinkedHashSet<String>();
        Set<String> cascadedAttributes = new LinkedHashSet<String>();
        localAttributes.add("local1");
        localAttributes.add("local2");
        cascadedAttributes.add("cascaded1");
        cascadedAttributes.add("cascaded2");
        AttributeContext toCopy = createMock(AttributeContext.class);
        expect(toCopy.getLocalAttributeNames()).andReturn(localAttributes);
        expect(toCopy.getLocalAttribute("local1")).andReturn(
                new Attribute("value1")).anyTimes();
        expect(toCopy.getLocalAttribute("local2")).andReturn(
                new Attribute("value2")).anyTimes();
        expect(toCopy.getCascadedAttributeNames())
                .andReturn(cascadedAttributes);
        expect(toCopy.getCascadedAttribute("cascaded1")).andReturn(
                new Attribute("value3")).anyTimes();
        expect(toCopy.getCascadedAttribute("cascaded2")).andReturn(
                new Attribute("value4")).anyTimes();
        Attribute templateAttribute = new Attribute("/template.jsp", Expression
                .createExpression("expression", null), "role1,role2",
                "template");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute);
        Set<String> roles = new HashSet<String>();
        roles.add("role1");
        roles.add("role2");
        expect(toCopy.getPreparer()).andReturn("my.preparer.Preparer");
        replay(toCopy);
        BasicAttributeContext context = new BasicAttributeContext(toCopy);
        assertEquals("/template.jsp", context.getTemplateAttribute().getValue(), "The template has not been set correctly");
        assertEquals("expression", context.getTemplateAttribute()
                        .getExpressionObject().getExpression(), "The template expression has not been set correctly");
        assertEquals(roles, context
                .getTemplateAttribute().getRoles(), "The roles are not the same");
        assertEquals("my.preparer.Preparer", context.getPreparer(), "The preparer has not been set correctly");
        Attribute attribute = context.getLocalAttribute("local1");
        assertNotNull(attribute, "Attribute local1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute local1 has not been set correctly");
        attribute = context.getLocalAttribute("local2");
        assertNotNull(attribute, "Attribute local2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute local2 has not been set correctly");
        attribute = context.getCascadedAttribute("cascaded1");
        assertNotNull(attribute, "Attribute cascaded1 not found");
        assertEquals("value3", attribute.getValue(), "Attribute cascaded1 has not been set correctly");
        attribute = context.getCascadedAttribute("cascaded2");
        assertNotNull(attribute, "Attribute cascaded2 not found");
        assertEquals("value4", attribute.getValue(), "Attribute cascaded2 has not been set correctly");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#BasicAttributeContext(BasicAttributeContext)}
     * .
     */
    @Test
    void testBasicAttributeContextBasicAttributeContext() {
        BasicAttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        Attribute templateAttribute = Attribute
                .createTemplateAttribute("/template.jsp");
        Set<String> roles = new HashSet<String>();
        roles.add("role1");
        roles.add("role2");
        templateAttribute.setRoles(roles);
        toCopy.setTemplateAttribute(templateAttribute);
        toCopy.setPreparer("my.preparer.Preparer");
        AttributeContext context = new BasicAttributeContext(toCopy);
        assertEquals("/template.jsp", context.getTemplateAttribute().getValue(), "The template has not been set correctly");
        assertEquals(roles, context
                .getTemplateAttribute().getRoles(), "The roles are not the same");
        assertEquals("my.preparer.Preparer", context.getPreparer(), "The preparer has not been set correctly");
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inheritCascadedAttributes(AttributeContext)}
     * .
     */
    @Test
    void testInheritCascadedAttributes() {
        AttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        AttributeContext context = new BasicAttributeContext();
        context.inheritCascadedAttributes(toCopy);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNull(attribute, "Attribute name1 found");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#inherit(BasicAttributeContext)}
     * testing inheritance between {@link ListAttribute} instances.
     */
    @Test
    void testInheritListAttribute() {
        AttributeContext toCopy = new BasicAttributeContext();
        ListAttribute parentListAttribute = new ListAttribute();
        Attribute first = new Attribute("first");
        Attribute second = new Attribute("second");
        parentListAttribute.add(first);
        toCopy.putAttribute("list", parentListAttribute);
        AttributeContext context = new BasicAttributeContext();
        ListAttribute listAttribute = new ListAttribute();
        listAttribute.setInherit(true);
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        ListAttribute result = (ListAttribute) context.getAttribute("list");
        assertNotNull(result, "The attribute must exist");
        List<Attribute> value = result.getValue();
        assertNotNull(value, "The list must exist");
        assertEquals(2, value.size(), "The size is not correct");
        assertEquals(first, value.get(0), "The first element is not correct");
        assertEquals(second, value
                .get(1), "The second element is not correct");

        context = new BasicAttributeContext();
        listAttribute = new ListAttribute();
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        result = (ListAttribute) context.getAttribute("list");
        assertNotNull(result, "The attribute must exist");
        value = result.getValue();
        assertNotNull(value, "The list must exist");
        assertEquals(1, value.size(), "The size is not correct");
        assertEquals(second, value
                .get(0), "The second element is not correct");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inheritCascadedAttributes(AttributeContext)}
     * .
     */
    @Test
    void testInherit() {
        AttributeContext toCopy = new BasicAttributeContext();
        Attribute parentTemplateAttribute = new Attribute();
        parentTemplateAttribute.setValue("/parent/template.jsp");
        toCopy.setTemplateAttribute(parentTemplateAttribute);
        toCopy.putAttribute("name1", new Attribute("value1"), true);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        toCopy.putAttribute("name3", new Attribute("value3"), false);
        toCopy.putAttribute("name4", new Attribute("value4"), false);
        AttributeContext context = new BasicAttributeContext();
        Attribute templateAttribute = new Attribute();
        templateAttribute.setRole("role1,role2");
        context.setTemplateAttribute(templateAttribute);
        context.putAttribute("name1", new Attribute("newValue1"), true);
        context.putAttribute("name3", new Attribute("newValue3"), false);
        context.inherit(toCopy);
        Attribute attribute = context.getTemplateAttribute();
        assertEquals("/parent/template.jsp", attribute.getValue());
        assertTrue(attribute.getRoles().contains("role1"));
        assertTrue(attribute.getRoles().contains("role2"));
        attribute = context.getCascadedAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("newValue1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getLocalAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("newValue3", attribute.getValue(), "Attribute name3 has not been set correctly");
        attribute = context.getLocalAttribute("name4");
        assertNotNull(attribute, "Attribute name4 not found");
        assertEquals("value4", attribute.getValue(), "Attribute name4 has not been set correctly");

        toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), true);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        toCopy.putAttribute("name3", new Attribute("value3"), false);
        toCopy.putAttribute("name4", new Attribute("value4"), false);
        context = new BasicAttributeContext();
        context.inherit(toCopy);
        attribute = context.getCascadedAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getLocalAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("value3", attribute.getValue(), "Attribute name3 has not been set correctly");
        attribute = context.getLocalAttribute("name4");
        assertNotNull(attribute, "Attribute name4 not found");
        assertEquals("value4", attribute.getValue(), "Attribute name4 has not been set correctly");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#inherit(AttributeContext)}
     * .
     */
    @Test
    void testInheritAttributeContext() {
        AttributeContext toCopy = createMock(AttributeContext.class);
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute);
        expect(toCopy.getPreparer()).andReturn("my.preparer");
        Set<String> cascadedNames = new HashSet<String>();
        cascadedNames.add("name1");
        cascadedNames.add("name2");
        expect(toCopy.getCascadedAttributeNames()).andReturn(cascadedNames);
        expect(toCopy.getCascadedAttribute("name1")).andReturn(new Attribute("value1"));
        expect(toCopy.getCascadedAttribute("name2")).andReturn(new Attribute("value2"));
        Set<String> names = new HashSet<String>();
        names.add("name3");
        names.add("name4");
        expect(toCopy.getLocalAttributeNames()).andReturn(names);
        expect(toCopy.getLocalAttribute("name3")).andReturn(new Attribute("value3"));
        expect(toCopy.getLocalAttribute("name4")).andReturn(new Attribute("value4"));

        replay(toCopy);
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("newValue1"), true);
        context.putAttribute("name3", new Attribute("newValue3"), false);
        context.inherit(toCopy);
        Attribute attribute = context.getCascadedAttribute("name1");
        assertEquals("/my/template.jsp", context.getTemplateAttribute().getValue());
        assertEquals("my.preparer", context.getPreparer());
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("newValue1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getLocalAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("newValue3", attribute.getValue(), "Attribute name3 has not been set correctly");
        attribute = context.getLocalAttribute("name4");
        assertNotNull(attribute, "Attribute name4 not found");
        assertEquals("value4", attribute.getValue(), "Attribute name4 has not been set correctly");
        verify(toCopy);
    }

    /**
     * Tests {@link BasicAttributeContext#inherit(AttributeContext)}
     * testing inheritance between {@link ListAttribute} instances.
     */
    @Test
    void testInheritAttributeContextListAttribute() {
        AttributeContext toCopy = createMock(AttributeContext.class);
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        expect(toCopy.getTemplateAttribute()).andReturn(templateAttribute).times(2);
        expect(toCopy.getPreparer()).andReturn("my.preparer").times(2);
        ListAttribute parentListAttribute = new ListAttribute();
        Attribute first = new Attribute("first");
        Attribute second = new Attribute("second");
        Attribute third = new Attribute("third");
        Attribute fourth = new Attribute("fourth");
        parentListAttribute.add(first);
        ListAttribute parentListAttribute2 = new ListAttribute();
        parentListAttribute2.add(third);
        Set<String> names = new HashSet<String>();
        names.add("list");
        Set<String> cascadedNames = new HashSet<String>();
        cascadedNames.add("list2");
        expect(toCopy.getCascadedAttributeNames()).andReturn(cascadedNames).times(2);
        expect(toCopy.getCascadedAttribute("list2")).andReturn(parentListAttribute2).times(2);
        expect(toCopy.getLocalAttributeNames()).andReturn(names).times(2);
        expect(toCopy.getLocalAttribute("list")).andReturn(parentListAttribute).times(2);

        replay(toCopy);
        AttributeContext context = new BasicAttributeContext();
        ListAttribute listAttribute = new ListAttribute();
        listAttribute.setInherit(true);
        listAttribute.add(second);
        context.putAttribute("list", listAttribute, false);
        ListAttribute listAttribute2 = new ListAttribute();
        listAttribute2.setInherit(true);
        listAttribute2.add(fourth);
        context.putAttribute("list2", listAttribute2, true);
        context.inherit(toCopy);
        ListAttribute result = (ListAttribute) context.getAttribute("list");
        assertNotNull(result, "The attribute must exist");
        List<Attribute> value = result.getValue();
        assertNotNull(value, "The list must exist");
        assertEquals(2, value.size(), "The size is not correct");
        assertEquals(first, value.get(0), "The first element is not correct");
        assertEquals(second, value
                .get(1), "The second element is not correct");
        result = (ListAttribute) context.getAttribute("list2");
        assertNotNull(result, "The attribute must exist");
        value = result.getValue();
        assertNotNull(value, "The list must exist");
        assertEquals(2, value.size(), "The size is not correct");
        assertEquals(third, value.get(0), "The first element is not correct");
        assertEquals(fourth, value
                .get(1), "The second element is not correct");

        context = new BasicAttributeContext();
        listAttribute = new ListAttribute();
        listAttribute.add(second);
        context.putAttribute("list", listAttribute);
        context.inherit(toCopy);
        result = (ListAttribute) context.getAttribute("list");
        assertNotNull(result, "The attribute must exist");
        value = result.getValue();
        assertNotNull(value, "The list must exist");
        assertEquals(1, value.size(), "The size is not correct");
        assertEquals(second, value
                .get(0), "The second element is not correct");
        verify(toCopy);
    }

    /**
     * Tests {@link BasicAttributeContext#addAll(Map)}.
     */
    @Test
    void testAddAll() {
        AttributeContext context = new BasicAttributeContext();
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        context.addAll(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");

        context.addAll(null);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");

        name2attrib = new HashMap<String, Attribute>();
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addAll(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#addMissing(Map)}.
     */
    @Test
    void testAddMissing() {
        Map<String, Attribute> name2attrib = new HashMap<String, Attribute>();
        Attribute attribute = new Attribute("Value 1");
        name2attrib.put("name1", attribute);
        attribute = new Attribute("Value 2");
        name2attrib.put("name2", attribute);
        AttributeContext context = new BasicAttributeContext(name2attrib);
        name2attrib.remove("name2");
        name2attrib.put("name1", new Attribute("Value 1a"));
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addMissing(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");

        context.addMissing(null);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");

        context = new BasicAttributeContext();
        name2attrib = new HashMap<String, Attribute>();
        name2attrib.put("name1", new Attribute("Value 1a"));
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addMissing(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1a", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");

        context = new BasicAttributeContext();
        context.putAttribute("name2", new Attribute("Value 2a"), true);
        name2attrib = new HashMap<String, Attribute>();
        name2attrib.put("name1", new Attribute("Value 1a"));
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addMissing(name2attrib);
        attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("Value 1a", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2a", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");

        context = new BasicAttributeContext();
        context.putAttribute("name2", new Attribute("Value 2a"), true);
        name2attrib = new HashMap<String, Attribute>();
        name2attrib.put("name2", new Attribute("Value 2b"));
        name2attrib.put("name3", new Attribute("Value 3"));
        context.addMissing(name2attrib);
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("Value 2a", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("Value 3", attribute.getValue(), "Attribute name3 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#getAttribute(String)}.
     */
    @Test
    void testGetAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("value3", attribute.getValue(), "Attribute name3 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttribute(String)}.
     */
    @Test
    void testGetLocalAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getLocalAttribute("name2");
        assertNull(attribute, "Attribute name2 found");
        attribute = context.getLocalAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("value3", attribute.getValue(), "Attribute name3 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttribute(String)}.
     */
    @Test
    void testGetCascadedAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Attribute attribute = context.getCascadedAttribute("name1");
        assertNull(attribute, "Attribute name1 found");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getCascadedAttribute("name3");
        assertNotNull(attribute, "Attribute name3 not found");
        assertEquals("value3a", attribute.getValue(), "Attribute name3 has not been set correctly");
    }

    /**
     * Tests {@link BasicAttributeContext#getLocalAttributeNames()}.
     */
    @Test
    void testGetLocalAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getLocalAttributeNames();
        assertTrue(names.contains("name1"), "Attribute name1 is not present");
        assertFalse(names.contains("name2"), "Attribute name2 is present");
        assertTrue(names.contains("name3"), "Attribute name3 is not present");
    }

    /**
     * Tests {@link BasicAttributeContext#getCascadedAttributeNames()}.
     */
    @Test
    void testGetCascadedAttributeNames() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.putAttribute("name3", new Attribute("value3a"), true);
        context.putAttribute("name3", new Attribute("value3"), false);
        Set<String> names = context.getCascadedAttributeNames();
        assertFalse(names.contains("name1"), "Attribute name1 is present");
        assertTrue(names.contains("name2"), "Attribute name2 is not present");
        assertTrue(names.contains("name3"), "Attribute name3 is not present");
    }

    /**
     * Tests {@link BasicAttributeContext#putAttribute(String, Attribute)}.
     */
    @Test
    void testPutAttributeStringAttribute() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"));
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name1");
        assertNull(attribute, "Attribute name1 found");
    }

    /**
     * Tests
     * {@link BasicAttributeContext#putAttribute(String, Attribute, boolean)}.
     */
    @Test
    void testPutAttributeStringAttributeBoolean() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        Attribute attribute = context.getLocalAttribute("name1");
        assertNotNull(attribute, "Attribute name1 not found");
        assertEquals("value1", attribute.getValue(), "Attribute name1 has not been set correctly");
        attribute = context.getCascadedAttribute("name1");
        assertNull(attribute, "Attribute name1 found");
        attribute = context.getCascadedAttribute("name2");
        assertNotNull(attribute, "Attribute name2 not found");
        assertEquals("value2", attribute.getValue(), "Attribute name2 has not been set correctly");
        attribute = context.getLocalAttribute("name2");
        assertNull(attribute, "Attribute name2 found");
    }

    /**
     * Tests {@link BasicAttributeContext#clear()}.
     */
    @Test
    void testClear() {
        AttributeContext context = new BasicAttributeContext();
        context.putAttribute("name1", new Attribute("value1"), false);
        context.putAttribute("name2", new Attribute("value2"), true);
        context.clear();
        Set<String> names = context.getLocalAttributeNames();
        assertTrue(names == null
                || names.isEmpty(), "There are local attributes");
        names = context.getCascadedAttributeNames();
        assertTrue(names == null
                || names.isEmpty(), "There are cascaded attributes");
    }

    /**
     * Tests {@link BasicAttributeContext#equals(Object)}.
     */
    @Test
    void testEquals() {
        BasicAttributeContext attributeContext = new BasicAttributeContext();
        attributeContext.setPreparer("my.preparer");
        attributeContext.setTemplateAttribute(Attribute.createTemplateAttribute("/my/template.jsp"));
        attributeContext.putAttribute("attribute1", new Attribute("value1"), true);
        attributeContext.putAttribute("attribute2", new Attribute("value2"), true);
        attributeContext.putAttribute("attribute3", new Attribute("value3"), false);
        BasicAttributeContext toCompare = new BasicAttributeContext(attributeContext);
        assertTrue(toCompare.equals(attributeContext));
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.putAttribute("attribute4", new Attribute("value4"), true);
        assertFalse(toCompare.equals(attributeContext));
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.putAttribute("attribute4", new Attribute("value4"), false);
        assertFalse(toCompare.equals(attributeContext));
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.setPreparer("another.preparer");
        assertFalse(toCompare.equals(attributeContext));
        toCompare = new BasicAttributeContext(attributeContext);
        toCompare.setTemplateAttribute(Attribute.createTemplateAttribute("/another/template.jsp"));
        assertFalse(toCompare.equals(attributeContext));
    }

    /**
     * Tests {@link BasicAttributeContext#hashCode()}.
     */
    @Test
    void testHashCode() {
        BasicAttributeContext attributeContext = new BasicAttributeContext();
        attributeContext.setPreparer("my.preparer");
        Attribute templateAttribute = Attribute.createTemplateAttribute("/my/template.jsp");
        attributeContext.setTemplateAttribute(templateAttribute);
        Attribute attribute1 = new Attribute("value1");
        Attribute attribute2 = new Attribute("value2");
        Attribute attribute3 = new Attribute("value3");
        attributeContext.putAttribute("attribute1", attribute1, true);
        attributeContext.putAttribute("attribute2", attribute2, true);
        attributeContext.putAttribute("attribute3", attribute3, false);
        Map<String, Attribute> cascadedAttributes = new HashMap<String, Attribute>();
        cascadedAttributes.put("attribute1", attribute1);
        cascadedAttributes.put("attribute2", attribute2);
        Map<String, Attribute> attributes = new HashMap<String, Attribute>();
        attributes.put("attribute3", attribute3);
        assertEquals(templateAttribute.hashCode() + "my.preparer".hashCode()
                + attributes.hashCode() + cascadedAttributes.hashCode(),
                attributeContext.hashCode());
    }

    /**
     * Tests {@link BasicAttributeContext} for the TILES-429 bug.
     */
    @Test
    void testTiles429() {
        AttributeContext toCopy = new BasicAttributeContext();
        toCopy.putAttribute("name1", new Attribute("value1"), false);
        toCopy.putAttribute("name2", new Attribute("value2"), true);
        List<Attribute> listOfObjects = new ArrayList<Attribute>();
        Attribute attribute1 = new Attribute(1);
        listOfObjects.add(attribute1);
        ListAttribute listAttribute = new ListAttribute(listOfObjects);
        listAttribute.setInherit(true);
        toCopy.putAttribute("name3", listAttribute);
        Attribute templateAttribute = Attribute
                .createTemplateAttribute("/template.jsp");
        Set<String> roles = new HashSet<String>();
        roles.add("role1");
        roles.add("role2");
        templateAttribute.setRoles(roles);
        toCopy.setTemplateAttribute(templateAttribute);
        toCopy.setPreparer("my.preparer.Preparer");
        AttributeContext context = new BasicAttributeContext(toCopy);
        Attribute attribute = context.getAttribute("name1");
        attribute.setValue("newValue1");
        attribute = context.getAttribute("name1");
        assertEquals("newValue1", attribute.getValue());
        attribute = toCopy.getAttribute("name1");
        assertEquals("value1", attribute.getValue());
        attribute = context.getAttribute("name3");
        assertTrue(attribute instanceof ListAttribute);
        assertTrue(((ListAttribute) attribute).isInherit());
    }
}
