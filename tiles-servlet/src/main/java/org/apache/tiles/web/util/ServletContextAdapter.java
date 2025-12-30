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
package org.apache.tiles.web.util;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.EventListener;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterRegistration;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.Servlet;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRegistration;
import jakarta.servlet.SessionCookieConfig;
import jakarta.servlet.SessionTrackingMode;
import jakarta.servlet.descriptor.JspConfigDescriptor;

/**
 * Adapts a servlet config and a servlet context to become a unique servlet
 * context.
 *
 * @version $Rev$ $Date$
 */
public class ServletContextAdapter implements ServletContext {

    /**
     * The root context to use.
     */
    private final ServletContext rootContext;

    /**
     * The union of init parameters of {@link ServletConfig} and
     * {@link ServletContext}.
     */
    private final Hashtable<String, String> initParameters;


    /**
     * Constructor.
     *
     * @param config The servlet configuration object.
     */
    public ServletContextAdapter(ServletConfig config) {
        this.rootContext = config.getServletContext();
        initParameters = new Hashtable<>();
        Enumeration<String> enumeration = rootContext
                .getInitParameterNames();
        while (enumeration.hasMoreElements()) {
            String paramName = enumeration.nextElement();
            initParameters.put(paramName, rootContext
                    .getInitParameter(paramName));
        }
        enumeration = config.getInitParameterNames();
        while (enumeration.hasMoreElements()) {
            String paramName = enumeration.nextElement();
            initParameters.put(paramName, config.getInitParameter(paramName));
        }
    }

    /** {@inheritDoc} */
    public ServletContext getContext(String string) {
        return rootContext.getContext(string);
    }

    /** {@inheritDoc} */
    public int getMajorVersion() {
        return rootContext.getMajorVersion();
    }

    /** {@inheritDoc} */
    public int getMinorVersion() {
        return rootContext.getMinorVersion();
    }

    /** {@inheritDoc} */
    public String getMimeType(String string) {
        return rootContext.getMimeType(string);
    }

    /** {@inheritDoc} */
    public Set<String> getResourcePaths(String string) {
        return rootContext.getResourcePaths(string);
    }

    /** {@inheritDoc} */
    public URL getResource(String string) throws MalformedURLException {
        return rootContext.getResource(string);
    }

    /** {@inheritDoc} */
    public InputStream getResourceAsStream(String string) {
        return rootContext.getResourceAsStream(string);
    }

    /** {@inheritDoc} */
    public RequestDispatcher getRequestDispatcher(String string) {
        return rootContext.getRequestDispatcher(string);
    }

    /** {@inheritDoc} */
    public RequestDispatcher getNamedDispatcher(String string) {
        return rootContext.getNamedDispatcher(string);
    }

    /** {@inheritDoc} */
    public void log(String string) {
        rootContext.log(string);
    }

    /** {@inheritDoc} */
    public void log(Exception exception, String string) {
        rootContext.log(string, exception);
    }

    /** {@inheritDoc} */
    public void log(String string, Throwable throwable) {
        rootContext.log(string, throwable);
    }

    /** {@inheritDoc} */
    public String getRealPath(String string) {
        return rootContext.getRealPath(string);
    }

    /** {@inheritDoc} */
    public String getServerInfo() {
        return rootContext.getServerInfo();
    }

    /** {@inheritDoc} */
    public String getInitParameter(String string) {
        return initParameters.get(string);
    }

    /** {@inheritDoc} */
    public Enumeration<String> getInitParameterNames() {
        return initParameters.keys();
    }

    /** {@inheritDoc} */
    public Object getAttribute(String string) {
        return rootContext.getAttribute(string);
    }

    /** {@inheritDoc} */
    public Enumeration<String> getAttributeNames() {
        return rootContext.getAttributeNames();
    }

    /** {@inheritDoc} */
    public void setAttribute(String string, Object object) {
        rootContext.setAttribute(string, object);
    }

    /** {@inheritDoc} */
    public void removeAttribute(String string) {
        rootContext.removeAttribute(string);
    }

    /** {@inheritDoc} */
    public String getServletContextName() {
        return rootContext.getServletContextName();
    }

