package controllers.mobile;

import constants.Constants;
import controllers.BaseController;
import play.Logger;
import play.mvc.Http;

/**
 * Created by libaozhong on 2015/5/27.
 */
public class QuickRegister extends BaseController {
    public static void quickRegister(){
       String fpHots= Constants.FP_HOST;

        Http.Request reuqets = Http.Request.current();
        Logger.info(reuqets.params.toString());
        String code=reuqets.params.get("code");
        Logger.info(code);
        render(code,fpHots);
    }

    public static void qrredirect(){

        String fpHots= Constants.FP_HOST;
        render(fpHots);


    }
    public static void registerSuccess(){
        String fpHots= Constants.FP_HOST;
        render(fpHots);

    }
}
