package eu.quanticol.moonlight.formula;

import eu.quanticol.moonlight.domain.Interval;
import eu.quanticol.moonlight.domain.IntervalDomain;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.monitoring.temporal.online.OnlineTemporalMonitoring;
import eu.quanticol.moonlight.util.MultiValuedTrace;
import eu.quanticol.moonlight.monitoring.TemporalMonitoring;
import eu.quanticol.moonlight.signal.*;
import eu.quanticol.moonlight.util.Pair;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * The formula of this example comes from:
 * https://doi.org/10.1007/978-3-642-39799-8_19
 */
class RoSIBerkeleyTest {
    private static final int X_SIGNAL = 0;
    private static final int Y_SIGNAL = 1;

    /* PROBLEM CONSTANTS */
    private static final int A = 6;
    private static final int B = 10;
    private static final int C = 14;

    /* TIME POINTS AT WHICH THE MONITORING WILL BE TESTED */
    private static final int T1 = 4;
    private static final int T2 = 8;
    private static final int T3 = 13;
    private static final int T4 = 19;
    private static final int T5 = 22;
    private static final int T_MAX = 24;

    @Disabled("Only meaningful for Online Monitoring")
    @Test
    void berkleyTestT2() {
        List<Pair<Integer, Interval>> xValues = new ArrayList<>();
        xValues.add(new Pair<>(1, new Interval(0, 4, true)));
        xValues.add(new Pair<>(2, new Interval(4, T2)));

        List<Pair<Integer, Interval>> yValues = new ArrayList<>();
        yValues.add(new Pair<>(-1, new Interval(0, 4, true)));
        yValues.add(new Pair<>(2, new Interval(4, T2)));

        assertEquals(new Interval(0).any(), test(T2, xValues, yValues));

    }

    @Disabled("Only meaningful for Online Monitoring")
    @Test
    void berkleyTestT3() {
        List<Pair<Integer, Interval>> xValues = new ArrayList<>();
        xValues.add(new Pair<>(1, new Interval(0, 4, true)));
        xValues.add(new Pair<>(2, new Interval(4, 8, true)));
        xValues.add(new Pair<>(-1, new Interval(8, T3)));

        List<Pair<Integer, Interval>> yValues = new ArrayList<>();
        yValues.add(new Pair<>(-1, new Interval(0, 4, true)));
        yValues.add(new Pair<>(2, new Interval(4, 8, true)));
        yValues.add(new Pair<>(-1, new Interval(8, T3)));

        assertEquals(new Interval(0).any(), test(T3, xValues, yValues));
    }

    //@Disabled("Under Investigation")
    @Test
    void berkleyTestT4() {
        List<Pair<Integer, Interval>> xValues = new ArrayList<>();
        xValues.add(new Pair<>(1, new Interval(0, 4, true)));
        xValues.add(new Pair<>(2, new Interval(4, 8, true)));
        xValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        xValues.add(new Pair<>(-2, new Interval(13, 19)));

        List<Pair<Integer, Interval>> yValues = new ArrayList<>();
        yValues.add(new Pair<>(-1, new Interval(0, 4, true)));
        yValues.add(new Pair<>(2, new Interval(4, 8, true)));
        yValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        yValues.add(new Pair<>(1, new Interval(13, T4)));

        assertEquals(new Interval(0).fromValue(-2.0), test(T4, xValues, yValues));
    }

    @Test
    void berkleyTestT5() {
        List<Pair<Integer, Interval>> xValues = new ArrayList<>();
        xValues.add(new Pair<>(1, new Interval(0, 4, true)));
        xValues.add(new Pair<>(2, new Interval(4, 8, true)));
        xValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        xValues.add(new Pair<>(-2, new Interval(13, 19, true)));
        xValues.add(new Pair<>(2, new Interval(19, T5)));

        List<Pair<Integer, Interval>> yValues = new ArrayList<>();
        yValues.add(new Pair<>(-1, new Interval(0, 4, true)));
        yValues.add(new Pair<>(2, new Interval(4, 8, true)));
        yValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        yValues.add(new Pair<>(1, new Interval(13, T5)));

        assertEquals(new Interval(0).fromValue(-2.0), test(T5, xValues, yValues));
    }

    @Test
    void berkleyTestTMax() {
        List<Pair<Integer, Interval>> xValues = new ArrayList<>();
        xValues.add(new Pair<>(1, new Interval(0, 4, true)));
        xValues.add(new Pair<>(2, new Interval(4, 8, true)));
        xValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        xValues.add(new Pair<>(-2, new Interval(13, 19, true)));
        xValues.add(new Pair<>(2, new Interval(19, 22, true)));
        xValues.add(new Pair<>(-1, new Interval(22, T_MAX)));

        List<Pair<Integer, Interval>> yValues = new ArrayList<>();
        yValues.add(new Pair<>(-1, new Interval(0, 4, true)));
        yValues.add(new Pair<>(2, new Interval(4, 8, true)));
        yValues.add(new Pair<>(-1, new Interval(8, 13, true)));
        yValues.add(new Pair<>(1, new Interval(13, T_MAX)));

        assertEquals(Interval.fromValue(-2.0), test(T_MAX, xValues, yValues));
    }

