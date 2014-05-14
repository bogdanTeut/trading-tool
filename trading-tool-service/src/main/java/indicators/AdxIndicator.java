package indicators;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 14/05/14
 * Time: 11:45
 * To change this template use File | Settings | File Templates.
 */
public class AdxIndicator {

    private double negativeDirectionalMovementIndicator;
    private double positiveDirectionalMovementIndicator;
    private double adxIndicator;

    public AdxIndicator(double negativeDirectionalMovementIndicator, double positiveDirectionalMovementIndicator, double adxIndicator) {
        this.negativeDirectionalMovementIndicator = negativeDirectionalMovementIndicator;
        this.positiveDirectionalMovementIndicator = positiveDirectionalMovementIndicator;
        this.adxIndicator = adxIndicator;
    }

    public double getNegativeDirectionalMovementIndicator() {
        return negativeDirectionalMovementIndicator;
    }

    public double getPositiveDirectionalMovementIndicator() {
        return positiveDirectionalMovementIndicator;
    }

    public double getAdxIndicator() {
        return adxIndicator;
    }
}
