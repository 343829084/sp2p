package controllers.mobile.account;

import play.mvc.With;
import controllers.BaseController;
import controllers.interceptor.H5Interceptor;

@With(H5Interceptor.class)
public class EnchashAction extends BaseController {
	public static void enchash(String investAmount){
		EnchashAction.render();
	}
}
