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
        if (candles.size() != 0) {
            Candle prevCandle = candles.get(candles.size() - 1);
            if (checkDownTrend(prevCandle) && psar < startPrice && type.equals(CandleEnum.BULLISH)) {
                result = true;
            }
            if (checkUpTrend(prevCandle) && psar > startPrice  && type.equals(CandleEnum.BEARISH)){
                result = true;
            }
        }

        return result;
    }

    private boolean checkDownTrend(Candle prevCandle) {
        return prevCandle.getPsar() >= prevCandle.getStartPrice() && prevCandle.getPsar() >= prevCandle.getStopPrice();
    }

    private boolean checkUpTrend(Candle prevCandle) {
        return prevCandle.getPsar() <= prevCandle.getStartPrice() && prevCandle.getPsar() <= prevCandle.getStopPrice();
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

    public String calculateAdxReverseEvent() {
        List<Candle> candleList = watch.candles();
        AdxIndicator thirdLastCandleAdx = candleList.get(candleList.size()-3).getAdxIndicator();
        //AdxIndicator secondLastCandleAdx = candleList.get(candleList.size()-2).getAdxIndicator();
        AdxIndicator lastCandleAdx = candleList.get(candleList.size()-1).getAdxIndicator();

        if (thirdLastCandleAdx.getPositiveDirectionalMovementIndicator() - lastCandleAdx.getPositiveDirectionalMovementIndicator() > 7 &&
                lastCandleAdx.getNegativeDirectionalMovementIndicator() - thirdLastCandleAdx.getNegativeDirectionalMovementIndicator() > 7){
            return "bearish";
        }

        if (lastCandleAdx.getPositiveDirectionalMovementIndicator() - thirdLastCandleAdx.getPositiveDirectionalMovementIndicator() > 7 &&
                thirdLastCandleAdx.getNegativeDirectionalMovementIndicator() - lastCandleAdx.getNegativeDirectionalMovementIndicator()> 7){
            return "bullish";
        }

//        if (beforePrevCandleAdx.getNegativeDirectionalMovementIndicator() > prevCandleAdx.getNegativeDirectionalMovementIndicator()
//                && beforePrevCandleAdx.getPositiveDirectionalMovementIndicator() < prevCandleAdx.getPositiveDirectionalMovementIndicator())
//            return true;

        return null;
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

    public void setStartPrice(double startPrice) {
        this.startPrice = startPrice;
    }

    public double getStopPrice() {
        return stopPrice;
    }
}

