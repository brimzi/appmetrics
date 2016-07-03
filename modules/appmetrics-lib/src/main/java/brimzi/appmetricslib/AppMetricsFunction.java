package brimzi.appmetricslib;

/**
 * Created by brimzy on 29/05/2016.
 */
@FunctionalInterface
public interface AppMetricsFunction<T> {

    T runFunction();
}
