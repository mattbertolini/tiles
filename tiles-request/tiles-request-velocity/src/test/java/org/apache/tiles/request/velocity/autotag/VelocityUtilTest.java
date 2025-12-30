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
package org.apache.tiles.request.velocity.autotag;

import org.apache.velocity.context.InternalContextAdapter;
import org.apache.velocity.runtime.parser.node.ASTMap;
import org.apache.velocity.runtime.parser.node.Node;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

/**
 * Tests {@link VelocityUtil}.
 *
 * @version $Rev$ $Date$
 */
class VelocityUtilTest {

    /**
     * Test method for {@link VelocityUtil#getParameters(InternalContextAdapter, Node)}.
     */
    @Test
    void testGetParameters() {
        InternalContextAdapter context = createMock(InternalContextAdapter.class);
        Node node = createMock(Node.class);
        ASTMap astMap = createMock(ASTMap.class);
        Map<String, Object> params = createMock(Map.class);

        expect(node.jjtGetChild(0)).andReturn(astMap);
        expect(astMap.value(context)).andReturn(params);

        replay(context, node, astMap, params);
        assertSame(params, VelocityUtil.getParameters(context, node));
        verify(context, node, astMap, params);
    }

    /**
     * Test method for {@link VelocityUtil#getObject(Object, Object)}.
     */
    @Test
    void testGetObject() {
        assertEquals(new Integer(1), VelocityUtil.getObject(new Integer(1), new Integer(2)));
        assertEquals(new Integer(1), VelocityUtil.getObject(new Integer(1), null));
        assertEquals(new Integer(2), VelocityUtil.getObject(null, new Integer(2)));
        assertNull(VelocityUtil.getObject(null, null));
    }

}
