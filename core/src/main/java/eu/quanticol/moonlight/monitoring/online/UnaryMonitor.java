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

package eu.quanticol.moonlight.monitoring.online;

import eu.quanticol.moonlight.algorithms.online.BooleanComputation;
import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.domain.Interval;
import eu.quanticol.moonlight.domain.SignalDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.monitoring.temporal.online.LegacyOnlineTemporalMonitoring;
import eu.quanticol.moonlight.monitoring.temporal.online.OnlineTemporalMonitor;
import eu.quanticol.moonlight.signal.online.OnlineSignal;
import eu.quanticol.moonlight.signal.online.SignalInterface;
import eu.quanticol.moonlight.signal.online.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Strategy to interpret (online) an atomic predicate on the signal of interest.
 *
 * @param <V> Signal Trace Type
 * @param <R> Semantic Interpretation Semiring Type
 *
 * @see LegacyOnlineTemporalMonitoring
 * @see TemporalMonitor
 */
public class UnaryMonitor<V extends Comparable<V>, R extends Comparable<R>>
        implements OnlineMonitor<Double, V, AbstractInterval<R>>
{

    private final UnaryOperator<AbstractInterval<R>> atomicFunction;
    //private final Interval horizon;
    private final SignalInterface<Double, AbstractInterval<R>> rho;
    private final OnlineMonitor<Double, V, AbstractInterval<R>> argumentMonitor;


    /**
     * Prepares an atomic online (temporal) monitor.
     * @param atomicFunction The function evaluated by the atomic predicate
     * //@param parentHorizon The temporal horizon of the parent formula
     * @param interpretation The interpretation domain of interest
     */
    public UnaryMonitor(OnlineMonitor<Double, V, AbstractInterval<R>> argument,
                        UnaryOperator<AbstractInterval<R>> atomicFunction,
                         //Interval parentHorizon,
                        SignalDomain<AbstractInterval<R>> interpretation)
    {
        this.atomicFunction = atomicFunction;
        //this.horizon = parentHorizon;
        this.rho = new OnlineSignal<>(interpretation);
        this.argumentMonitor = argument;
    }

    @Override
    public List<Update<Double, AbstractInterval<R>>> monitor(
            Update<Double, V> signalUpdate)
    {
        List<Update<Double, AbstractInterval<R>>> argUpdates =
                                        argumentMonitor.monitor(signalUpdate);

        List<Update<Double, AbstractInterval<R>>> updates = new ArrayList<>();

        for(Update<Double, AbstractInterval<R>> argU : argUpdates) {
            updates.addAll(BooleanComputation.unary(argU, atomicFunction));
        }

        for(Update<Double, AbstractInterval<R>> u : updates) {
            rho.refine(u);
        }

        return updates;
    }

    @Override
    public SignalInterface<Double, AbstractInterval<R>> getResult() {
        return rho;
    }
}
