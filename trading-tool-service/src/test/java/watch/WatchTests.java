package watch;

import candle.Candle;
import metatrader.MetaTraderService;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created with IntelliJ IDEA.
 * User: bogdan.teut
 * Date: 11/04/14
 * Time: 10:32
 * To change this template use File | Settings | File Templates.
 */
public class WatchTests {
    final int TIME_UNIT = 60;

    @Mock
    MetaTraderService metaTraderService;

    @InjectMocks
    Watch watch;

    @BeforeClass
    public void setUp(){
        MockitoAnnotations.initMocks(this);

        watch.start();
        try {
            Thread.currentThread().sleep(3 * TIME_UNIT * 1000);
        } catch (InterruptedException e) {
            System.out.println("Something nasty happened");
        }
        watch.stop();
    }

    @Test
    public void checkCandlesCreationEveryMinute(){

        List<Candle> candles =  watch.candles();
        Assert.assertNotNull (candles);
        Assert.assertEquals(candles.size(), 3);

        Candle candle1 = candles.get(0);
        Assert.assertNotNull (candle1);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle1.lifeTime())/1000));

        Candle candle2 = candles.get(1);
        Assert.assertNotNull (candle2);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle2.lifeTime())/1000));

        Candle candle3 = candles.get(2);
        Assert.assertNotNull (candle3);
        Assert.assertEquals(TIME_UNIT, Math.round( (float)(candle3.lifeTime())/1000));


    }

    @Test
    public void checkMetatraderServiceCalledEveryFewMillSecs(){

        verify(metaTraderService, times((int)timeInTermsOfMilliseconds()/1000+1)).getPsar();
    }

    private long timeInTermsOfMilliseconds() {
        Calendar calendar = new GregorianCalendar();
        calendar.set(Calendar.SECOND, 0);
        return 1000 * 2 * TIME_UNIT + System.currentTimeMillis() - calendar.getTimeInMillis() ;
    }

}
