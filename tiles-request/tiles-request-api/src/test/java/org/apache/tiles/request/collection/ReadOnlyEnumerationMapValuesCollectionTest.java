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
package org.apache.tiles.request.collection;

import org.apache.tiles.request.attribute.HasKeys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ReadOnlyEnumerationMap#values()}.
 *
 * @version $Rev$ $Date$
 */
class ReadOnlyEnumerationMapValuesCollectionTest {
    /**
     * The extractor to use.
     */
    private HasKeys<Integer> extractor;

    /**
     * The map to test.
     */
    private ReadOnlyEnumerationMap<Integer> map;

    /**
     * The collection to test.
     */
    private Collection<Integer> coll;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        extractor = createMock(HasKeys.class);
        map = new ReadOnlyEnumerationMap<Integer>(extractor);
        coll = map.values();
    }

    /**
     * Tests {@link Collection#add(Object)}.
     */
    @Test
    void testAdd() {
        assertThrows(UnsupportedOperationException.class, () -> coll.add(null));
    }

    /**
     * Tests {@link Collection#addAll(Object)}.
     */
    @Test
    void testAddAll() {
        assertThrows(UnsupportedOperationException.class, () -> coll.addAll(null));
    }

    /**
     * Tests {@link Collection#clear(Object)}.
     */
    @Test
    void testClear() {
        assertThrows(UnsupportedOperationException.class, () -> coll.clear());
    }

    /**
     * Tests {@link Collection#contains(Object)}.
     */
    @Test
    void testContainsValue() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        assertTrue(coll.contains(2));
        verify(extractor, keys);
    }

    /**
     * Tests {@link Collection#contains(Object)}.
     */
    @Test
    void testContainsValueFalse() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        assertFalse(coll.contains(3));
        verify(extractor, keys);
    }

    /**
     * Tests {@link Collection#containsAll(Object)}.
     */
    @Test
    void testContainsAll() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        List<Integer> coll = new ArrayList<Integer>();
        coll.add(1);
        coll.add(2);
        assertTrue(this.coll.containsAll(coll));
        verify(extractor, keys);
    }

    /**
     * Tests {@link Collection#containsAll(Object)}.
     */
    @Test
    void testContainsAllFalse() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        List<Integer> coll = new ArrayList<Integer>();
        coll.add(3);
        assertFalse(this.coll.containsAll(coll));
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Collection#isEmpty()}.
     */
    @Test
    void testIsEmpty() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);

        replay(extractor, keys);
        assertFalse(coll.isEmpty());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Collection#iterator()}.
     */
    @Test
    void testIterator() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");

        expect(extractor.getValue("two")).andReturn(2);

        replay(extractor, keys);
        Iterator<Integer> entryIt = coll.iterator();
        assertTrue(entryIt.hasNext());
        assertEquals(new Integer(2), entryIt.next());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Collection#iterator()}.
     */
    @Test
    void testIteratorRemove() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);

        try {
            replay(extractor, keys);
            assertThrows(UnsupportedOperationException.class, () -> coll.iterator().remove());
        } finally {
            verify(extractor, keys);
        }
    }

    /**
     * Tests {@link Collection#remove(Object)}.
     */
    @Test
    void testRemove() {
        assertThrows(UnsupportedOperationException.class, () -> coll.remove(null));
    }

    /**
     * Tests {@link Collection#removeAll(java.util.Collection)}.
     */
    @Test
    void testRemoveAll() {
        assertThrows(UnsupportedOperationException.class, () -> coll.removeAll(null));
    }

    /**
     * Tests {@link Collection#retainAll(java.util.Collection)}.
     */
    @Test
    void testRetainAll() {
        assertThrows(UnsupportedOperationException.class, () -> coll.retainAll(null));
    }

    /**
     * Test method for {@link org.apache.tiles.request.collection.HeaderValuesMap#size()}.
     */
    @Test
    void testSize() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        replay(extractor, keys);
        assertEquals(2, coll.size());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Collection#toArray()}.
     */
    @Test
    void testToArray() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        Integer[] entryArray = new Integer[] {1, 2};

        replay(extractor, keys);
        assertArrayEquals(entryArray, coll.toArray());
        verify(extractor, keys);
    }

    /**
     * Test method for {@link Collection#toArray(Object[])}.
     */
    @Test
    void testToArrayTArray() {
        Enumeration<String> keys = createMock(Enumeration.class);

        expect(extractor.getKeys()).andReturn(keys);
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("one");
        expect(keys.hasMoreElements()).andReturn(true);
        expect(keys.nextElement()).andReturn("two");
        expect(keys.hasMoreElements()).andReturn(false);

        expect(extractor.getValue("one")).andReturn(1);
        expect(extractor.getValue("two")).andReturn(2);

        Integer[] entryArray = new Integer[] {1, 2};

        replay(extractor, keys);
        Integer[] realArray = new Integer[2];
        assertArrayEquals(entryArray, coll.toArray(realArray));
        verify(extractor, keys);
    }
}
