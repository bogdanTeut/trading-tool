package candle;

import metatrader.MetaTraderService;

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
}
