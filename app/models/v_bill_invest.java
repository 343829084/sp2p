package models;

import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.Transient;
import constants.Constants;
import play.db.jpa.Model;
import utils.Security;

/**
 *我的理财账单
 */
@Entity
public class v_bill_invest extends Model {
	public long user_id;
	public long bid_id;
    public String title;
    public double income_amounts;
    public int status;
    public Date repayment_time;
    public Date real_repayment_time;
	public double receive_corpus;
	@Transient
	public int bidStatus;
	@Transient
	public Date bidTime;
	@Transient
	public int invest_period;
	@Transient
	public int period_unit;
    @Transient
	public String sign;
	@Transient
	public double apr;
	
	/**
	 * 获取加密ID
	 */
	public String getSign() {
		return Security.addSign(this.id, Constants.BILL_ID_SIGN);
	}

}