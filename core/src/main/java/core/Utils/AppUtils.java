package core.Utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import core.Core;

import static java.lang.String.format;

//Remove all activites
//intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

//Remove range of activites
//i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//startActivity(i);
//finish();

public class AppUtils {
    private static String TAG = AppUtils.class.getSimpleName();

    public static void Toast(String Message) {
        Toast toast = Toast.makeText(Core.getContext(), "  " + Message + "  ", Toast.LENGTH_SHORT);
//        TextView textView = (((TextView)((LinearLayout)toast.getView()).getChildAt(0)));
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
//        textView.setTextColor(Color.BLACK);
//        toast.getView().setBackgroundColor(NavBus.getContext().getResources().getColor(R.color.colorPrimary));
//        Toast toast = new Toast(NavBus.getContext());
//        View layout = LayoutInflater.from(NavBus.getContext()).inflate(R.layout.toast,null,false);
//        TextView textView = layout.findViewById(R.id.toast);
//        textView.setText(Message);
//        toast.setView(layout);
//        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.show();

    }

    public static void Toast(String Message, boolean Long) {
        Toast toast = Toast.makeText(Core.getContext(), "  " + Message + "  ", Toast.LENGTH_LONG);
//        TextView textView = (((TextView)((LinearLayout)toast.getView()).getChildAt(0)));
//        textView.setTypeface(Typeface.DEFAULT_BOLD);
//        textView.setTextColor(Color.BLACK);
//        toast.getView().setBackgroundColor(Core.getContext().getResources().getColor(R.color.colorPrimary));
        toast.show();
    }

    public static void Toast(int Message) {
        Toast.makeText(Core.getContext(), "  " + String.valueOf(Message) + "  ", Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(CharSequence target) {
        if (TextUtils.isEmpty(target) || target.length() < 10) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public static Date parseDate(String date, String time) {
//        27-4-2018 12 : 00
        final String inputFormat = "dd-MM-yyyy HH:mm";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat, Locale.US);
        try {
            return inputParser.parse(date + " " + time);
        } catch (ParseException e) {
            return new Date(0);
        }
    }

    public static Date parseDate(String datetime) {
//        27-4-2018 12 : 00
//        2019-04-09 06:59:32
        final String inputFormat = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat inputParser = new SimpleDateFormat(inputFormat);
        try {
//            inputParser.setTimeZone(TimeZone.getTimeZone("UTC"));
            return inputParser.parse(datetime);
        } catch (ParseException e) {
            return new Date(0);
        }
    }
    public static Date addDay(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_YEAR, i);
        return cal.getTime();
    }
    public static Date addMonth(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, i);
        return cal.getTime();
    }
//    public static String getPhoneNumber() {
//        TelephonyManager tMgr = (TelephonyManager) JobsOnTheMapEmployee.getContext().getSystemService(Context.TELEPHONY_SERVICE);
//        String mPhoneNumber = tMgr.getLine1Number();
//        if (mPhoneNumber != null) {
//            Log.i("DeviceInfo ", "Number: " + mPhoneNumber);
//            return mPhoneNumber;
//        } else return "";
//    }

    public static String formatDate(String format, Date date) {
//        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday
//        String day          = (String) DateFormat.format("dd",   date); // 20
//        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
//        String monthString  = (String) DateFormat.format("MMMM",  date); // August
//        String monthNumber  = (String) DateFormat.format("MM",   date); // 06
//        String year         = (String) DateFormat.format("yyyy", date); // 2013
//        String year         = (String) DateFormat.format("yyyy", date); // 2013
//        String 24hourTime         = (String) DateFormat.format("HH:mm:ss", date); // 15:02:50
//        String 12hourTime         = (String) DateFormat.format("hh:mm:ss", date); // 03:02 PM
        if (date == null)
            return null;
        return (String) DateFormat.format(format, date);
    }

    public static String customFormatDate(String format, Date date) {
//        String dayOfTheWeek = (String) DateFormat.format("EEEE", date); // Thursday

//        String monthString  = (String) DateFormat.format("MMM",  date); // Jun
//        String monthString  = (String) DateFormat.format("MMMM",  date); // August


//        String year         = (String) DateFormat.format("yyyy", date); // 2013
//        String 24hourTime         = (String) DateFormat.format("HH:mm:ss", date); // 15:02:50
//        String 12hourTime         = (String) DateFormat.format("hh:mm:ss", date); // 03:02 PM
        if (date == null)
            return null;

        Date localDate = utcToLocalTime(date);

        return (String) DateFormat.format(format, localDate);
    }

    private static Calendar cal = Calendar.getInstance();
    private static TimeZone tz = cal.getTimeZone();

    public static Date utcToLocalTime(Date date) {
        //tz.getRawOffset() returns 18000000 milliseconds = 5 hours(pakistani timezone for pakistan)
        System.out.print(tz.getRawOffset());
        date.setTime(date.getTime() + tz.getRawOffset());
        return date;
    }

