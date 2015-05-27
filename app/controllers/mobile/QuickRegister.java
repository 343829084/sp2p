package controllers.mobile;

import constants.Constants;
import controllers.BaseController;

/**
 * Created by libaozhong on 2015/5/27.
 */
public class QuickRegister extends BaseController {
    public static void quickRegister(){
       String fpHots= Constants.FP_HOST;
        render(fpHots);
    }

    public static void registerSuccess(){
        String fpHots= Constants.FP_HOST;
        render(fpHots);
    }
}
