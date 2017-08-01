package com.haizhi.np.dispatch.schedule;

import com.haizhi.np.dispatch.constants.Format;
import com.mongodb.DBObject;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.spi.CalendarDataProvider;

/**
 * Created by haizhi on 2017/7/20.
 */
public class MonitorRuleAndRecord {

    @Test
    public void testIsOverDue(){
        Date d = new Date();
        //d.setMonth(d.getMonth()-2);
        Date d0 = new Date();
        d.setDate(d.getDate()-3);
        //d.setDate(d.getDate()+3);
        //System.out.println(d.getDate());
        //System.out.println(isOverDue(d));
        System.out.println(d.compareTo(d0));
    }

    @Test
    public void test(){
        Calendar calendar = Calendar.getInstance();

        Calendar now = Calendar.getInstance();

        SimpleDateFormat f = new SimpleDateFormat("yyyy");

        try {
            Date d = f.parse("2017");
            Date n = new Date();

            n.setYear(n.getYear()-1);

            ;

            System.out.println(n.getYear()<= d.getYear());
        }catch(Exception e){
            e.printStackTrace();
        }

        //calendar.set(Calendar.YEAR,calendar.get(Calendar.YEAR)-1);

    }

    private boolean isOverDue(Date  dateObj){
        if(dateObj != null){
            SimpleDateFormat format = new SimpleDateFormat(Format.PATTERN_DATE);
            try {
                Date now = new Date();
                now.setMonth(now.getMonth()-1);
                if(now.before(dateObj)){
                    return false;
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return true;
    }
}
