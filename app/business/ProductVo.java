package business;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by Yuan on 2015/6/12.
 */
public class ProductVo {

    private Long prodId;                                //产品id
    private String prodName;                            //产品名
    private String interestRate;                        //年化收益率
    private String deadline;                            //期限
    private BigDecimal bidMoney;                        //起投金额
    private boolean isNewUser;                          //是否新手标
    private BigDecimal remainingAvailableMoney;         //剩余可投金额
    private BigDecimal availableMoney;                  //可投金额
    private Date sellTime;                              //开售时间
    private String duringTime;                          //理财期限 （理财冻结时间）
    private String predictDeadline;                     //预计到期时间
    private BigDecimal totalBidMoney;                   //累计可投金额
    private String prodStatus;                          //产品状态

    public Long getProdId() {
        return prodId;
    }

    public void setProdId(Long prodId) {
        this.prodId = prodId;
    }

    public String getProdName() {
        return prodName;
    }

    public void setProdName(String prodName) {
        this.prodName = prodName;
    }

    public String getInterestRate() {
        return interestRate;
    }

    public void setInterestRate(String interestRate) {
        this.interestRate = interestRate;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public Date getSellTime() {
        return sellTime;
    }

    public void setSellTime(Date sellTime) {
        this.sellTime = sellTime;
    }

    public BigDecimal getBidMoney() {
        return bidMoney;
    }

    public void setBidMoney(BigDecimal bidMoney) {
        this.bidMoney = bidMoney;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public BigDecimal getRemainingAvailableMoney() {
        return remainingAvailableMoney;
    }

    public void setRemainingAvailableMoney(BigDecimal remainingAvailableMoney) {
        this.remainingAvailableMoney = remainingAvailableMoney;
    }

    public BigDecimal getAvailableMoney() {
        return availableMoney;
    }

    public void setAvailableMoney(BigDecimal availableMoney) {
        this.availableMoney = availableMoney;
    }

    public String getDuringTime() {
        return duringTime;
    }

    public void setDuringTime(String duringTime) {
        this.duringTime = duringTime;
    }

    public String getPredictDeadline() {
        return predictDeadline;
    }

    public void setPredictDeadline(String predictDeadline) {
        this.predictDeadline = predictDeadline;
    }

    public BigDecimal getTotalBidMoney() {
        return totalBidMoney;
    }

    public void setTotalBidMoney(BigDecimal totalBidMoney) {
        this.totalBidMoney = totalBidMoney;
    }

    public String getProdStatus() {
        return prodStatus;
    }

    public void setProdStatus(String prodStatus) {
        this.prodStatus = prodStatus;
    }
}
