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
        scheduledFuture = Executors.newScheduledThreadPool(2);
        final Runnable indicatorsAnalyserTask =  new Runnable() {
            @Override
            public void run() {
                System.out.println(Thread.currentThread().getName());
                metaTraderService.getAdx();
                metaTraderService.getRsi();
            }
        };

        final Runnable candleCreatorTask =  new Runnable() {
            long startTime = System.currentTimeMillis();
            @Override
            public void run() {
                long currentTime = System.currentTimeMillis();
                System.out.println("Watch thread is running after: "+ Math.round( (float)(currentTime - startTime)/1000));
                System.out.println(Thread.currentThread().getName());
                //it is null only at the beginning
                if (candle != null) {
                    candle.stop(0);
                }

                candle =  new Candle(Watch.this);
                candle.setMetaTraderService(metaTraderService);
                candle.start();

                candles.add(candle);
                startTime = currentTime;
            }
        };

        final ScheduledFuture<?> indicatorsTaskHandler = scheduledFuture.scheduleAtFixedRate(indicatorsAnalyserTask,
                firstRunTimeCalendar().getTime() - System.currentTimeMillis(), 1000, TimeUnit.MILLISECONDS);
        final ScheduledFuture<?> candlesTaskHandler = scheduledFuture.scheduleAtFixedRate(candleCreatorTask,
                firstRunTimeCalendar().getTime() - System.currentTimeMillis(), TIME_UNIT * 1000, TimeUnit.MILLISECONDS);

    }

    private Date firstRunTimeCalendar() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.MINUTE, 1);
        return calendar.getTime();
    }

    public void stop() {
            if (candle != null){
                candle.stop(candle.getStopTime());
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

