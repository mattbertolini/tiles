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

package org.apache.tiles.definition.digester;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.ListAttribute;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the <code>org.apache.tiles.definition.digester.DigesterDefinitionsReader</code> class.
 *
 * @version $Rev$ $Date$
 */
class TestDigesterDefinitionsReader {

    /**
     * The logging object.
     */
    private final Logger log = LoggerFactory
            .getLogger(TestDigesterDefinitionsReader.class);

    /**
     * The definitions reader.
     */
    private DigesterDefinitionsReader reader;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        reader = new DigesterDefinitionsReader();
    }

    /**
     * Tests the read method under normal conditions.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testRead() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/tiles-defs.xml");
        assertNotNull(configFile, "Config file not found");

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull(definitions, "Definitions not returned.");
        assertNotNull(definitions.get("doc.mainLayout"), "Couldn't find doc.mainLayout tile.");
        assertNotNull(definitions.get(
                "doc.mainLayout").getAttribute("title").getValue(), "Couldn't Find title attribute.");
        assertEquals("Tiles Library Documentation", definitions.get(
                        "doc.mainLayout").getAttribute("title").getValue(), "Incorrect Find title attribute.");

        Definition def = definitions.get("doc.role.test");
        assertNotNull(def, "Couldn't find doc.role.test tile.");
        Attribute attribute = def.getAttribute("title");
        assertNotNull(attribute
                .getValue(), "Couldn't Find title attribute.");
        assertEquals("myrole", attribute.getRole(), "Role 'myrole' expected");

        def = definitions.get("doc.listattribute.test");
        assertNotNull(def, "Couldn't find doc.listattribute.test tile.");
        attribute = def.getAttribute("items");
        assertNotNull(attribute, "Couldn't Find items attribute.");
        assertInstanceOf(ListAttribute.class, attribute, "The class of the attribute is not right");
        assertInstanceOf(List.class, attribute.getValue(), "The class of value of the attribute is not right");
    }


    /**
     * Tests the read method under normal conditions for the new features in 2.1
     * version of the DTD.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testRead21Version() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/tiles-defs-2.1.xml");
        assertNotNull(configFile, "Config file not found");

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull(definitions, "Definitions not returned.");
        Definition def = definitions.get("doc.cascaded.test");

        assertNotNull(def, "Couldn't find doc.role.test tile.");
        Attribute attribute = def.getLocalAttribute("title");
        assertNotNull(attribute, "Couldn't Find title local attribute.");
        attribute = def.getCascadedAttribute("title2");
        assertNotNull(attribute, "Couldn't Find title2 cascaded attribute.");
        attribute = def.getLocalAttribute("items1");
        assertNotNull(attribute, "Couldn't Find items1 local attribute.");
        attribute = def.getCascadedAttribute("items2");
        assertNotNull(attribute, "Couldn't Find items2 cascaded attribute.");

        def = definitions.get("test.nesting.definitions");
        assertNotNull(def, "Couldn't find test.nesting.definitions tile.");
        assertEquals("/layout.jsp", def.getTemplateAttribute().getValue());
        assertEquals("template", def.getTemplateAttribute().getRenderer());
        attribute = def.getAttribute("body");
        assertNotNull(attribute, "Couldn't Find body attribute.");
        assertEquals("definition",
                attribute.getRenderer(), "Attribute not of 'definition' type");
        assertNotNull(attribute.getValue(), "Attribute value null");
        String defName = attribute.getValue().toString();
        def = definitions.get(defName);
        assertNotNull(def, "Couldn't find " + defName + " tile.");

        def = definitions.get("test.nesting.list.definitions");
        assertNotNull(
                def, "Couldn't find test.nesting.list.definitions tile.");
        attribute = def.getAttribute("list");
        assertNotNull(attribute, "Couldn't Find list attribute.");
        assertInstanceOf(ListAttribute.class, attribute, "Attribute not of valid type");
        ListAttribute listAttribute = (ListAttribute) attribute;
        List<Attribute> list = listAttribute.getValue();
        assertEquals(1, list.size(), "The list is not of correct size");
        attribute = list.get(0);
        assertNotNull(attribute, "Couldn't Find element attribute.");
        assertEquals("definition",
                attribute.getRenderer(), "Attribute not of 'definition' type");
        assertNotNull(attribute.getValue(), "Attribute value null");
        defName = attribute.getValue().toString();
        def = definitions.get(defName);
        assertNotNull(def, "Couldn't find " + defName + " tile.");

        defName = "test.inherit.list.base";
        def = definitions.get(defName);
        assertNotNull(def, "Couldn't find " + defName + " tile.");
        defName = "test.inherit.list";
        def = definitions.get(defName);
        assertNotNull(def, "Couldn't find " + defName + " tile.");
        listAttribute = (ListAttribute) def.getAttribute("list");
        assertTrue(listAttribute.isInherit(), "This definition does not inherit its list attribute");
        defName = "test.noinherit.list";
        def = definitions.get(defName);
        listAttribute = (ListAttribute) def.getAttribute("list");
        assertFalse(listAttribute.isInherit(), "This definition inherits its list attribute");

        defName = "test.new.attributes";
        def = definitions.get(defName);
        assertNotNull(def, "Couldn't find " + defName + " tile.");
        Attribute templateAttribute = def.getTemplateAttribute();
        assertEquals("${my.expression}",
                templateAttribute.getExpressionObject().getExpression());
        assertEquals("mytype", templateAttribute.getRenderer());
        attribute = def.getAttribute("body");
        assertNotNull(attribute, "Couldn't Find body attribute.");
        assertEquals("${my.attribute.expression}", attribute
                .getExpressionObject().getExpression());
    }

    /**
     * Tests read with bad input source.
     */
    @Test
    void testBadSource() {
        try {
            // Read definitions.
            reader.read("Bad Input");
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            // correct.
            if (log.isDebugEnabled()) {
                log.debug("Exception caught, it is OK", e);
            }
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Tests read with bad XML source.
     */
    @Test
    void testBadXml() {
        try {
            URL configFile = this.getClass().getClassLoader().getResource(
                    "org/apache/tiles/config/malformed-defs.xml");
            assertNotNull(configFile, "Config file not found");

            InputStream source = configFile.openStream();
            reader.read(source);
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            // correct.
            if (log.isDebugEnabled()) {
                log.debug("Exception caught, it is OK", e);
            }
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Tests the validating input parameter.
     *
     * This test case enables Digester's validating property then passes in a
     * configuration file with invalid XML.
     */
    @Test
    void testValidatingParameter() {
        // Testing with default (validation ON).
        try {
            URL configFile = this.getClass().getClassLoader().getResource(
                    "org/apache/tiles/config/invalid-defs.xml");
            assertNotNull(configFile, "Config file not found");

            InputStream source = configFile.openStream();
            reader.setValidating(true);
            reader.read(source);
            fail("Should've thrown an exception.");
        } catch (DefinitionsFactoryException e) {
            // correct.
            if (log.isDebugEnabled()) {
                log.debug("Exception caught, it is OK", e);
            }
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }

        // Testing with validation OFF.
        try {
            setUp();
            URL configFile = this.getClass().getClassLoader().getResource(
                    "org/apache/tiles/config/invalid-defs.xml");
            assertNotNull(configFile, "Config file not found");

            InputStream source = configFile.openStream();
            reader.read(source);
        } catch (DefinitionsFactoryException e) {
            fail("Should not have thrown an exception." + e);
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Regression test for bug TILES-352.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    void testRegressionTiles352() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/defs_regression_TILES-352.xml");
        assertNotNull(configFile, "Config file not found");

        InputStream source = configFile.openStream();
        Map<String, Definition> name2defs = reader.read(source);
        source.close();
        Definition root = name2defs.get("root");
        Attribute attribute = root.getAttribute("body");
        Definition child = name2defs.get(attribute.getValue());
        ListAttribute listAttribute = (ListAttribute) child.getAttribute("list");
        List<Attribute> list = listAttribute.getValue();
        assertEquals("This is a value", (list.get(0)).getValue());
    }

    /**
     * Tests {@link DigesterDefinitionsReader#read(Object)}.
     */
    @Test
    void testReadNoSource() {
        assertNull(reader.read(null));
    }

    /**
     * Tests {@link DigesterDefinitionsReader#addDefinition(Definition)}.
     */
    @Test
    void testAddDefinitionNoName() {
        Definition def = new Definition();
        assertThrows(DigesterDefinitionsReaderException.class, () -> reader.addDefinition(def));
    }
}
