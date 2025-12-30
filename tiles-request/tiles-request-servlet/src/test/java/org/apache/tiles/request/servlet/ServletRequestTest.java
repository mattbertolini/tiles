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
package org.apache.tiles.request.servlet;

import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.collection.HeaderValuesMap;
import org.apache.tiles.request.collection.ReadOnlyEnumerationMap;
import org.apache.tiles.request.collection.ScopeMap;
import org.apache.tiles.request.servlet.extractor.HeaderExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Locale;
import java.util.Map;

import static org.easymock.EasyMock.createMock;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests {@link ServletRequest}.
 *
 * @version $Rev$ $Date$
 */
class ServletRequestTest {

    /**
     * The application context.
     */
    private ApplicationContext applicationContext;

    /**
     * The request.
     */
    private HttpServletRequest request;

    /**
     * The response.
     */
    private HttpServletResponse response;

    /**
     * The request to test.
     */
    private ServletRequest req;

    /**
     * Sets up the test.
     */
    @BeforeEach
    void setUp() {
        applicationContext = createMock(ApplicationContext.class);
        request = createMock(HttpServletRequest.class);
        response = createMock(HttpServletResponse.class);
        req = new ServletRequest(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doForward(java.lang.String)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testDoForward() throws ServletException, IOException {
        RequestDispatcher rd = createMock(RequestDispatcher.class);

        expect(response.isCommitted()).andReturn(false);
        expect(request.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.forward(request, response);

        replay(applicationContext, request, response, rd);
        req.doForward("/my/path");
        verify(applicationContext, request, response, rd);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doForward(java.lang.String)}.
     */
    @Test
    void testDoForwardNoDispatcher() {
        expect(response.isCommitted()).andReturn(false);
        expect(request.getRequestDispatcher("/my/path")).andReturn(null);

        replay(applicationContext, request, response);
        try {
            assertThrows(IOException.class, () -> req.doForward("/my/path"));
        } finally {
            verify(applicationContext, request, response);
        }
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doForward(java.lang.String)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testDoForwardServletException() throws ServletException, IOException {
        RequestDispatcher rd = createMock(RequestDispatcher.class);

        expect(response.isCommitted()).andReturn(false);
        expect(request.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.forward(request, response);
        expectLastCall().andThrow(new ServletException());

        replay(applicationContext, request, response, rd);
        try {
            assertThrows(IOException.class, () -> req.doForward("/my/path"));
        } finally {
            verify(applicationContext, request, response, rd);
        }
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doForward(java.lang.String)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testDoForwardInclude() throws ServletException, IOException {
        RequestDispatcher rd = createMock(RequestDispatcher.class);

        expect(response.isCommitted()).andReturn(true);
        expect(request.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);

        replay(applicationContext, request, response, rd);
        req.doForward("/my/path");
        verify(applicationContext, request, response, rd);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doInclude(java.lang.String)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testDoInclude() throws IOException, ServletException {
        RequestDispatcher rd = createMock(RequestDispatcher.class);

        expect(request.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);

        replay(applicationContext, request, response, rd);
        req.doInclude("/my/path");
        verify(applicationContext, request, response, rd);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doInclude(java.lang.String)}.
     */
    @Test
    void testDoIncludeNoDispatcher() {
        expect(request.getRequestDispatcher("/my/path")).andReturn(null);

        replay(applicationContext, request, response);
        try {
            assertThrows(IOException.class, () -> req.doInclude("/my/path"));
        } finally {
            verify(applicationContext, request, response);
        }
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#doInclude(java.lang.String)}.
     * @throws IOException If something goes wrong.
     * @throws ServletException If something goes wrong.
     */
    @Test
    void testDoIncludeServletException() throws IOException, ServletException {
        RequestDispatcher rd = createMock(RequestDispatcher.class);

        expect(request.getRequestDispatcher("/my/path")).andReturn(rd);
        rd.include(request, response);
        expectLastCall().andThrow(new ServletException());

        replay(applicationContext, request, response, rd);
        try {
            assertThrows(IOException.class, () -> req.doInclude("/my/path"));
        } finally {
            verify(applicationContext, request, response, rd);
        }
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getHeader()}.
     */
    @Test
    void testGetHeader() {
        assertInstanceOf(ReadOnlyEnumerationMap.class, req.getHeader());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getHeader()}.
     */
    @Test
    void testGetResponseHeaders() {
        assertInstanceOf(HeaderExtractor.class, req.getResponseHeaders());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getHeaderValues()}.
     */
    @Test
    void testGetHeaderValues() {
        assertInstanceOf(HeaderValuesMap.class, req.getHeaderValues());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getParam()}.
     */
    @Test
    void testGetParam() {
        assertInstanceOf(ReadOnlyEnumerationMap.class, req.getParam());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getParamValues()}.
     */
    @Test
    void testGetParamValues() {
        Map<String, String[]> paramMap = createMock(Map.class);

        expect(request.getParameterMap()).andReturn(paramMap);

        replay(applicationContext, request, response, paramMap);
        assertEquals(paramMap, req.getParamValues());
        verify(applicationContext, request, response, paramMap);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getRequestScope()}.
     */
    @Test
    void testGetRequestScope() {
        assertInstanceOf(ScopeMap.class, req.getRequestScope());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getSessionScope()}.
     */
    @Test
    void testGetSessionScope() {
        assertInstanceOf(ScopeMap.class, req.getSessionScope());
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getOutputStream()}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testGetOutputStream() throws IOException {
        ServletOutputStream os = createMock(ServletOutputStream.class);

        expect(response.getOutputStream()).andReturn(os);

        replay(applicationContext, request, response, os);
        assertEquals(req.getOutputStream(), os);
        verify(applicationContext, request, response, os);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getWriter()}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testGetWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(response.getWriter()).andReturn(os);

        replay(applicationContext, request, response, os);
        assertEquals(req.getWriter(), os);
        verify(applicationContext, request, response, os);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getPrintWriter()}.
     * @throws IOException If something goes wrong.
     */
    @Test
    void testGetPrintWriter() throws IOException {
        PrintWriter os = createMock(PrintWriter.class);

        expect(response.getWriter()).andReturn(os);

        replay(applicationContext, request, response, os);
        assertEquals(req.getPrintWriter(), os);
        verify(applicationContext, request, response, os);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#isResponseCommitted()}.
     */
    @Test
    void testIsResponseCommitted() {
        expect(response.isCommitted()).andReturn(true);

        replay(applicationContext, request, response);
        assertTrue(req.isResponseCommitted());
        verify(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#setContentType(java.lang.String)}.
     */
    @Test
    void testSetContentType() {
        response.setContentType("text/html");

        replay(applicationContext, request, response);
        req.setContentType("text/html");
        verify(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getRequestLocale()}.
     */
    @Test
    void testGetRequestLocale() {
        Locale locale = Locale.ITALY;

        expect(request.getLocale()).andReturn(locale);

        replay(applicationContext, request, response);
        assertEquals(locale, req.getRequestLocale());
        verify(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getRequest()}.
     */
    @Test
    void testGetRequest() {
        replay(applicationContext, request, response);
        assertEquals(request, req.getRequest());
        verify(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#getResponse()}.
     */
    @Test
    void testGetResponse() {
        replay(applicationContext, request, response);
        assertEquals(response, req.getResponse());
        verify(applicationContext, request, response);
    }

    /**
     * Test method for {@link org.apache.tiles.request.servlet.ServletRequest#isUserInRole(java.lang.String)}.
     */
    @Test
    void testIsUserInRole() {
        expect(request.isUserInRole("myrole")).andReturn(true);

        replay(applicationContext, request, response);
        assertTrue(req.isUserInRole("myrole"));
        verify(applicationContext, request, response);
    }

}
