package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;
import 	javax.persistence.Temporal;
import 	javax.persistence.TemporalType;
import 	java.util.Calendar;

/**
 * 业务实体表：t_acct_year_period
 * 业务实体说明：
 * 生成时间：2010-10-28 11:43:07
 * 版权所有：
 */
@IdClass(TAcctYearPeriodPK.class)
@Entity
@Table(name = "t_acct_year_period")
public class TAcctYearPeriodPO extends BasePersistenceObject{

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
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "acct_month" ,nullable=false,length=2)
	
    private String acct_month;

    /**
     */
    @Column(name = "begin_date" ,nullable=false,length=7)
	
    private Calendar begin_date;

    /**
     */
    @Column(name = "end_date" ,nullable=false,length=7)
	
    private Calendar end_date;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "cash_flag" ,nullable=false,length=1)
	
    private Integer cash_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "fix_flag" ,nullable=false,length=1)
	
    private Integer fix_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "mat_flag" ,nullable=false,length=1)
	
    private Integer mat_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "med_flag" ,nullable=false,length=1)
	
    private Integer med_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "drugstore_flag" ,nullable=false,length=1)
	
    private Integer drugstore_flag;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "wage_flag" ,nullable=false,length=1)
	
    private Integer wage_flag;

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
    @Column(name = "mat_check_date" ,nullable=true,length=7)
	
    private Calendar mat_check_date;

    /**
     */
    @Column(name = "fix_check_date" ,nullable=true,length=7)
	
    private Calendar fix_check_date;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "is_depreciation" ,nullable=false,length=1)
	
    private Integer is_depreciation;

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
    public void setAcct_month(String acct_month){
            support.firePropertyChange("acct_month", this.acct_month, acct_month);
            this.acct_month = acct_month;
    }
    public String getAcct_month(){
            return this.acct_month;
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
    public void setCash_flag(Integer cash_flag){
            support.firePropertyChange("cash_flag", this.cash_flag, cash_flag);
            this.cash_flag = cash_flag;
    }
    public Integer getCash_flag(){
            return this.cash_flag;
    }
    public void setFix_flag(Integer fix_flag){
            support.firePropertyChange("fix_flag", this.fix_flag, fix_flag);
            this.fix_flag = fix_flag;
    }
    public Integer getFix_flag(){
            return this.fix_flag;
    }
    public void setMat_flag(Integer mat_flag){
            support.firePropertyChange("mat_flag", this.mat_flag, mat_flag);
            this.mat_flag = mat_flag;
    }
    public Integer getMat_flag(){
            return this.mat_flag;
    }
    public void setMed_flag(Integer med_flag){
            support.firePropertyChange("med_flag", this.med_flag, med_flag);
            this.med_flag = med_flag;
    }
    public Integer getMed_flag(){
            return this.med_flag;
    }
    public void setDrugstore_flag(Integer drugstore_flag){
            support.firePropertyChange("drugstore_flag", this.drugstore_flag, drugstore_flag);
            this.drugstore_flag = drugstore_flag;
    }
    public Integer getDrugstore_flag(){
            return this.drugstore_flag;
    }
    public void setWage_flag(Integer wage_flag){
            support.firePropertyChange("wage_flag", this.wage_flag, wage_flag);
            this.wage_flag = wage_flag;
    }
    public Integer getWage_flag(){
            return this.wage_flag;
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
    public void setMat_check_date(Calendar mat_check_date){
            support.firePropertyChange("mat_check_date", this.mat_check_date, mat_check_date);
            this.mat_check_date = mat_check_date;
    }
    public Calendar getMat_check_date(){
            return this.mat_check_date;
    }
    public void setFix_check_date(Calendar fix_check_date){
            support.firePropertyChange("fix_check_date", this.fix_check_date, fix_check_date);
            this.fix_check_date = fix_check_date;
    }
    public Calendar getFix_check_date(){
            return this.fix_check_date;
    }
    public void setIs_depreciation(Integer is_depreciation){
            support.firePropertyChange("is_depreciation", this.is_depreciation, is_depreciation);
            this.is_depreciation = is_depreciation;
    }
    public Integer getIs_depreciation(){
            return this.is_depreciation;
    }
    public void setDj_flag(Integer dj_flag){
            support.firePropertyChange("dj_flag", this.dj_flag, dj_flag);
            this.dj_flag = dj_flag;
    }
    public Integer getDj_flag(){
            return this.dj_flag;
    }

}

class TAcctYearPeriodPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String copy_code;
    private String acct_year;
    private String acct_month;
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
    public void setAcct_month(String acct_month){
            this.acct_month = acct_month;
    }
    public String getAcct_month(){
            return this.acct_month;
    }

}

