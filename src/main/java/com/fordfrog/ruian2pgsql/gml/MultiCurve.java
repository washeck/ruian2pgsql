/**
 * Copyright 2012 Miroslav Šulc
 * Copyright 2012 Petr Morávek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.fordfrog.ruian2pgsql.gml;

import java.util.ArrayList;
import java.util.List;

/**
 * MultiCurve.
 *
 * @author fordfrog
 */
public class MultiCurve implements Geometry {

    /**
     * Segments of the curve.
     */
    private final List<Geometry> segments = new ArrayList<>(5);
    /**
     * SRID.
     */
    private Integer srid;

    @Override
    public Integer getSrid() {
        return srid;
    }

    /**
     * Setter for {@link #srid}.
     *
     * @param srid {@link #srid}
     */
    public void setSrid(final Integer srid) {
        this.srid = srid;
    }

    /**
     * Adds segment to the list of segments.
     *
     * @param segment segment
     */
    public void addSegment(final Geometry segment) {
        segments.add(segment);
    }

    @Override
    public String toWKT() {
        final StringBuilder sbString =
                new StringBuilder(segments.size() * 1_024);
        WKTUtils.appendSrid(sbString, srid);

        if (hasArc()) {
            sbString.append("MULTICURVE(");
        } else {
            sbString.append("MULTILINESTRING(");
        }

        boolean first = true;

        for (final Geometry segment : segments) {
            if (first) {
                first = false;
            } else {
                sbString.append(',');
            }

            sbString.append(segment.toWKT().replaceFirst("^LINESTRING", ""));
        }

        sbString.append(')');

        return sbString.toString();
    }

    /**
     * Checks whether the multiline has arc.
     *
     * @return true if the multiline has arc, otherwise false
     */
    public boolean hasArc() {
        for (final Geometry segment : segments) {
            if (segment instanceof CircularString) {
                return true;
            }
        }

        return false;
    }
}
