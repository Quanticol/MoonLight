/*
 * MoonLight: a light-weight framework for runtime monitoring
 * Copyright (C) 2018-2021
 *
 * See the NOTICE file distributed with this work for additional information
 * regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.quanticol.moonlight.domain;

import eu.quanticol.moonlight.signal.DataHandler;

/**
 * @deprecated use the more general {@link AbsIntervalDomain}
 * Signal domain to support intervals.
 * Currently limited to intervals of doubles.
 *
 * @see Interval
 * @see SignalDomain
 */
@Deprecated
public class IntervalDomain implements SignalDomain<Interval> {
    private static final Interval NEGATIVE_INFINITY =
            new Interval(Double.NEGATIVE_INFINITY);
    private static final Interval POSITIVE_INFINITY =
            new Interval(Double.POSITIVE_INFINITY);
    private static final Interval TOTAL_INTERVAL =
            new Interval(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    @Override
    public Interval conjunction(Interval x, Interval y) {
        return new Interval(Math.min(x.getStart(), y.getStart()),
                Math.min(x.getEnd(), y.getEnd()));
    }

    @Override
    public Interval disjunction(Interval x, Interval y) {
        return new Interval(Math.max(x.getStart(), y.getStart()),
                            Math.max(x.getEnd(), y.getEnd()));
    }

    @Override
    public Interval negation(Interval x) {
        return new Interval(- x.getEnd(), - x.getStart());
    }

    @Override
    public Interval min() {
        return NEGATIVE_INFINITY;
    }

    @Override
    public Interval max() {
        return POSITIVE_INFINITY;
    }

    @Override
    public Interval any() {
        return TOTAL_INTERVAL;
    }

    @Override
    public DataHandler<Interval> getDataHandler() {
        return DataHandler.INTERVAL;
    }

    /* NOT IMPLEMENTED/USED METHODS */

    @Override
    public boolean equalTo(Interval x, Interval y) {
        return x.equals(y);
    }

    @Override
    public Interval valueOf(boolean b) {
        throw new UnsupportedOperationException("Not implemented as not used.");
    }

    @Override
    public Interval valueOf(double v) {
        throw new UnsupportedOperationException("Not implemented as not used.");
    }

    @Override
    public Interval computeLessThan(double v1, double v2) {
        throw new UnsupportedOperationException("Not implemented as not in " +
                                                "the original scope of the " +
                                                "class development.");
    }

    @Override
    public Interval computeLessOrEqualThan(double v1, double v2) {
        throw new UnsupportedOperationException("Not implemented as not in " +
                                                "the original scope of the " +
                                                "class development.");
    }

    @Override
    public Interval computeEqualTo(double v1, double v2) {
        throw new UnsupportedOperationException("Not implemented as not in " +
                                                "the original scope of the " +
                                                "class development.");
    }

    @Override
    public Interval computeGreaterThan(double v1, double v2) {
        throw new UnsupportedOperationException("Not implemented as not in " +
                                                "the original scope of the " +
                                                "class development.");
    }

    @Override
    public Interval computeGreaterOrEqualThan(double v1, double v2) {
        throw new UnsupportedOperationException("Not implemented as not in " +
                                                "the original scope of the " +
                                                "class development.");
    }
}
