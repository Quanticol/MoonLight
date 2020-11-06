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

package eu.quanticol.moonlight.monitoring.spatialtemporal.online;

import eu.quanticol.moonlight.algorithms.ReachOperator;
import eu.quanticol.moonlight.domain.Interval;
import eu.quanticol.moonlight.domain.SignalDomain;
import eu.quanticol.moonlight.monitoring.spatialtemporal.SpatialTemporalMonitor;
import eu.quanticol.moonlight.signal.DistanceStructure;
import eu.quanticol.moonlight.signal.LocationService;
import eu.quanticol.moonlight.signal.SpatialModel;
import eu.quanticol.moonlight.signal.SpatialTemporalSignal;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Strategy to interpret the Reach spatial logic operator.
 *
 * @param <S> Spatial Graph Edge Type
 * @param <T> Signal Trace Type
 * @param <R> Semantic Interpretation Semiring Type
 *
 * @see SpatialTemporalMonitor
 */
public class OnlineSTMonitorReachOperator<S, T, R>
        implements SpatialTemporalMonitor<S, T, R>
{
    private final SpatialTemporalMonitor<S, T, R> m1;
    private final Function<SpatialModel<S>, DistanceStructure<S, ?>> distance;
    private final SpatialTemporalMonitor<S, T, R> m2;
    private final SignalDomain<R> domain;
    private final Interval horizon;
    private double signalEnd = 0;
    private final List<SpatialTemporalSignal<R>> worklist;

    public OnlineSTMonitorReachOperator(SpatialTemporalMonitor<S, T, R> m1,
                                        Function<SpatialModel<S>,
                                        DistanceStructure<S, ?>> distance,
                                        SpatialTemporalMonitor<S, T, R> m2,
                                        SignalDomain<R> domain,
                                        Interval parentHorizon)
    {
        this.m1 = m1;
        this.distance = distance;
        this.m2 = m2;
        this.domain = domain;
        this.horizon = parentHorizon;
        this.worklist = new ArrayList<>();
    }

    @Override
    public SpatialTemporalSignal<R> monitor(LocationService<S> locServ,
                                            SpatialTemporalSignal<T> signal)
    {
        //if(horizon.contains(signalEnd) || worklist.isEmpty()) {
        //update result
        worklist.add(ReachOperator.computeDynamic(locServ, distance, domain,
                                                  m1.monitor(locServ, signal),
                                                  m2.monitor(locServ, signal)));
        //System.out.println("[DEBUG] Binary Operator worklist:");
        //System.out.println(getWorklist().toString());
        //}

        signalEnd =  signal.end();
        return worklist.get(worklist.size() - 1); //return last computed value
    }

}