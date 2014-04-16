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

    public void start() {
        System.out.println("Candle start");
        startTime = System.currentTimeMillis();
        if (metaTraderService == null){
            metaTraderService = new MetaTraderService();
        }
        startPrice = metaTraderService.getPrice();
    }

    public void stop() {
        System.out.println("Candle stop");
        stopTime = System.currentTimeMillis();
        stopPrice = metaTraderService.getPrice();
    }

    public void setStopTime() {
        stopTime = startTime + 1000 * TIME_UNIT;
    }

    public long lifeTime() {
        return stopTime-startTime;
    }

    public void setMetaTraderService(MetaTraderService metaTraderService) {
        this.metaTraderService = metaTraderService;
    }
}
