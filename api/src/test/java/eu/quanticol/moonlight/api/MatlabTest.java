package eu.quanticol.moonlight.api;

import eu.quanticol.moonlight.MoonLightScript;
import eu.quanticol.moonlight.TemporalScriptComponent;
import eu.quanticol.moonlight.monitoring.temporal.TemporalMonitor;
import eu.quanticol.moonlight.signal.Record;
import eu.quanticol.moonlight.signal.RecordHandler;
import eu.quanticol.moonlight.signal.Signal;
import eu.quanticol.moonlight.xtext.ScriptLoader;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;
import java.util.stream.IntStream;

class MatlabTest {

    @Test
    void name() {
//        TestMoonLightScript testMoonLightScript = new TestMoonLightScript();
//        TemporalScriptComponent<?> temporalScriptComponent = testMoonLightScript.selectDefaultTemporalComponent();
//        TemporalMonitor<Record, ?> monitor = temporalScriptComponent.getMonitor(new Object());
//        double[] times = IntStream.range(0, 10).mapToDouble(s ->s).toArray();
//        Object[][] array = new Object[times.length][2];
//        for (int i = 0; i < array.length; i++) {
//            array[i][0]=times[i];
//            array[i][1]=times[i];
//        }
//        Object[][] objects = temporalScriptComponent.monitorToObjectArray(times, array);
//        System.out.println();
    }

    @Test
    void name2() {
        Matlab.loadFromFile("C:\\Users\\Simone\\Documents\\git\\MoonLight\\api_resources\\testscript.mls");
        System.out.println();
    }

    @Test
    void name3() throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        MoonLightScript cityMonitor = Matlab.loadJavaClass("C:\\Users\\Simone\\Documents\\git\\MoonLight\\api_resources\\CityMonitor.java");
        System.out.println();
    }
}