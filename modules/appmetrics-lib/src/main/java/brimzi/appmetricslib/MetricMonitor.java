package brimzi.appmetricslib;

import com.codahale.metrics.*;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by brimzy on 28/05/2016.
 */
public class MetricMonitor {

    private MetricRegistry registry;
    private HashSet<Monitorable> monitorables;
    private Function<Slf4jReporter.Builder,Slf4jReporter> slf4jReporterBuild;
    private Function<ConsoleReporter.Builder,ConsoleReporter> consoleReporterBuild;
    private Function<CsvReporter.Builder,CsvReporter> csvReporterBuild;
    private Function<JmxReporter.Builder,JmxReporter> jmxReporterBuild;
    private ConsoleReporter consoleReporter;
    private Slf4jReporter slf4jReporter;
    private CsvReporter csvReporter;
    private JmxReporter jmxReporter;
    private long period;
    private TimeUnit unit;
    private boolean on=false;

    private MetricMonitor(Builder builder){

        this.registry = builder.registry;
        this.monitorables = builder.monitorables;

        this.consoleReporterBuild = builder.consoleReporterBuild;
        this.slf4jReporterBuild = builder.slf4jReporterBuild;
        this.csvReporterBuild = builder.csvReporterBuild;
        this.jmxReporterBuild= builder.jmxReporterBuild;
        this.monitorables = builder.monitorables;
        this.period = builder.period;
        this.unit = builder.unit;
    }

    public synchronized void startNewMonitoring(){

        if(!on){
            monitorables.forEach(m->{
                m.getAllMetrics().forEach((name,metric)->registry.register(name,metric));
                m.turnOnMetrics();
            });

            startReporters();
            on =true;
        }
    }

    public synchronized void stopMonitoring(){

        if(on){
            stopReporters();
            monitorables.forEach(Monitorable::turnOffMetrics);
            registry.removeMatching((name,metric)->true);//remove all
            on =false;
        }
    }

    private void stopReporters(){

        if(consoleReporter!=null){
            consoleReporter.stop();
        }

        if(slf4jReporter!=null){
            slf4jReporter.stop();
        }

        if(csvReporter!=null){
            csvReporter.stop();
        }

        if(jmxReporter!=null){
            jmxReporter.stop();
        }
    }

    private void startReporters(){

        initNewReporters();

        if(consoleReporter!=null){
            consoleReporter.start(period,unit);
        }

        if(slf4jReporter!=null){
            slf4jReporter.start(period,unit);
        }

        if(csvReporter!=null){
            csvReporter.start(period,unit);
        }

        if(jmxReporter!=null){
            jmxReporter.start();
        }
    }

    private void initNewReporters(){

        if(consoleReporterBuild!=null){
            consoleReporter=consoleReporterBuild.apply(ConsoleReporter.forRegistry(registry));
        }

        if(slf4jReporterBuild!=null){
            slf4jReporter=slf4jReporterBuild.apply(Slf4jReporter.forRegistry(registry));
        }

        if(csvReporterBuild!=null){
            csvReporter=csvReporterBuild.apply(CsvReporter.forRegistry(registry));
        }

        if(jmxReporterBuild!=null){
            jmxReporter=jmxReporterBuild.apply(JmxReporter.forRegistry(registry));
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder{

        private MetricRegistry registry;
        private HashSet<Monitorable> monitorables;
        private Function<Slf4jReporter.Builder,Slf4jReporter> slf4jReporterBuild;
        private Function<ConsoleReporter.Builder,ConsoleReporter> consoleReporterBuild;
        private Function<CsvReporter.Builder,CsvReporter> csvReporterBuild;
        private Function<JmxReporter.Builder,JmxReporter> jmxReporterBuild;
        private long period =30;
        private TimeUnit unit = TimeUnit.SECONDS;

        private Builder(){
            this.registry=new MetricRegistry();
            monitorables = new HashSet<>();
        }

        public Builder enableConsoleReporter(Function<ConsoleReporter.Builder,ConsoleReporter> buildOperation){

            this.consoleReporterBuild = buildOperation;
            return this;
        }

        public Builder enableCsvReporter(Function<CsvReporter.Builder,CsvReporter> buildOperation){

            this.csvReporterBuild= buildOperation;
            return this;
        }

        public Builder enableSfl4jReporter(Function<Slf4jReporter.Builder,Slf4jReporter> buildOperation){

            this.slf4jReporterBuild= buildOperation;
            return this;
        }

        public Builder enableJmxReporter(Function<JmxReporter.Builder,JmxReporter> buildOperation){

            this.jmxReporterBuild = buildOperation;
            return this;
        }

        public Builder setReportPeriod(long period,TimeUnit unit){

            this.period =period;
            this.unit=unit;
            return this;
        }

        public MetricMonitor build(){

            return new MetricMonitor(this);
        }

        public Builder registerMonitorable(Monitorable monitorable){

            monitorables.add(monitorable);
            return this;
        }

    }

}
