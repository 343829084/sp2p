package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by libaozhong on 2015/6/15.
 */
@Entity
public class t_redpacket_bill extends Model {
    private String billNo; //int(11) NULL红包订单号
    private String openid; //varchar(50) NOT NULL领取红包的openid
    private Integer amount; //int(11) NULL领取红包的金额
    private Date addTime; //timestamp NOT NULL红包领取时间
    private Integer result; //int(2) NULL红包领取结果1.失败2.成功
    private String remark; //varchar(300) NULL备注用于记录微信返回json
    private Integer redPackId; //int(11) NULL红包id
}
