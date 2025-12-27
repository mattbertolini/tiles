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

package org.apache.tiles.definition.dao;

import static org.easymock.EasyMock.*;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.tiles.Definition;
import org.apache.tiles.definition.DefinitionsReader;
import org.apache.tiles.definition.MockDefinitionsReader;
import org.apache.tiles.definition.digester.DigesterDefinitionsReader;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.ApplicationResource;
import org.apache.tiles.request.locale.URLApplicationResource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link LocaleUrlDefinitionDAO}.
 *
 * @version $Rev$ $Date$
 */
class LocaleUrlDefinitionDAOTest {

    /**
     * The object to test.
     */
    private LocaleUrlDefinitionDAO definitionDao;

    private ApplicationContext applicationContext;

    private ApplicationResource url1;

    private ApplicationResource url2;

    private ApplicationResource url3;

    private ApplicationResource setupUrl(String filename, Locale... locales) throws IOException {
        ApplicationResource url = new URLApplicationResource("org/apache/tiles/config/" + filename + ".xml", this
                .getClass().getClassLoader().getResource("org/apache/tiles/config/" + filename + ".xml"));
        assertNotNull(url, "Could not load " + filename + " file.");
        expect(applicationContext.getResource(url.getLocalePath())).andReturn(url).anyTimes();
        expect(applicationContext.getResource(url, Locale.ROOT)).andReturn(url).anyTimes();
        Map<Locale, ApplicationResource> localeResources = new HashMap<Locale, ApplicationResource>();
        for (Locale locale : locales) {
            ApplicationResource urlLocale = new URLApplicationResource("org/apache/tiles/config/" + filename + "_"
                    + locale.toString() + ".xml", this.getClass().getClassLoader()
                    .getResource("org/apache/tiles/config/" + filename + "_" + locale.toString() + ".xml"));
            assertNotNull(urlLocale, "Could not load " + filename + "_" + locale.toString() + " file.");
            localeResources.put(locale, urlLocale);
        }
        for (Locale locale : new Locale[] { Locale.CANADA_FRENCH, Locale.FRENCH, Locale.US, Locale.ENGLISH,
                Locale.CHINA, Locale.CHINESE }) {
            ApplicationResource urlLocale = localeResources.get(locale);
            expect(applicationContext.getResource(url, locale)).andReturn(urlLocale).anyTimes();
        }
        return url;
    }

    /** Sets up the test. */
    @BeforeEach
    void setUp() throws IOException {
        applicationContext = createMock(ApplicationContext.class);
        url1 = setupUrl("defs1", Locale.FRENCH, Locale.CANADA_FRENCH, Locale.US);
        url2 = setupUrl("defs2");
        url3 = setupUrl("defs3");
        replay(applicationContext);
        definitionDao = new LocaleUrlDefinitionDAO(applicationContext);
    }

    /**
     * Tests {@link LocaleUrlDefinitionDAO#getDefinition(String, Locale)}.
     */
    @Test
    void testGetDefinition() {
        List<ApplicationResource> sourceURLs = new ArrayList<ApplicationResource>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);
        assertNotNull(definitionDao.getDefinition("test.def1", null), "test.def1 definition not found.");
        assertNotNull(definitionDao.getDefinition("test.def2", null), "test.def2 definition not found.");
        assertNotNull(definitionDao.getDefinition("test.def3", null), "test.def3 definition not found.");
        assertNotNull(definitionDao.getDefinition("test.common", null), "test.common definition not found.");
        assertNotNull(definitionDao.getDefinition("test.common", Locale.US), "test.common definition in US locale not found.");
        assertNotNull(definitionDao.getDefinition("test.common", Locale.FRENCH), "test.common definition in FRENCH locale not found.");
        assertNotNull(definitionDao.getDefinition("test.common", Locale.CHINA), "test.common definition in CHINA locale not found.");
        assertNotNull(definitionDao.getDefinition("test.common.french", Locale.FRENCH), "test.common.french definition in FRENCH locale not found.");
        assertNotNull(definitionDao.getDefinition("test.common.french", Locale.CANADA_FRENCH), "test.common.french definition in CANADA_FRENCH locale not found.");
        assertNotNull(definitionDao.getDefinition("test.def.toextend", null), "test.def.toextend definition not found.");
        assertNotNull(definitionDao.getDefinition("test.def.overridden", null), "test.def.overridden definition not found.");
        assertNotNull(definitionDao.getDefinition("test.def.overridden", Locale.FRENCH), "test.def.overridden definition in FRENCH locale not found.");

