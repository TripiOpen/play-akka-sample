package services.log;

import com.google.inject.Singleton;
import models.log.BookingLog;

import java.util.Date;

@Singleton
public class BookingLogServiceImpl implements LogService {

    @Override
    public void writeLogs(String content) {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        BookingLog log = new BookingLog();
        log.setContent(content);
        log.setCreatedTime(new Date());

        /** Begin write log */
        BookingLog.db().beginTransaction();
        BookingLog.db().save(log);
        BookingLog.db().commitTransaction();
    }
}
