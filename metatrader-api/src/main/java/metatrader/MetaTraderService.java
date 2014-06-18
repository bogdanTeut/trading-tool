package metatrader;

import indicators.AdxIndicator;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 18/06/14
 * Time: 15:49
 * To change this template use File | Settings | File Templates.
 */
public class MetaTraderService {


    private double price;
    private double psar;
    private AdxIndicator adx;
    private double rsi;

    public double getPrice() {
        return price;
    }

    public double getPsar() {
        return psar;
    }

    public AdxIndicator getAdx() {
        return new AdxIndicator(0, 0, 0);
    }

    public double getRsi() {
        return rsi;
    }

    public void doOrder() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public void getCalled() {
    }
}