    /** {@inheritDoc} */
    public String getContextPath() {
        return rootContext.getContextPath();
    }

    @Override
    public int getEffectiveMajorVersion() {
        return rootContext.getEffectiveMajorVersion();
    }

    @Override
    public int getEffectiveMinorVersion() {
        return rootContext.getEffectiveMinorVersion();
    }

    @Override
    public boolean setInitParameter(String name, String value) {
        return rootContext.setInitParameter(name, value);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, String s1) {
        return rootContext.addServlet(s, s1);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Servlet servlet) {
        return rootContext.addServlet(s, servlet);
    }

    @Override
    public ServletRegistration.Dynamic addServlet(String s, Class<? extends Servlet> aClass) {
        return rootContext.addServlet(s, aClass);
    }

    @Override
    public ServletRegistration.Dynamic addJspFile(String s, String s1) {
        return rootContext.addJspFile(s, s1);
    }

    @Override
    public <T extends Servlet> T createServlet(Class<T> aClass) throws ServletException {
        return rootContext.createServlet(aClass);
    }

    @Override
    public ServletRegistration getServletRegistration(String s) {
        return rootContext.getServletRegistration(s);
    }

    @Override
    public Map<String, ? extends ServletRegistration> getServletRegistrations() {
        return rootContext.getServletRegistrations();
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, String s1) {
        return rootContext.addFilter(s, s1);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Filter filter) {
        return rootContext.addFilter(s, filter);
    }

    @Override
    public FilterRegistration.Dynamic addFilter(String s, Class<? extends Filter> aClass) {
        return rootContext.addFilter(s, aClass);
    }

    @Override
    public <T extends Filter> T createFilter(Class<T> aClass) throws ServletException {
        return rootContext.createFilter(aClass);
    }

    @Override
    public FilterRegistration getFilterRegistration(String s) {
        return rootContext.getFilterRegistration(s);
    }

    @Override
    public Map<String, ? extends FilterRegistration> getFilterRegistrations() {
        return rootContext.getFilterRegistrations();
    }

    @Override
    public SessionCookieConfig getSessionCookieConfig() {
        return rootContext.getSessionCookieConfig();
    }

    @Override
    public void setSessionTrackingModes(Set<SessionTrackingMode> set) {
        rootContext.setSessionTrackingModes(set);
    }

    @Override
    public Set<SessionTrackingMode> getDefaultSessionTrackingModes() {
        return rootContext.getDefaultSessionTrackingModes();
    }

    @Override
    public Set<SessionTrackingMode> getEffectiveSessionTrackingModes() {
        return rootContext.getEffectiveSessionTrackingModes();
    }

    @Override
    public void addListener(String s) {
        rootContext.addListener(s);
    }

    @Override
    public <T extends EventListener> void addListener(T t) {
        rootContext.addListener(t);
    }

    @Override
    public void addListener(Class<? extends EventListener> aClass) {
        rootContext.addListener(aClass);
    }

    @Override
    public <T extends EventListener> T createListener(Class<T> aClass) throws ServletException {
        return rootContext.createListener(aClass);
    }

    @Override
    public JspConfigDescriptor getJspConfigDescriptor() {
        return rootContext.getJspConfigDescriptor();
    }

    @Override
    public ClassLoader getClassLoader() {
        return rootContext.getClassLoader();
    }

    @Override
    public void declareRoles(String... strings) {
        rootContext.declareRoles(strings);
    }

    @Override
    public String getVirtualServerName() {
        return rootContext.getVirtualServerName();
    }

    @Override
    public int getSessionTimeout() {
        return rootContext.getSessionTimeout();
    }

    @Override
    public void setSessionTimeout(int i) {
        rootContext.setSessionTimeout(i);
    }

    @Override
    public String getRequestCharacterEncoding() {
        return rootContext.getRequestCharacterEncoding();
    }

    @Override
    public void setRequestCharacterEncoding(String s) {
        rootContext.setRequestCharacterEncoding(s);
    }

    @Override
    public String getResponseCharacterEncoding() {
        return rootContext.getResponseCharacterEncoding();
    }

    @Override
    public void setResponseCharacterEncoding(String s) {
        rootContext.setResponseCharacterEncoding(s);
    }
}