    public static String localToUtcTime(Date time) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss");
//        Date inputTime = timeFormat.parse(time);

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
            SimpleDateFormat localDateFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss");
            Date utcTime = localDateFormat.parse(simpleDateFormat.format(time));
            return timeFormat.format(utcTime);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getTime() {
        Calendar now = Calendar.getInstance();
//        int date = now.get(Calendar.DATE);
//        int month = now.get(Calendar.MONTH);
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);
        int second = now.get(Calendar.SECOND);
        return hour + ":" + minute + ":" + second;
    }

    public static String getDateString() {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            String date_str = sdf.format(calendar.getTime());
            return date_str;
        } catch (Exception e) {
            System.out.print(e.getMessage());
            return "";
        }
    }

    // 2021-01-31 by shen
    public static List<Calendar> dateStrings2Canlendars(ArrayList<String> dates) {
        List<Calendar> calendars = new ArrayList<Calendar>();
        for (String date_str: dates) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar c = Calendar.getInstance();
                c.setTime(sdf.parse(date_str));
                calendars.add(c);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
        return calendars;
    }
    // 2021-01-31 by shen
    public static ArrayList<String> canlendars2DateStrings(List<Calendar> calendars) {
        ArrayList<String> dates = new ArrayList<String>();
        for (Calendar calendar: calendars) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String date_str = sdf.format(calendar.getTime());
                dates.add(date_str);
            } catch (Exception e) {
                System.out.print(e.getMessage());
            }
        }
        return dates;
    }


    public static String printDifference(Date startDate, Date endDate) {
        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        System.out.println("startDate : " + startDate);
        System.out.println("endDate : " + endDate);
        System.out.println("different : " + different);

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = different / daysInMilli;
        different = different % daysInMilli;

        long elapsedHours = different / hoursInMilli;
        different = different % hoursInMilli;

        long elapsedMinutes = different / minutesInMilli;
        different = different % minutesInMilli;

        long elapsedSeconds = different / secondsInMilli;

        System.out.printf(
                "%d days, %d hours, %d minutes, %d seconds%n",
                elapsedDays, elapsedHours, elapsedMinutes, elapsedSeconds);
        return /*elapsedDays + "d" + */elapsedHours + "h" + elapsedMinutes + "m";
    }

    /**
     * Returns the consumer friendly device name
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        }
        return capitalize(manufacturer) + " " + model;
    }

    private static String capitalize(String str) {
        if (TextUtils.isEmpty(str)) {
            return str;
        }
        char[] arr = str.toCharArray();
        boolean capitalizeNext = true;

        StringBuilder phrase = new StringBuilder();
        for (char c : arr) {
            if (capitalizeNext && Character.isLetter(c)) {
                phrase.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            } else if (Character.isWhitespace(c)) {
                capitalizeNext = true;
            }
            phrase.append(c);
        }

        return phrase.toString();
    }

    /**
     * Get IP address from first non-localhost interface
     *
     * @param /ipv4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':') < 0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim < 0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
            AppLog.e(TAG, ex.getMessage());
        } // for now eat exceptions
        return "";
    }

    public static String getAppVersionName() {
        try {
            PackageInfo pInfo = Core.getContext().getPackageManager().getPackageInfo(Core.getContext().getPackageName(), 0);
            String version = pInfo.versionName;
            int verCode = pInfo.versionCode;//Version Code
            return version;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Toast(e.getMessage());
            return null;
        }
    }

    public static String capitalizeFirstChar(String string) {
        if (string == null || string.length() == 0)
            return string;
        else
            return string.substring(0, 1).toUpperCase() + string.substring(1);
    }

    public static String replaceWord(String inputString, String searchFor, String replaceWith) {
        if (inputString.indexOf(searchFor) > 0) {
            return inputString.replace(searchFor, replaceWith);
        } else return inputString;
    }

    public static String userNameFromEmail(String email) {
        int indexOfAtSign = email.indexOf("@");
        return email.substring(0, indexOfAtSign);
    }

    public static double distanceInKM(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    public static String getSuitableDistance(double distanceInMeter) {
        DecimalFormat df = new DecimalFormat("#.#");
        double oneMileinMeter = 1609.34;
        if (distanceInMeter < 1000)
            return df.format(distanceInMeter) + "m";
        else if (distanceInMeter < oneMileinMeter)
            return df.format(distanceInMeter / 1000) + "km";
        else
            return df.format(distanceInMeter / oneMileinMeter) + "mi";
    }

    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private static double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public static String removeStartingSpaces(String string) {
        String regex = "^\\s+";
        return string.replaceAll(regex, "");//Removing all spaces from start
    }

    public static String removeEndingSpaces(String string) {
        String regex = "\\s+$";
        return string.replaceAll(regex, "");
    }

    public static String formatNumber(String string) {
        return String.format("%,d", (int)Double.parseDouble(string));
    }

    public static String removeStartEndSpaces(String string) {
        return string.trim();
    }

    public String addMinsInCurrentTime(String dateFormat, String createdTime, int timeToAdded) {
        SimpleDateFormat simpleDateFormat = null;
        String addedResultTime = "";
        GregorianCalendar gregorianCalendar;
        Date timeCreated;

        try {
            simpleDateFormat = new SimpleDateFormat(dateFormat);
            timeCreated = simpleDateFormat.parse(createdTime);
            gregorianCalendar = new GregorianCalendar();

            timeCreated = gregorianCalendar.getTime();
            gregorianCalendar.add(GregorianCalendar.MINUTE, timeToAdded);
            Date newTimeUptoScheduled = gregorianCalendar.getTime();

            addedResultTime = AppUtils.formatDate(dateFormat, newTimeUptoScheduled).toUpperCase();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return addedResultTime;
    }

    public long parseTimeInMilliseconds(String currentDateTime, String timeAfterAdding) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("hh:mm:ss a");
        Date parseCurrentTime = null;
        long timeInMilliSeconds = 0;

        try {
            parseCurrentTime = simpleDateFormat.parse(currentDateTime);

            Date parseTimeAfterAdding = simpleDateFormat.parse(timeAfterAdding);

            timeInMilliSeconds = parseTimeAfterAdding.getTime() - parseCurrentTime.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
        }

        return timeInMilliSeconds;
    }

    public String getCounterTime(Long millisUntilFinished) {
        long hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(
                TimeUnit.MILLISECONDS.toHours(millisUntilFinished));
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(
                TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));

        String timeUpdated = format("%d:%02d:%02d", hours, minutes, seconds);

        return timeUpdated;
    }


}