    /**
     * Actual parametric test runner.
     * @param traceLength length of the input trace
     * @param xValues values for the first input signal
     * @param yValues values for the second input signal
     * @return an Interval corresponding to the final result of the monitoring
     */
    private static Interval test(int traceLength,
                                 List<Pair<Integer, Interval>> xValues,
                                 List<Pair<Integer, Interval>> yValues) {
        try {
            // Signals generator...
            Signal<List<Comparable<?>>> trace = traceGenerator(traceLength,
                                                               xValues,
                                                               yValues);
            // Formula selection...
            Formula formula = testFormula();

            // Generate Monitors...
            Signal<Interval> m = monitor(formula, trace);

            return m.getIterator(true).value();
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /**
     * Runs a temporal monitor on the given input.
     * @param formula input formula to monitor
     * @param trace input data over which the formula is monitored
     * @return a signal corresponding to the result of the monitoring.
     */
    private static Signal<Interval> monitor(Formula formula,
                                            Signal<List<Comparable<?>>> trace) {
        //a is the atomic proposition: a >= 0
        HashMap<String,
                Function<Parameters, Function<List<Comparable<?>>, Interval>>>
                atoms = new HashMap<>();

        atoms.put("positiveX", ps -> trc ->
                                    toInterval((Integer) trc.get(X_SIGNAL)));
        atoms.put("positiveY", ps -> trc ->
                                    toInterval((Integer) trc.get(Y_SIGNAL)));

        OnlineTemporalMonitoring<List<Comparable<?>>, Interval> monitoring =
                new OnlineTemporalMonitoring<>(atoms, new IntervalDomain());

        Signal<Interval> monitor = monitoring.monitor(formula, null).monitor(trace);

        System.out.print(monitoring.debug());
        return monitor;
    }

    private static Formula testFormula() {
        Formula atomX = new AtomicFormula("positiveX");
        Formula atomY = new AtomicFormula("positiveY");

        return new GloballyFormula(
                    new OrFormula(new EventuallyFormula(atomX,
                                                   new Interval(B, C)),
                                  new NegationFormula(atomY)),
                    new Interval(0, A));

        //formula for simpler testing, invert the comments for real tests
        /*return new OrFormula(new EventuallyFormula(atomX,
                                                   new Interval(B, C)),
                             new NegationFormula(atomY));
                             */
    }

    /**
     * Given two list of values, generates a multivalued trace.
     * @param traceLength length of the trace
     * @param xValues values for the first coordinate
     * @param yValues values for the second coordinate
     * @return a MultiValuedTrace
     *
     * @see MultiValuedTrace
     */
    private static Signal<List<Comparable<?>>> traceGenerator(int traceLength,
                                      List<Pair<Integer, Interval>> xValues,
                                      List<Pair<Integer, Interval>> yValues) {
        Integer[][] xSignal = new Integer[1][traceLength]; // 1 location
        xSignal[0] = valuesFromIntervals(xValues);

        Integer[][] ySignal = new Integer[1][traceLength]; // 1 location
        ySignal[0] = valuesFromIntervals(yValues);

        MultiValuedTrace trace = new MultiValuedTrace(1, traceLength);
        trace.setDimension(xSignal, X_SIGNAL)
             .setDimension(ySignal, Y_SIGNAL)
             .initialize();

        return trace.getSignals().get(0);   //list discarded (only one location)
    }

    /**
     * Given a piecewise constant integer function,
     * returns a corresponding array of values, one for any integer.
     * @param function function piecewise definitions
     * @return Array of function values
     */
    private static Integer[] valuesFromIntervals(List<Pair<Integer, Interval>>
                                                                    function) {
        int end = (int) Math.round(function.get(function.size() - 1).getSecond().getEnd());

        Integer[] data = new Integer[end];

        for(int i = 0; i < end; i++) {
            for (Pair<Integer, Interval> piece : function) {
                if (piece.getSecond().contains((double) i)) {
                    data[i] = piece.getFirst();
                    break;
                }
            }
        }
        return data;
    }

    /**
     * Given a number, returns a singleton Interval containing it.
     * @param number the numeric value contained in the interval
     * @return an Interval containing only that number.
     */
    private static Interval toInterval(Number number) {
        return new Interval((Integer) number, (Integer) number);
    }
}