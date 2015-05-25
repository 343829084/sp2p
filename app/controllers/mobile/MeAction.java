package controllers.mobile;

import business.Bid;
import business.Invest;
import controllers.BaseController;
import models.v_invest_records;
import models.y_subject_url;
import play.db.jpa.JPA;
import utils.ErrorInfo;
import utils.PageBean;

import javax.persistence.Query;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * <p>Project: com.shovesoft.sp2p</p>
 * <p>Title: ProductAction.java</p>
 * <p>Description: </p>
 * <p>Copyright (c) 2014 Sunlights.cc</p>
 * <p>All Rights Reserved.</p>
 *
 * @author <a href="mailto:jiaming.wang@sunlights.cc">wangJiaMing</a>
 */
public class MeAction extends BaseController {

    public static void changePassWord(String borrowId) {
        render();
    }
    public static void aboutOur() {
        render();
    }
    public static void safety() {
        render();
    }

    static Bid buildBid(long bidId) {
        Bid bid = new Bid();
        bid.id = bidId;
        return bid;
    }
}
