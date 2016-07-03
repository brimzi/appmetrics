package brimzi.appmetricslib;

import com.codahale.metrics.Metric;

import java.util.Map;

/**
 * Created by brimzy on 28/05/2016.
 */
interface Monitorable {

    Map<String, Metric> getAllMetrics();
    boolean turnOnMetrics();
    boolean turnOffMetrics();
}
