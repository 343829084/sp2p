package models;

import play.db.jpa.Model;

import javax.persistence.Entity;
import java.util.Date;

/**
 * Created by libaozhong on 2015/6/15.
 */
@Entity
public class t_redpacket extends Model {
    private Integer activityId; //int(11) NULL活动id
    private Integer balane; //int(11) NULL红包余额
    private Integer total; //int(11) NULL红包总金额
    private Integer sendint; //(11) NULL已经发送红包金额
    private Date time; //NOT NULL红包创建时间
    private Integer minValue; //int(11) NULL红包最小值
    private Integer maxvalue; //int(11) NULL红包最大值
    private Integer totalNum;  //int(11) NULL发送总人数
    private Integer sendNum; //int(11) NULL已经发送人数
    private String actName; //varchar(35) NULL活动的名字
    private String remarkvar;  //char(50) NULL活动备注
    private String logo_imgurl;  //varchar(110) NULLlogo图片
    private String content;  //varchar(110) NULL活动内容
    private String share_imgurl;   //varchar(110) NULL分享图片链接
    private String share_url;  //varchar(110) NULL分享链接
    private String wishing;  //varchar(110) NULL祝福语
    private Integer over; //int(2) NULL红包是否已结1.未结束2.结束
    private Integer couple; //int(2); NULL红包可以重复领取1.可以 2不可以

    public  t_redpacket(){
    }
}
