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

package org.apache.tiles.request.reflect;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Tests {@link CannotAccessMethodException}.
 *
 * @version $Rev$ $Date$
 */
class CannotAccessMethodExceptionTest {

    /**
     * Test method for {@link org.apache.tiles.CannotAccessMethodException#CannotAccessMethodException()}.
     */
    @Test
    void testCannotAccessMethodException() {
        CannotAccessMethodException exception = new CannotAccessMethodException();
        assertNull(exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test method for {@link CannotAccessMethodException#CannotAccessMethodException(String)}.
     */
    @Test
    void testCannotAccessMethodExceptionString() {
        CannotAccessMethodException exception = new CannotAccessMethodException("my message");
        assertEquals("my message", exception.getMessage());
        assertNull(exception.getCause());
    }

    /**
     * Test method for {@link CannotAccessMethodException#CannotAccessMethodException(Throwable)}.
     */
    @Test
    void testCannotAccessMethodExceptionThrowable() {
        Throwable cause = new Throwable();
        CannotAccessMethodException exception = new CannotAccessMethodException(cause);
        assertEquals(cause.toString(), exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

    /**
     * Test method for {@link CannotAccessMethodException#CannotAccessMethodException(String, Throwable)}.
     */
    @Test
    void testCannotAccessMethodExceptionStringThrowable() {
        Throwable cause = new Throwable();
        CannotAccessMethodException exception = new CannotAccessMethodException("my message", cause);
        assertEquals("my message", exception.getMessage());
        assertEquals(cause, exception.getCause());
    }

}
