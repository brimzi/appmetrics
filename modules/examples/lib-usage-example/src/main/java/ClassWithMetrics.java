
import brimzi.appmetricslib.AppMetricType;
import brimzi.appmetricslib.BaseMonitorable;

/**
 * Created by brimzy on 03/07/2016.
 */
public class ClassWithMetrics extends BaseMonitorable {

    public ClassWithMetrics(){
        registerMetric("meter1", AppMetricType.METER);
        registerMetric("counter1", AppMetricType.COUNTER);
    }



    public long runStuff(){

        long total=0;
        try{
            for (int i = 0; i < 10000000; i++) {
                int x = runMeteredFunction("meter1",()->{
                    return 2*2;
                });
                total+=x;
                Thread.sleep(1000);
            }

        }catch(Exception e){e.printStackTrace();}

        return total;
    }
}
