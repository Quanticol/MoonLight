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

package eu.quanticol.moonlight.algorithms.online;

import eu.quanticol.moonlight.signal.online.*;
import eu.quanticol.moonlight.space.DistanceStructure;
import eu.quanticol.moonlight.space.LocationService;
import eu.quanticol.moonlight.space.SpatialModel;
import eu.quanticol.moonlight.util.Pair;
import org.jetbrains.annotations.NotNull;

import static eu.quanticol.moonlight.signal.online.Update.asTimeChain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;

public class SpatialComputation
<T extends Comparable<T> & Serializable, S, R extends Comparable<R>>
{
    private final LocationService<T, S> locSvc;
    private final Function<SpatialModel<S>, DistanceStructure<S, ?>> dist;
    private final BiFunction<IntFunction<R>,
                             DistanceStructure<S, ?>,
                             List<R>> op;

    Pair<T, SpatialModel<S>> currSpace;
    Pair<T, SpatialModel<S>> nextSpace;

    public SpatialComputation(@NotNull LocationService<T, S> locationService,
                              Function<SpatialModel<S>,
                                       DistanceStructure<S, ?>> distance,
                              BiFunction<IntFunction<R>,
                                                  DistanceStructure<S, ?>,
                                                  List<R>> operator)
    {
        checkLocationServiceValidity(locationService);
        locSvc = locationService;
        dist = distance;
        op = operator;
    }

    private void checkLocationServiceValidity(LocationService<T, S> locSvc)
    {
        if (locSvc.isEmpty())
            throw new UnsupportedOperationException("The location Service " +
                                                    "must not be empty!");
    }

    public List<Update<T, List<R>>> computeUnary(Update<T, List<R>> u)
    {

        Iterator<Pair<T, SpatialModel<S>>> spaceItr = locSvc.times();

        T t = u.getStart();
        T tNext = u.getEnd();
        IntFunction<R> spatialSignal = i -> u.getValue().get(i);

        tNext = seekSpace(t, tNext, spaceItr);

        SpatialModel<S> sm = currSpace.getSecond();
        DistanceStructure<S, ?> f = dist.apply(sm);

        f.checkDistance(0, 0); //TODO: Done to force pre-computation of distance matrix

        return computeOp(t, tNext, f, spatialSignal, spaceItr);
    }

    private List<Update<T, List<R>>> computeOp(
            T t, T tNext,
            DistanceStructure<S, ?> f,
            IntFunction<R> spatialSignal,
            Iterator<Pair<T, SpatialModel<S>>> spaceItr)
    {
        List<Update<T, List<R>>> results = new ArrayList<>();

        results.add(new Update<>(t, tNext, op.apply(spatialSignal, f)));

        while (nextSpace != null &&
                nextSpace.getFirst().compareTo(tNext) < 0)
        {
            currSpace = nextSpace;
            t = currSpace.getFirst();
            nextSpace = getNext(spaceItr);
            if(nextSpace != null && !currSpace.equals(nextSpace)) {
                tNext = nextSpace.getFirst();
                f = dist.apply(currSpace.getSecond());
                results.add(new Update<>(t, tNext, op.apply(spatialSignal, f)));
            }
        }

        return results;
    }

    public TimeChain<T, List<R>> computeUnaryChain(TimeChain<T, List<R>> ups)
    {
        TimeChain<T, List<R>> results =  new TimeChain<>(ups.getEnd());

        for(int i = 0; i < ups.size(); i++) {
            Iterator<Pair<T, SpatialModel<S>>> spaceItr = locSvc.times();
            SegmentInterface<T, List<R>> up = ups.get(i);
            T t = up.getStart();
            T tNext = i != ups.size() - 1 ? ups.get(i + 1).getStart() : ups.getEnd();
            IntFunction<R> spatialSignal = j -> up.getValue().get(j);
            tNext = seekSpace(t, tNext, spaceItr);
            SpatialModel<S> sm = currSpace.getSecond();
            DistanceStructure<S, ?> f = dist.apply(sm);
            f.checkDistance(0, 0); //TODO: Done to force pre-computation of distance matrix
            computeOpChain(t, tNext, f, spatialSignal, spaceItr)
                    .forEach(results::add);
        }

        return results;
    }

    private TimeChain<T, List<R>> computeOpChain(
            T t, T tNext,
            DistanceStructure<S, ?> f,
            IntFunction<R> spatialSignal,
            Iterator<Pair<T, SpatialModel<S>>> spaceItr)
    {
        return asTimeChain(computeOp(t, tNext, f, spatialSignal, spaceItr));
    }

    private T seekSpace(T start, T end,
                        Iterator<Pair<T, SpatialModel<S>>> spaceItr)
    {
        currSpace = spaceItr.next();
        getNext(spaceItr);
        while(nextSpace != null && nextSpace.getFirst().compareTo(start) <= 0) {
            currSpace = nextSpace;
            nextSpace = getNext(spaceItr);
        }
        return fromNextSpaceOrCurrent(end);
    }

    private T fromNextSpaceOrCurrent(T fallback) {
        if(nextSpace != null)
            return nextSpace.getFirst();
        return fallback;
    }


    /**
     * Returns the next element if there is one, otherwise null
     * @param itr Location Service Iterator
     * @param <S> Spatial Domain
     * @return Next element of the Location Service
     */
    private static <T, S> Pair<T, SpatialModel<S>> getNext(
            Iterator<Pair<T, SpatialModel<S>>> itr)
    {
        return (itr.hasNext() ? itr.next() : null);
    }
}
