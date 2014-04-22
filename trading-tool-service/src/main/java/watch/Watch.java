package watch;

import candle.Candle;
import metatrader.MetaTraderService;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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
    Candle candle;
    ScheduledExecutorService scheduledFuture;
    MetaTraderService metaTraderService;

    public Watch() {
        this.candles = new ArrayList<Candle>();
    }

    public void start() {
        System.out.println("watch start");
        scheduledFuture = Executors.newScheduledThreadPool(30);
        final Runnable indicatorsAnalyserTask =  new Runnable() {
            @Override
            public void run() {
                System.out.println("Another run");
                metaTraderService.getPsar();
            }
        };
        System.out.println("First run: " +  Math.round( (float)(firstRunTimeCalendar().getTime() - System.currentTimeMillis()))/1000 + " "+ (firstRunTimeCalendar().getTime() - System.currentTimeMillis()));
        final ScheduledFuture<?> taskHandler = scheduledFuture.scheduleAtFixedRate(indicatorsAnalyserTask,
                firstRunTimeCalendar().getTime() - System.currentTimeMillis(), 1000, TimeUnit.MILLISECONDS);

        timer = new Timer();
        timer.scheduleAtFixedRate(createTimerThread(), firstRunTimeCalendar(), TIME_UNIT * 1000);
    }

    private TimerTask createTimerThread() {
        return new TimerTask() {
            long startTime = System.currentTimeMillis();


            public void run() {
                long currentTime = System.currentTimeMillis();
                System.out.println("Watch thread is running after: "+ Math.round( (float)(currentTime - startTime)/1000));

                //it is null only at the beginning
                if (candle != null) {
                    candle.stop(0);
                }

                candle =  new Candle();
                candle.setMetaTraderService(metaTraderService);
                candle.start();

                candles.add(candle);
                startTime = currentTime;
            }
        };
    }

    private Date firstRunTimeCalendar() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, 1);
        return calendar.getTime();
    }

    public void stop() {
        if (timer != null){
            System.out.println("timer cancelled");
            timer.cancel();
            if (candle != null){
                candle.stop(candle.getStopTime());
            }
        }
        scheduledFuture.shutdown();
    }

    public List<Candle> candles() {
        return candles;
    }

    public void setMetaTraderService(MetaTraderService metaTraderService) {
        this.metaTraderService = metaTraderService;
    }
}

