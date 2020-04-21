package org.estatio.module.application.app;

import java.util.TimeZone;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import org.apache.isis.applib.clock.Clock;

public class EstatioClock extends Clock {
    private static final TimeZone AMS_TIME_ZONE;

    static {
        TimeZone tempTimeZone = TimeZone.getTimeZone("Europe/Amsterdam");
        if (tempTimeZone == null) {
            tempTimeZone = TimeZone.getTimeZone("/Etc/GMT-2");
        }
        AMS_TIME_ZONE = tempTimeZone;
    }

    public synchronized static EstatioClock initialize() {
        if (!isInitialized() || !(getInstance() instanceof EstatioClock)) {
            new EstatioClock();
        }
        return (EstatioClock) getInstance();
    }

    @Override
    protected long time() {
//        Calendar calendar = new GregorianCalendar();
//        calendar.setTimeZone(AMS_TIME_ZONE);
//        return calendar.getTimeInMillis();
        return System.currentTimeMillis();
    }

    public static LocalDateTime getTimeAsLocalDateTime() {
        return new LocalDateTime(getTime(), DateTimeZone.forTimeZone(AMS_TIME_ZONE));
    }

    public static LocalDate getTimeAsLocalDate() {
        return new LocalDate(getTime(), DateTimeZone.forTimeZone(AMS_TIME_ZONE));
    }

    public static DateTime getTimeAsDateTime(){
        return new DateTime(getTime(), DateTimeZone.forTimeZone(AMS_TIME_ZONE));
    }

}
