package com.rab.sys.sysmanager.base.model;

import java.io.Serializable;
import java.util.Calendar;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_acct_year
 * 业务实体说明：
 * 生成时间：2010-10-26 11:08:11
 * 版权所有：
 */
@IdClass(TAcctYearPK.class)
@Entity
@Table(name = "t_acct_year")
public class TAcctYearPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "copy_code" ,nullable=false,length=3)
	
    private String copy_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "acct_year" ,nullable=false,length=4)
	
    private String acct_year;

    /**
     */
    @Column(name = "begin_date" ,nullable=false,length=7)
	
    private Calendar begin_date;

    /**
     */
    @Column(name = "end_date" ,nullable=false,length=7)
	
    private Calendar end_date;

    /**
     */
    @Column(name = "period_num" ,nullable=false,length=3)
	
    private Integer period_num;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "acc_flag" ,nullable=false,length=1)
	
    private Integer acc_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "budg_flag" ,nullable=false,length=1)
	
    private Integer budg_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "perf_flag" ,nullable=false,length=1)
	
    private Integer perf_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "cost_flag" ,nullable=false,length=1)
	
    private Integer cost_flag;

    /**
     */
    @Column(name = "cash_date" ,nullable=true,length=7)
	
    private Calendar cash_date;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "dj_flag" ,nullable=false,length=1)
	
    private Integer dj_flag;


    public void setCopy_code(String copy_code){
            support.firePropertyChange("copy_code", this.copy_code, copy_code);
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setAcct_year(String acct_year){
            support.firePropertyChange("acct_year", this.acct_year, acct_year);
            this.acct_year = acct_year;
    }
    public String getAcct_year(){
            return this.acct_year;
    }
    public void setBegin_date(Calendar begin_date){
            support.firePropertyChange("begin_date", this.begin_date, begin_date);
            this.begin_date = begin_date;
    }
    public Calendar getBegin_date(){
            return this.begin_date;
    }
    public void setEnd_date(Calendar end_date){
            support.firePropertyChange("end_date", this.end_date, end_date);
            this.end_date = end_date;
    }
    public Calendar getEnd_date(){
            return this.end_date;
    }
    public void setPeriod_num(Integer period_num){
            support.firePropertyChange("period_num", this.period_num, period_num);
            this.period_num = period_num;
    }
    public Integer getPeriod_num(){
            return this.period_num;
    }
    public void setAcc_flag(Integer acc_flag){
            support.firePropertyChange("acc_flag", this.acc_flag, acc_flag);
            this.acc_flag = acc_flag;
    }
    public Integer getAcc_flag(){
            return this.acc_flag;
    }
    public void setBudg_flag(Integer budg_flag){
            support.firePropertyChange("budg_flag", this.budg_flag, budg_flag);
            this.budg_flag = budg_flag;
    }
    public Integer getBudg_flag(){
            return this.budg_flag;
    }
    public void setPerf_flag(Integer perf_flag){
            support.firePropertyChange("perf_flag", this.perf_flag, perf_flag);
            this.perf_flag = perf_flag;
    }
    public Integer getPerf_flag(){
            return this.perf_flag;
    }
    public void setCost_flag(Integer cost_flag){
            support.firePropertyChange("cost_flag", this.cost_flag, cost_flag);
            this.cost_flag = cost_flag;
    }
    public Integer getCost_flag(){
            return this.cost_flag;
    }
    public void setCash_date(Calendar cash_date){
            support.firePropertyChange("cash_date", this.cash_date, cash_date);
            this.cash_date = cash_date;
    }
    public Calendar getCash_date(){
            return this.cash_date;
    }
    public void setDj_flag(Integer dj_flag){
            support.firePropertyChange("dj_flag", this.dj_flag, dj_flag);
            this.dj_flag = dj_flag;
    }
    public Integer getDj_flag(){
            return this.dj_flag;
    }

}

class TAcctYearPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String copy_code;
    private String acct_year;
    public void setCopy_code(String copy_code){
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setAcct_year(String acct_year){
            this.acct_year = acct_year;
    }
    public String getAcct_year(){
            return this.acct_year;
    }

}

