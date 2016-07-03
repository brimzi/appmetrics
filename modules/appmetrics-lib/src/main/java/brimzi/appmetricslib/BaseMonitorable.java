package brimzi.appmetricslib;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import com.codahale.metrics.Counter;
import com.codahale.metrics.Meter;
import com.codahale.metrics.Metric;
import com.codahale.metrics.Timer;


/**
 * Created by brimzy on 29/05/2016.
 */
public class BaseMonitorable implements Monitorable {

    private AtomicBoolean metricsOn = new AtomicBoolean();
    private Map<String, AppMetricType> appMetrics = new HashMap<>();
    private Map<String, Metric> metricsMap = new HashMap<>();

    @Override
    public final Map<String, Metric> getAllMetrics() {

        turnOffMetrics();//just to make sure
        metricsMap = new HashMap<>();
        appMetrics.entrySet().stream().forEach(kv -> {
            final String name = kv.getKey();
            final Metric m = createMetric(name, kv.getValue());
            metricsMap.put(name, m);
        });

        return metricsMap;
    }

    @Override
    public final boolean turnOnMetrics() {

        return !metricsOn.getAndSet(true);
    }

    @Override
    public final boolean turnOffMetrics() {

        return metricsOn.getAndSet(false);
    }

    protected final void registerMetric(String name, AppMetricType metric) {

        appMetrics.put(getFullName(name), metric);
    }

    protected final <T> T runMeteredFunction(String meterName, AppMetricsFunction<T> op) throws Exception {
        T rtVal = null;

        Metric m = getMetric(meterName);
        if (m == null) {
            throw new Exception(String.format("No meter with name: %s found", meterName));
        }
        if (!(m instanceof Meter)) {
            throw new Exception("Specified name is not a meter");
        }

        Meter meter = (Meter) m;
        rtVal=op.runFunction();
        meter.mark();

        return rtVal;
    }

    protected final <T> T runTimedFunction(String timerName, AppMetricsFunction<T> op) throws Exception {
        final T rtVal;

        Metric m = getMetric(timerName);
        if (m == null) {
            throw new Exception(String.format("No timer with name: %s found", timerName));
        }
        if (!(m instanceof Timer)) {
            throw new Exception("Specified name is not a Timer");
        }

        Timer timer = (Timer) m;
        Timer.Context ctx = timer.time();
        rtVal = op.runFunction();
        ctx.stop();

        return rtVal;
    }

    protected final <T> T runCountedFunction(String timerName, AppMetricsFunction<T> op) throws Exception {
        final T rtVal;

        Metric m = getMetric(timerName);
        if (m == null) {
            throw new Exception(String.format("No counter with name: %s found", timerName));
        }
        if (!(m instanceof Counter)) {
            throw new Exception("Specified name is not a Counter");
        }

        Counter counter = (Counter) m;
        rtVal = op.runFunction();
        counter.inc();

        return rtVal;
    }

    private Metric getMetric(String name) {

        return metricsMap.get(getFullName(name));
    }

    private String getFullName(String name) {
        return this.getClass().getName()+"-"+name;
    }

    private Metric createMetric(String name, AppMetricType type) {
        final Metric m;
        switch (type) {
            case METER:
                m = new Meter();
                break;
            case COUNTER:
                m = new Counter();
                break;
            default:
                m = null;
        }

        return m;
    }
}
