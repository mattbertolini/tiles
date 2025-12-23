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

package org.apache.tiles.compat.definition.digester;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;

import org.apache.tiles.Attribute;
import org.apache.tiles.Definition;
import org.apache.tiles.definition.DefinitionsFactoryException;
import org.apache.tiles.definition.DefinitionsReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Tests the <code>org.apache.tiles.definition.digester.DigesterDefinitionsReader</code> class.
 *
 * @version $Rev$ $Date$
 */
class TestCompatibilityDigesterDefinitionsReader {

    /**
     * The logging object.
     */
    private final Logger log = LoggerFactory
            .getLogger(TestCompatibilityDigesterDefinitionsReader.class);

    /**
     * The definitions reader.
     */
    private DefinitionsReader reader;

    /** {@inheritDoc} */
    @BeforeEach
    void setUp() {
        reader = new CompatibilityDigesterDefinitionsReader();
    }

    /**
     * Tests the read method to read Tiles 1.1 files.
     *
     * @throws DefinitionsFactoryException If the definitions factory fails.
     * @throws IOException If an I/O exception happens.
     */
    @Test
    void testReadOldFormat() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/tiles-defs-1.1.xml");
        assertNotNull(configFile, "Config file not found");

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull(definitions, "Definitions not returned.");
        assertNotNull(definitions.get("doc.mainLayout"), "Couldn't find doc.mainLayout tile.");
        assertNotNull(definitions.get(
                "doc.mainLayout").getAttribute("title").getValue(), "Couldn't Find title attribute.");
        assertEquals("Tiles Library Documentation", definitions.get(
                        "doc.mainLayout").getAttribute("title").getValue(), "Incorrect Find title attribute.");
    }

    /**
     * Tests the read method to read Tiles 2.0 files.
     *
     * @throws IOException If an I/O exception happens.
     */
    @Test
    void testReadNewFormat() throws IOException {
        URL configFile = this.getClass().getClassLoader().getResource(
                "org/apache/tiles/config/tiles-defs-2.0.xml");
        assertNotNull(configFile, "Config file not found");

        InputStream source = configFile.openStream();
        Map<String, Definition> definitions = reader.read(source);

        assertNotNull(definitions, "Definitions not returned.");
        assertNotNull(definitions.get("doc.mainLayout"), "Couldn't find doc.mainLayout tile.");
        assertNotNull(definitions.get(
                "doc.mainLayout").getAttribute("title").getValue(), "Couldn't Find title attribute.");
        assertEquals("Tiles Library Documentation", definitions.get(
                        "doc.mainLayout").getAttribute("title").getValue(), "Incorrect Find title attribute.");
    }

    /**
     * Tests the read method under normal conditions for the new features in 2.1
     * version of the DTD.
     */
    @Test
    void testRead21Version() {
        try {
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
        } catch (Exception e) {
            fail("Exception reading configuration." + e);
        }
    }

    /**
     * Tests read with bad input source.
     */
    @Test
    void testBadSource() {
        try {
            // Read definitions.
            reader.read(new String("Bad Input"));
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
}
