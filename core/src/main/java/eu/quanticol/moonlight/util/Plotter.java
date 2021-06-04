package eu.quanticol.moonlight.util;

import eu.quanticol.moonlight.domain.AbstractInterval;
import eu.quanticol.moonlight.signal.online.SegmentInterface;
import eu.quanticol.moonlight.signal.online.TimeChain;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

/**
 * Utility class for plotting.
 * <p>
 * !! Requires python 3+ and Matplotlib in running environment
 * </p>
 */
public class Plotter {
    private final List<Thread> threads;

    public Plotter() {
        threads = new ArrayList<>();
    }

    public void waitActivePlots() {
        try {
            for (Thread t: threads) {
                t.join();
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public void plot(List<Double> data, String name, String label) {
        asyncShow(() -> plotSingle(data, name, label));
    }

    public void plot(List<Double> dataDown, List<Double> dataUp, String name) {
        asyncShow(() -> plotInterval(dataDown, dataUp, name));
    }

    public void plot(TimeChain<Double, AbstractInterval<Double>> data, String name) {
        asyncShow(() -> {
            List<Double> dataDown =
                    replaceInfinite(data.stream()
                                        .map(x -> x.getValue().getStart())
                                        .collect(Collectors.toList()));
            List<Double> dataUp =
                    replaceInfinite(data.stream()
                                        .map(x -> x.getValue().getEnd())
                                        .collect(Collectors.toList()));
            List<Double> times = data.stream()
                                     .map(SegmentInterface::getStart)
                                     .collect(Collectors.toList());

            plotInterval(dataDown, dataUp, name);
        });
    }

    private Plot createPlot(String name) {
        Plot plt = Plot.create();
        plt.xlabel("times");
        //plt.ylabel("robustness");
        plt.title(name);
        plt.legend();
        return plt;
    }

    private void addData(Plot plt, List<Double> data, String label) {
        plt.plot().add(data).label(label);
    }

    private void showPlot(Plot plt) {
        try {
            plt.show();
        } catch (PythonExecutionException | IOException e) {
            System.err.println("unable to plot!");
            e.printStackTrace();
        }
    }

    private void plotInterval(List<Double> dataDown, List<Double> dataUp,
                              String name)
    {
        Plot plt = createPlot(name);

        addData(plt, dataDown, "rho_down");
        addData(plt, dataUp, "rho_up");

        //plt.xticks(times);    // Experimental feature, still unreliable
        showPlot(plt);
    }

    private void plotSingle(List<Double> data, String name, String label) {
        Plot plt = createPlot(name);

        addData(plt, data, label);

        //plt.xticks(times);    // Experimental feature, still unreliable
        showPlot(plt);
    }

    private void asyncShow(Runnable r) {
        Thread t = new Thread(r) {{
            start();
        }};
        threads.add(t);
    }

    private static List<Double> replaceInfinite(List<Double> vs) {
        vs = vs.stream().map(v -> v.equals(Double.POSITIVE_INFINITY) ?  10 : v)
                .map(v -> v.equals(Double.NEGATIVE_INFINITY) ? -10 : v)
                .collect(Collectors.toList());
        return vs;
    }
}