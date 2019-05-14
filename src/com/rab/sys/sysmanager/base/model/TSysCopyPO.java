package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_copy
 * 业务实体说明：
 * 生成时间：2010-10-26 11:08:11
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_copy")
public class TSysCopyPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：【t_mate_dept_app_main : copy_code】  【t_mate_out_main : copy_code】  
     */
    @Id
    @Column(name = "copy_code" ,nullable=false,length=3)
	
    private String copy_code;

    /**
     */
    @Column(name = "copy_name" ,nullable=false,length=20)
	
    private String copy_name;

    /**
     */
    @Column(name = "comp_id" ,nullable=false,length=8)
	
    private Integer comp_id;

    /**
     */
    @Column(name = "co_code" ,nullable=false,length=10)
	
    private String co_code;

    /**
     */
    @Column(name = "copy_start_year" ,nullable=false,length=4)
	
    private String copy_start_year;

    /**
     */
    @Column(name = "copy_start_month" ,nullable=false,length=2)
	
    private String copy_start_month;

    /**
     */
    @Column(name = "start_year" ,nullable=true,length=4)
	
    private String start_year;

    /**
     */
    @Column(name = "start_month" ,nullable=true,length=2)
	
    private String start_month;

    /**
     */
    @Column(name = "cur_year" ,nullable=true,length=4)
	
    private String cur_year;

    /**
     */
    @Column(name = "cur_month" ,nullable=true,length=2)
	
    private String cur_month;

    /**
     */
    @Column(name = "end_day" ,nullable=false,length=4)
	
    private String end_day;

    /**
	 * 数据库默认值： 1
     */
    @Column(name = "copy_type" ,nullable=false,length=2)
	
    private Integer copy_type;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "is_analyse" ,nullable=false,length=1)
	
    private Integer is_analyse;

    /**
	 * 数据库默认值： 0
     */
    @Column(name = "is_check" ,nullable=false,length=1)
	
    private Integer is_check;


    public void setCopy_code(String copy_code){
            support.firePropertyChange("copy_code", this.copy_code, copy_code);
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setCopy_name(String copy_name){
            support.firePropertyChange("copy_name", this.copy_name, copy_name);
            this.copy_name = copy_name;
    }
    public String getCopy_name(){
            return this.copy_name;
    }
    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setCo_code(String co_code){
            support.firePropertyChange("co_code", this.co_code, co_code);
            this.co_code = co_code;
    }
    public String getCo_code(){
            return this.co_code;
    }
    public void setCopy_start_year(String copy_start_year){
            support.firePropertyChange("copy_start_year", this.copy_start_year, copy_start_year);
            this.copy_start_year = copy_start_year;
    }
    public String getCopy_start_year(){
            return this.copy_start_year;
    }
    public void setCopy_start_month(String copy_start_month){
            support.firePropertyChange("copy_start_month", this.copy_start_month, copy_start_month);
            this.copy_start_month = copy_start_month;
    }
    public String getCopy_start_month(){
            return this.copy_start_month;
    }
    public void setStart_year(String start_year){
            support.firePropertyChange("start_year", this.start_year, start_year);
            this.start_year = start_year;
    }
    public String getStart_year(){
            return this.start_year;
    }
    public void setStart_month(String start_month){
            support.firePropertyChange("start_month", this.start_month, start_month);
            this.start_month = start_month;
    }
    public String getStart_month(){
            return this.start_month;
    }
    public void setCur_year(String cur_year){
            support.firePropertyChange("cur_year", this.cur_year, cur_year);
            this.cur_year = cur_year;
    }
    public String getCur_year(){
            return this.cur_year;
    }
    public void setCur_month(String cur_month){
            support.firePropertyChange("cur_month", this.cur_month, cur_month);
            this.cur_month = cur_month;
    }
    public String getCur_month(){
            return this.cur_month;
    }
    public void setEnd_day(String end_day){
            support.firePropertyChange("end_day", this.end_day, end_day);
            this.end_day = end_day;
    }
    public String getEnd_day(){
            return this.end_day;
    }
    public void setCopy_type(Integer copy_type){
            support.firePropertyChange("copy_type", this.copy_type, copy_type);
            this.copy_type = copy_type;
    }
    public Integer getCopy_type(){
            return this.copy_type;
    }
    public void setIs_analyse(Integer is_analyse){
            support.firePropertyChange("is_analyse", this.is_analyse, is_analyse);
            this.is_analyse = is_analyse;
    }
    public Integer getIs_analyse(){
            return this.is_analyse;
    }
    public void setIs_check(Integer is_check){
            support.firePropertyChange("is_check", this.is_check, is_check);
            this.is_check = is_check;
    }
    public Integer getIs_check(){
            return this.is_check;
    }

}


