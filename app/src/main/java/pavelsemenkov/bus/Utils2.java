package pavelsemenkov.bus;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class contains static util methods
 */
public class Utils2 {

    /**
     * Private constructor in order to prevent accidental instantiation
     */
    private Utils2() {}

    /**
     * Convert Unix timestamp to a human readable representation of date
     */
    public static String convertToHumanReadableDate(String timestamp) {
        SimpleDateFormat fmtOut = new SimpleDateFormat();
        return fmtOut.format(new Date(Long.valueOf(timestamp)));
    }

}
