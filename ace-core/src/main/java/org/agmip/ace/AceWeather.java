package org.agmip.ace;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class AceWeather extends AceComponent implements IAceBaseComponent {
    private String wid;
    private AceRecordCollection dailyWeather = null;
    private List<String> recordYears;
    private List<String> missingDates;
    private DateTimeFormatter agmipDateFormat = DateTimeFormat.forPattern("yyyyMMdd");

    public AceWeather(byte[] source) throws IOException {
        super(source);
        this.wid = this.getValue("wid");
    }

    public String getId() {
        return this.wid;
    }
    
    public AceComponentType getType() {
        return AceComponentType.ACE_WEATHER;
    }

    public AceRecordCollection getDailyWeather() throws IOException {
        if (this.dailyWeather == null) {
            this.dailyWeather = this.getRecords("dailyWeather");
        }
        return this.dailyWeather;
    }

    public List<String> getRecordYears() throws IOException {
        if (this.recordYears == null) {
            AceRecordCollection daily = getDailyWeather();
            this.recordYears = new ArrayList<String>();
            for(AceRecord r : daily) {
                String date = r.getValue("w_date");
                if (date != null) {
                    String year = date.substring(0, 4);
                    if (! recordYears.contains(year)) {
                        recordYears.add(year);
                    }
                }
            }
        }
        return this.recordYears;
    }

    public List<String> getMissingDates() throws IOException {
        this.missingDates = new ArrayList<String>();
        Iterator<AceRecord> i = this.getDailyWeather().iterator();
        AceRecord r;
        DateTime currentDate;

        if (i.hasNext()) {
            r = i.next();
            currentDate = agmipDateFormat.parseDateTime(r.getValue("w_date"));
            while(i.hasNext()) {
                r = i.next();
                DateTime nextDate = agmipDateFormat.parseDateTime(r.getValue("w_date"));
                currentDate = currentDate.plusDays(1);
                while(! currentDate.equals(nextDate)) {
                    this.missingDates.add(currentDate.toString(agmipDateFormat));
                    currentDate = currentDate.plusDays(1);
                }
            }
        }
        return this.missingDates;
    }
}