        assertEquals("default", definitionDao.getDefinition("test.def1", null)
                .getAttribute("country").getValue(), "Incorrect default country value");
        assertEquals("US", definitionDao.getDefinition("test.def1", Locale.US)
                .getAttribute("country").getValue(), "Incorrect US country value");
        assertEquals("France", definitionDao
                .getDefinition("test.def1", Locale.FRENCH).getAttribute("country").getValue(), "Incorrect France country value");
        assertEquals("default",
                definitionDao.getDefinition("test.def1", Locale.CHINA).getAttribute("country").getValue(), "Incorrect Chinese country value (should be default)");
        assertEquals("default",
                definitionDao.getDefinition("test.def.overridden", null).getAttribute("country").getValue(), "Incorrect default country value");
        assertEquals("Definition to be overridden",
                definitionDao.getDefinition("test.def.overridden", null).getAttribute("title").getValue(), "Incorrect default title value");
        assertEquals("France",
                definitionDao.getDefinition("test.def.overridden", Locale.FRENCH).getAttribute("country").getValue(), "Incorrect France country value");
        assertNull(definitionDao.getDefinition("test.def.overridden", Locale.FRENCH)
                .getAttribute("title"), "Definition in French not found");
    }

    /**
     * Tests {@link LocaleUrlDefinitionDAO#getDefinitions(Locale)}.
     */
    @Test
    void testGetDefinitions() {
        List<ApplicationResource> sourceURLs = new ArrayList<ApplicationResource>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);

        Map<String, Definition> defaultDefinitions = definitionDao.getDefinitions(null);
        Map<String, Definition> usDefinitions = definitionDao.getDefinitions(Locale.US);
        Map<String, Definition> frenchDefinitions = definitionDao.getDefinitions(Locale.FRENCH);
        Map<String, Definition> chinaDefinitions = definitionDao.getDefinitions(Locale.CHINA);
        Map<String, Definition> canadaFrenchDefinitions = definitionDao.getDefinitions(Locale.CANADA_FRENCH);

        assertNotNull(defaultDefinitions.get("test.def1"), "test.def1 definition not found.");
        assertNotNull(defaultDefinitions.get("test.def2"), "test.def2 definition not found.");
        assertNotNull(defaultDefinitions.get("test.def3"), "test.def3 definition not found.");
        assertNotNull(defaultDefinitions.get("test.common"), "test.common definition not found.");
        assertNotNull(usDefinitions.get("test.common"), "test.common definition in US locale not found.");
        assertNotNull(frenchDefinitions.get("test.common"), "test.common definition in FRENCH locale not found.");
        assertNotNull(chinaDefinitions.get("test.common"), "test.common definition in CHINA locale not found.");
        assertNotNull(frenchDefinitions.get("test.common.french"), "test.common.french definition in FRENCH locale not found.");
        assertNotNull(canadaFrenchDefinitions.get("test.common.french"), "test.common.french definition in CANADA_FRENCH locale not found.");
        assertNotNull(defaultDefinitions.get("test.def.toextend"), "test.def.toextend definition not found.");
        assertNotNull(defaultDefinitions.get("test.def.overridden"), "test.def.overridden definition not found.");
        assertNotNull(frenchDefinitions.get("test.def.overridden"), "test.def.overridden definition in FRENCH locale not found.");

        assertEquals("default",
                defaultDefinitions.get("test.def1").getAttribute("country").getValue(), "Incorrect default country value");
        assertEquals("US", usDefinitions.get("test.def1").getAttribute("country")
                .getValue(), "Incorrect US country value");
        assertEquals("France",
                frenchDefinitions.get("test.def1").getAttribute("country").getValue(), "Incorrect France country value");
        assertEquals("default", chinaDefinitions
                .get("test.def1").getAttribute("country").getValue(), "Incorrect Chinese country value (should be default)");
        assertEquals("default", defaultDefinitions.get("test.def.overridden")
                .getAttribute("country").getValue(), "Incorrect default country value");
        assertEquals("Definition to be overridden",
                defaultDefinitions.get("test.def.overridden").getAttribute("title").getValue(), "Incorrect default title value");
        assertEquals("France", frenchDefinitions.get("test.def.overridden")
                .getAttribute("country").getValue(), "Incorrect France country value");
        assertNull(frenchDefinitions.get("test.def.overridden").getAttribute("title"), "Definition in French not found");
    }

    /**
     * Tests {@link LocaleUrlDefinitionDAO#setSources(List)}.
     */
    @Test
    void testSetSourceURLs() {
        List<ApplicationResource> sourceURLs = new ArrayList<ApplicationResource>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        assertEquals(sourceURLs, definitionDao.sources, "The source URLs are not set correctly");
    }

    /**
     * Tests {@link LocaleUrlDefinitionDAO#setReader(DefinitionsReader)}.
     */
    @Test
    void testSetReader() {
        DefinitionsReader reader = createMock(DefinitionsReader.class);
        replay(reader);
        definitionDao.setReader(reader);
        assertEquals(reader, definitionDao.reader, "There reader has not been set correctly");
        verify(reader);
    }

    /**
     * Tests execution.
     *
     * @throws IOException If something goes wrong.
     */
    @Test
    void testInit() throws IOException {
        ApplicationContext applicationContext = createMock(ApplicationContext.class);
        List<ApplicationResource> urlSet = new ArrayList<ApplicationResource>();
        urlSet.add(url1);
        expect(applicationContext.getResources("/WEB-INF/tiles.xml")).andReturn(urlSet);
        replay(applicationContext);
        DefinitionsReader reader = new DigesterDefinitionsReader();
        definitionDao.setReader(reader);
        List<ApplicationResource> sourceURLs = new ArrayList<ApplicationResource>();
        sourceURLs.add(url1);
        definitionDao.setSources(sourceURLs);
        assertEquals(DigesterDefinitionsReader.class,
                definitionDao.reader.getClass(), "The reader is not of the correct class");
        assertEquals(sourceURLs, definitionDao.sources, "The source URLs are not correct");
        reset(applicationContext);

        definitionDao.setReader(new MockDefinitionsReader());
        assertEquals(MockDefinitionsReader.class,
                definitionDao.reader.getClass(), "The reader is not of the correct class");
        sourceURLs = new ArrayList<ApplicationResource>();
        sourceURLs.add(url1);
        sourceURLs.add(url2);
        sourceURLs.add(url3);
        definitionDao.setSources(sourceURLs);
        assertEquals(sourceURLs, definitionDao.sources, "The source URLs are not correct");
    }
}
