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
package org.apache.tiles.request;

import java.util.Collection;
import java.util.Locale;
import java.util.Map;


/**
 * Delegate for ease of customization.
 */
public class ApplicationContextWrapper implements ApplicationContext {

    /**
     * The original context.
     */
    private final ApplicationContext context;

    /**
     * Constructor.
     *
     * @param context The original context.
     */
    public ApplicationContextWrapper(ApplicationContext context) {
        this.context = context;
    }

    /**
     * Returns the wrapped application context.
     *
     * @return The wrapped application context.
     */
    public ApplicationContext getWrappedApplicationContext() {
        return context;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getApplicationScope() {
        return context.getApplicationScope();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getContext() {
        return context.getContext();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, String> getInitParams() {
        return context.getInitParams();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationResource getResource(String localePath) {
        return context.getResource(localePath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ApplicationResource getResource(ApplicationResource base, Locale locale) {
        return context.getResource(base, locale);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<ApplicationResource> getResources(String path) {
        return context.getResources(path);
    }
}
