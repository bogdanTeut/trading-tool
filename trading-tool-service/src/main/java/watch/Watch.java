package watch;

import candle.Candle;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 11/04/14
 * Time: 11:00
 * To change this template use File | Settings | File Templates.
 */
public class Watch {

    public final int TIME_UNIT = 60;
    List<Candle> candles;
    Timer timer;
    Candle candle = null;

    public Watch() {
        this.candles = new ArrayList<Candle>();
    }

    public void start() {
        System.out.println("watch start");
        int interval = TIME_UNIT * 1000; // 10 sec
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, 1);
        //Date timeToRun = new Date(System.currentTimeMillis() + interval);
        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            long startTime = System.currentTimeMillis();
            public void run() {
                if (candle != null) {
                    candle.stop();
                }
                candle =  new Candle();
                candles.add(candle);
                long currentTime = System.currentTimeMillis();
                System.out.println("run: "+ Math.round( (float)(currentTime - startTime)/1000));
                startTime = currentTime;
            }
        }, calendar.getTime(), interval);

    }

    public void stop() {
        if (timer != null){
            timer.cancel();
            candle.setStopTime();
            System.out.println("timer cancelled");
        }
    }

    public List<Candle> candles() {
        return candles;
    }
}

