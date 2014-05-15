package candle;

import indicators.AdxIndicator;
import metatrader.MetaTraderService;
import watch.Watch;

import java.util.List;

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
    public double startPrice;
    public double stopPrice;
    private MetaTraderService metaTraderService;
    public CandleEnum type;
    private double psar;
    private boolean psarReverseEvent;
    private Watch watch;
    private AdxIndicator adxIndicator;
    private boolean adxReverseEvent;
    private boolean rsiEvent;

    public Candle() {
    }

    public void start() {
        System.out.println("Candle start");
        startTime = System.currentTimeMillis();
        if (metaTraderService == null){
            metaTraderService = new MetaTraderService();
        }
        startPrice = metaTraderService.getPrice();
    }

    public void stop(long stopTime) {
        System.out.println("Candle stop");
        if (stopTime != 0){
            this.stopTime = stopTime;
        }else{
            this.stopTime = System.currentTimeMillis();
        }
        stopPrice = metaTraderService.getPrice();
        if (startPrice<stopPrice) {
            type = CandleEnum.BULLISH;
        }else if (startPrice>stopPrice){
            type = CandleEnum.BEARISH;
        }else {
            type = CandleEnum.NEUTRAL;
        }
        psar = metaTraderService.getPsar();
        psarReverseEvent = calculatePsarReverseEvent();
    }

    public long getStopTime() {
        return startTime + 1000 * TIME_UNIT;
    }

    public long lifeTime() {
        return stopTime-startTime;
    }

    public void setMetaTraderService(MetaTraderService metaTraderService) {
        this.metaTraderService = metaTraderService;
    }

    public double getPsar() {
        return psar;
    }

    public boolean calculatePsarReverseEvent() {
        boolean result = false;
        List<Candle> candles = watch.candles();
        if (candles.size()!=0){
            Candle prevCandle = candles.get(candles.size()-1);
            if (stopPrice != 0){
                if (this.type.equals(CandleEnum.BULLISH) && prevCandle.type.equals(CandleEnum.BEARISH)){
                    if (this.stopPrice - prevCandle.getPsar() > 0.0010){
                        result = true;
                    }
                };
                if (this.type.equals(CandleEnum.BEARISH) && prevCandle.type.equals(CandleEnum.BULLISH)){
                    if (prevCandle.getPsar() - this.stopPrice > 0.0010){
                        result = true;
                    }
                };
            }
        }

        return result;
    }

    public boolean isPsarReverseEvent() {
        return psarReverseEvent;
    }

    public void setPsarReverseEvent(boolean psarReverseEvent) {
        this.psarReverseEvent = psarReverseEvent;
    }

    public Watch getWatch() {
        return watch;
    }

    public void setWatch(Watch watch) {
        this.watch = watch;
    }

    public void setPsar(double psar) {
        this.psar = psar;
    }

    public void setType(CandleEnum type) {
        this.type = type;
    }

    public void setStopPrice(double stopPrice) {
        this.stopPrice = stopPrice;
    }

    public void setAdxIndicator(AdxIndicator adxIndicator) {
        this.adxIndicator = adxIndicator;
    }

    public AdxIndicator getAdxIndicator() {
        return adxIndicator;
    }

    public boolean calculateAdxReverseEvent() {
        List<Candle> candleList = watch.candles();
        AdxIndicator beforePrevCandleAdx = candleList.get(candleList.size()-2).getAdxIndicator();
        AdxIndicator prevCandleAdx = candleList.get(candleList.size()-1).getAdxIndicator();

        if (beforePrevCandleAdx.getNegativeDirectionalMovementIndicator() < prevCandleAdx.getNegativeDirectionalMovementIndicator()
            && beforePrevCandleAdx.getPositiveDirectionalMovementIndicator() > prevCandleAdx.getPositiveDirectionalMovementIndicator())
            return true;

        if (beforePrevCandleAdx.getNegativeDirectionalMovementIndicator() > prevCandleAdx.getNegativeDirectionalMovementIndicator()
                && beforePrevCandleAdx.getPositiveDirectionalMovementIndicator() < prevCandleAdx.getPositiveDirectionalMovementIndicator())
            return true;

        return false;
    }

    public void setAdxReverseEvent(boolean adxReverseEvent) {
        this.adxReverseEvent = adxReverseEvent;
    }

    public boolean isAdxReverseEvent() {
        return adxReverseEvent;
    }

    public double getStartPrice() {
        return startPrice;
    }

    public boolean isRsiEvent() {
        return rsiEvent;
    }

    public void setRsiEvent(boolean rsiEvent) {
        this.rsiEvent = rsiEvent;
    }
}

