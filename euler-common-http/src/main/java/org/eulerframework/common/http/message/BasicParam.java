package org.eulerframework.common.http.message;

import org.eulerframework.common.http.Param;
import org.eulerframework.common.http.util.Args;

import java.util.Objects;

public class BasicParam implements Param {
    private final String name;
    private final String value;

    public BasicParam(String name, String value) {
        this.name = Args.notNull(name, "Name");
        this.value = value;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Param)) {
            return false;
        }

        Param that = (Param) o;
        return name.equals(that.getName()) && Objects.equals(value, that.getValue());
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Objects.hashCode(value);
        return result;
    }

    @Override
    public String toString() {
        // don't call complex default formatting for a simple toString

        if (this.value == null) {
            return name;
        }
        final int len = this.name.length() + 1 + this.value.length();
        final StringBuilder buffer = new StringBuilder(len);
        buffer.append(this.name);
        buffer.append("=");
        buffer.append(this.value);
        return buffer.toString();
    }
}
