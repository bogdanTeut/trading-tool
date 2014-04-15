package candle;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 11/04/14
 * Time: 11:03
 * To change this template use File | Settings | File Templates.
 */
public class Candle {
    public long stopTime;
    public long startTime;
    public final int TIME_UNIT = 60;

    public Candle (){
        System.out.println("Candle start");
        startTime = System.currentTimeMillis();

    }

    public void stop() {
        System.out.println("Candle stop");
        stopTime = System.currentTimeMillis();
    }

    public void setStopTime() {
        stopTime = startTime + 1000 * TIME_UNIT;
    }

    public long lifeTime() {
        return stopTime-startTime;
    }
}
