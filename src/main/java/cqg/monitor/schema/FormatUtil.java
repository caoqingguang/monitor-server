package cqg.monitor.schema;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by caoqingguang on 2017/3/4.
 */
public abstract class FormatUtil {

  public static final SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  public static String formatMillis(long millis){
    return sdf.format(new Date(millis));
  }

  public static long getCurrentMinuteMillis(){
    long timeMillis = System.currentTimeMillis();
    long dc = timeMillis % 60000;
    if(dc>0){
      timeMillis-=dc;
    }
    return timeMillis;
  }

  public static float formatFloat(float value){
    return new BigDecimal(value)
      .setScale(2, BigDecimal.ROUND_HALF_UP).floatValue();
  }



}
