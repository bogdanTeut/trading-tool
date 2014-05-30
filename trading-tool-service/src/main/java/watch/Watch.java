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
                doAlgorithm();
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

                candle =  new Candle();
                candle.setWatch(Watch.this);
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

    protected void doAlgorithm() {
        metaTraderService.getCalled();
        if (candles().size() < 3) {
            return;
        }
        metaTraderService.getAdx(); //TO DO:  this only for the time being

        if (checkLastFewCandlesForPsarReverseEvent()){
            if (checkLastFewCandlesForAdxReverseEvent()){
                if (checkLastCandleForRsiEvent()){
                    metaTraderService.doOrder();
                }
            }
        }
    }

    private boolean checkLastFewCandlesForPsarReverseEvent() {
        for (Candle candle:candles.subList(candles.size()-3, candles.size()-1)){
            if (candle.isPsarReverseEvent()){
                return true;
            }
        }
        return false;
    }

    private boolean checkLastFewCandlesForAdxReverseEvent() {
        for (Candle candle:candles.subList(candles.size()-3, candles.size()-1)){
            if (candle.isAdxReverseEvent()){
                return true;
            }
        }
        return false;
    }

    private boolean checkLastCandleForRsiEvent() {
        Candle candle = candles.get(candles.size()-1);
        if (candle.isRsiEvent()){
            return true;
        }
        return false;
    }

    protected Date firstRunTimeCalendar() {
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

    public void setScheduledFuture(ScheduledExecutorService scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    public void setMetaTraderService(MetaTraderService metaTraderService) {
        this.metaTraderService = metaTraderService;
    }
}

