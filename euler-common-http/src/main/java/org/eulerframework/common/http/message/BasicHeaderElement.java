/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

package org.eulerframework.common.http.message;

import org.eulerframework.common.http.HeaderElement;
import org.eulerframework.common.http.Param;

import java.util.Arrays;
import java.util.Objects;

/**
 * Basic implementation of {@link HeaderElement}
 * <p>
 * CLAUSE: Most code of this class is copied from Apache HTTP.
 */
public class BasicHeaderElement implements HeaderElement {

    private final String name;
    private final String value;
    private final Param[] parameters;

    /**
     * Constructor with name, value and parameters.
     *
     * @param name       header element name
     * @param value      header element value. May be {@code null}
     * @param parameters header element parameters. May be {@code null}.
     *                   Parameters are copied by reference, not by value
     */
    public BasicHeaderElement(
            final String name,
            final String value,
            final Param[] parameters) {
        super();
        this.name = name;
        this.value = value;
        if (parameters != null) {
            this.parameters = parameters;
        } else {
            this.parameters = new Param[]{};
        }
    }

    /**
     * Constructor with name and value.
     *
     * @param name  header element name
     * @param value header element value. May be {@code null}
     */
    public BasicHeaderElement(final String name, final String value) {
        this(name, value, null);
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public Param[] getParameters() {
        return this.parameters.clone();
    }

    @Override
    public int getParameterCount() {
        return this.parameters.length;
    }

    @Override
    public Param getParameter(final int index) {
        // ArrayIndexOutOfBoundsException is appropriate
        return this.parameters[index];
    }

    @Override
    public Param getParameterByName(final String name) {
//        Args.notNull(name, "Name");
        Param found = null;
        for (final Param current : this.parameters) {
            if (current.getName().equalsIgnoreCase(name)) {
                found = current;
                break;
            }
        }
        return found;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof HeaderElement)) {
            return false;
        }

        HeaderElement that = (HeaderElement) o;
        return name.equals(that.getName())
                && Objects.equals(value, that.getValue())
                && Arrays.equals(parameters, that.getParameters());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Objects.hashCode(value);
        result = 31 * result + Arrays.hashCode(parameters);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder buffer = new StringBuilder();
        buffer.append(this.name);
        if (this.value != null) {
            buffer.append("=");
            buffer.append(this.value);
        }
        for (final Param parameter : this.parameters) {
            buffer.append("; ");
            buffer.append(parameter);
        }
        return buffer.toString();
    }
}

