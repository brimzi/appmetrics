import brimzi.appmetricslib.MetricMonitor;
import com.codahale.metrics.ConsoleReporter;
import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.Slf4jReporter;

import java.io.File;
import java.util.concurrent.TimeUnit;

/**
 * Created by brimzy on 03/07/2016.
 */
public class Example {

    public static void main(String[] args) throws Exception{

        ClassWithMetrics c =new ClassWithMetrics();

        MetricMonitor monitor = MetricMonitor.builder().setReportPeriod(2, TimeUnit.SECONDS)
                                            .enableConsoleReporter(ConsoleReporter.Builder::build)
                                            //.enableCsvReporter(b-> b.build(new File("logs")))
                                            //.enableJmxReporter(JmxReporter.Builder::build)
                                            //.enableSfl4jReporter(Slf4jReporter.Builder::build)
                                            .registerMonitorable(c)
                                            .build();
        monitor.startNewMonitoring();
        new Thread(()->System.out.println("Total: "+c.runStuff())).start();

        Thread.sleep(6000);
        System.out.println("\n\n\nSwitch off reporting");
        monitor.stopMonitoring();
        Thread.sleep(10000);
        System.out.println("Switch on reporting");
        monitor.startNewMonitoring();

    }
}
