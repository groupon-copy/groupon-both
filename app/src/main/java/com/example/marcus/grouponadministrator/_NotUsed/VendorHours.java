package com.example.marcus.grouponadministrator._NotUsed;

/**
 * Created by Marcus Chiu on 4/12/2016.
 */
/*
public class VendorHours implements Serializable
{
    public final static int OPEN_TODAY = 1;
    public final static int CLOSED_TODAY = 0;
    public final static int TIME_VARIES = 2;

    List<VendorDay> days;

    public VendorHours()
    {
        days = new ArrayList<>();

        String[] data = {"VAR", "VAR", "VAR", "VAR", "VAR", "VAR", "VAR"};
        for (String e: data)
        {
            VendorDay day = new VendorDay(e);
            days.add(day);
        }
    }

    public VendorHours(String str)
    {
        days = new ArrayList<>();

        String[] data = str.split(":");
        for (String e: data)
        {
            VendorDay day = new VendorDay(e);
            days.add(day);
        }
    }

    public class VendorDay implements Serializable
    {
        public final static String TWENTY_FOUR_HOURS = "Twenty Four Hours";
        public final static String CLOSED = "Closed";
        public final static String VARIES = "Varies";

        String other;
        List<VendorStartEndTime> times;

        public VendorDay(String str)
        {
            other = "";
            times = new ArrayList<>();

            if(str.equals("TFH"))
            {
                other = TWENTY_FOUR_HOURS;
            }
            else if(str.equals("X"))
            {
                other = CLOSED;
            }
            else if(str.equals("VAR"))
            {
                other = VARIES;
            }
            else
            {
                String[] data = str.split(",");
                for (String s: data)
                {
                    VendorStartEndTime t = new VendorStartEndTime(s);
                    times.add(t);
                }
            }
        }
    }

    public class VendorStartEndTime implements Serializable
    {
        VendorTime start;
        VendorTime end;

        public VendorStartEndTime(String str)
        {
            String[] data = str.split("-");
            start = new VendorTime(data[0]);
            end = new VendorTime(data[1]);
        }
    }

    public class VendorTime implements Serializable
    {
        int hour;
        int minute;

        public VendorTime(String str)
        {
            hour = stringToInteger(str.substring(0,2));
            minute = stringToInteger(str.substring(2, 4));
        }
    }

    private static int stringToInteger(String str)
    {
        if(str != null)
        {
            if(str.substring(0, 1).equals("0"))
            {
                return Integer.parseInt(str.substring(1, 2));
            }
            else
            {
                return Integer.parseInt(str.substring(0, 2));
            }
        }
        return -1;
    }

    //return 1 if open, 0 if closed, 2 if don't know
    public static int isVendorOpenNow(VendorHours hours)
    {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        VendorHours.VendorDay dayHours = hours.days.get(day - 1);

        if(dayHours.other.equals(VendorHours.VendorDay.TWENTY_FOUR_HOURS))
            return OPEN_TODAY;
        else if(dayHours.other.equals(VendorHours.VendorDay.CLOSED))
            return CLOSED_TODAY;
        else if(dayHours.other.equals(VendorHours.VendorDay.VARIES))
            return TIME_VARIES;
        else
        {
            int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            int minute = Calendar.getInstance().get(Calendar.MINUTE);

            for(VendorHours.VendorStartEndTime t : dayHours.times)
            {
                if(isCurrentTimeWithinStartEndTime(t, hour, minute))
                    return OPEN_TODAY;
            }
            return CLOSED_TODAY;
        }
    }

    private static boolean isCurrentTimeWithinStartEndTime(VendorHours.VendorStartEndTime t, int cHour, int cMinute)
    {
        if(isCurrentTimeAfterTime(cHour, cMinute, t.start.hour, t.start.minute))
        {
            if(!isCurrentTimeAfterTime(cHour, cMinute, t.end.hour, t.end.minute))
                return true;
        }
        return false;
    }

    private static boolean isCurrentTimeAfterTime(int cHour, int cMinute, int hour, int minute)
    {
        if((cHour == hour && cMinute > minute) || cHour > hour)
            return true;
        else
            return false;
    }
}*/
