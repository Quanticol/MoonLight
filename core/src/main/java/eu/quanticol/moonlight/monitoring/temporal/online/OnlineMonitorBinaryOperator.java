/*
 * MoonLight: a light-weight framework for runtime monitoring
 * Copyright (C) 2018
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

package eu.quanticol.moonlight.monitoring.temporal.online;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.Interval;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Signal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

/**
 * Strategy to interpret online a binary logic operator
 *
 * @param <T> Signal Trace Type
 * @param <R> Semantic Interpretation Semiring Type
 *
 * @see TemporalMonitor
 */
public class OnlineMonitorBinaryOperator<T, R>
        implements OnlineTemporalMonitor<T, R>
{
    private final TemporalMonitor<T, R> m1;
    private final BinaryOperator<R> op;
    private final TemporalMonitor<T, R> m2;
    private final Interval horizon;
    private final List<Signal<R>> worklist;


    public OnlineMonitorBinaryOperator(TemporalMonitor<T, R> m1,
                                       BinaryOperator<R> op,
                                       TemporalMonitor<T, R> m2,
                                       Interval parentHorizon)
    {
        this.m1 = m1;
        this.op = op;
        this.m2 = m2;
        horizon = parentHorizon;
        worklist = new ArrayList<>();
    }

    @Override
    public Signal<R> monitor(Signal<T> signal) {
        if(horizon.contains(signal.getEnd()) || worklist.isEmpty()) {
            //update result
            worklist.add(Signal.apply(m1.monitor(signal),
                                      op,
                                      m2.monitor(signal)));
            System.out.println("[DEBUG] Binary Operator worklist:");
            System.out.println(getWorklist().toString());
        }

        return worklist.get(worklist.size() - 1); //return last computed value
    }

    /**
     * @return the definition horizon of the formula
     */
    public Interval getHorizon() {
        return horizon;
    }

    //TODO: for debugging purposes mainly
    @Override
    public List<R> getWorklist() {
        List<R> lastValues = new ArrayList<>();
        for(Signal<R> item: worklist) {
            lastValues.add(item.valueAt(0));
        }

        return lastValues;
    }
}
