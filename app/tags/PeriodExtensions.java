package tags;

import play.templates.JavaExtensions;

/**
 * Created by Yuan on 2015/5/12.
 */
public class PeriodExtensions extends JavaExtensions {

    public static String formatPeriod(Integer period, Integer periodUnit) {
        if (period == null || periodUnit == null) return null;
        Integer value = null;
        String unit = null;
        if (periodUnit == 1) {
            if (31 > period) {
                value = period;
                unit = "天";
            } else {
                value = Integer.valueOf((int) Math.floor(period / 31));
                unit = "个月";
            }
        } else if (periodUnit == 0) {
            value = period;
            unit = "个月";
        } else if (periodUnit == -1) {
            value = period;
            unit = "年";
        }

        return value + unit;
    }
}
