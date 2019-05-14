package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_para_data
 * 业务实体说明：
 * 生成时间：2010-10-21 15:02:19
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_para_data")
public class TSysParaDataPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "para_code" ,nullable=false, length=4)
    private String para_code;

    /**
     */
    @Column(name = "mod_code" ,nullable=false, length=20)
    private String mod_code;

    /**
     */
    @Column(name = "para_name" ,nullable=false, length=40)
    private String para_name;

    /**
     */
    @Column(name = "para_value" ,nullable=false, length=40)
    private String para_value;

    /**
     */
    @Column(name = "para_type" ,nullable=false, length=3)
    private Integer para_type;

    /**
     */
    @Column(name = "para_sql" ,nullable=true, length=800)
    private String para_sql;

    /**
     */
    @Column(name = "para_attr" ,nullable=false, length=3)
    private Integer para_attr;

    /**
     */
    @Column(name = "down_type" ,nullable=true, length=3)
    private Integer down_type;

    /**
     */
    @Column(name = "para_option" ,nullable=true, length=40)
    private String para_option;

    /**
     */
    @Column(name = "data_type" ,nullable=false, length=3)
    private Integer data_type;

    /**
     */
    @Column(name = "describe" ,nullable=true, length=200)
    private String describe;

    /**
     */
    @Column(name = "comp_code" ,nullable=true, length=20)
    private String comp_code;

    /**
     */
    @Column(name = "copy_code" ,nullable=true, length=3)
    private String copy_code;


    public void setPara_code(String para_code){
            support.firePropertyChange("para_code", this.para_code, para_code);
            this.para_code = para_code;
    }
    public String getPara_code(){
            return this.para_code;
    }
    public void setMod_code(String mod_code){
            support.firePropertyChange("mod_code", this.mod_code, mod_code);
            this.mod_code = mod_code;
    }
    public String getMod_code(){
            return this.mod_code;
    }
    public void setPara_name(String para_name){
            support.firePropertyChange("para_name", this.para_name, para_name);
            this.para_name = para_name;
    }
    public String getPara_name(){
            return this.para_name;
    }
    public void setPara_value(String para_value){
            support.firePropertyChange("para_value", this.para_value, para_value);
            this.para_value = para_value;
    }
    public String getPara_value(){
            return this.para_value;
    }
    public void setPara_type(Integer para_type){
            support.firePropertyChange("para_type", this.para_type, para_type);
            this.para_type = para_type;
    }
    public Integer getPara_type(){
            return this.para_type;
    }
    public void setPara_sql(String para_sql){
            support.firePropertyChange("para_sql", this.para_sql, para_sql);
            this.para_sql = para_sql;
    }
    public String getPara_sql(){
            return this.para_sql;
    }
    public void setPara_attr(Integer para_attr){
            support.firePropertyChange("para_attr", this.para_attr, para_attr);
            this.para_attr = para_attr;
    }
    public Integer getPara_attr(){
            return this.para_attr;
    }
    public void setDown_type(Integer down_type){
            support.firePropertyChange("down_type", this.down_type, down_type);
            this.down_type = down_type;
    }
    public Integer getDown_type(){
            return this.down_type;
    }
    public void setPara_option(String para_option){
            support.firePropertyChange("para_option", this.para_option, para_option);
            this.para_option = para_option;
    }
    public String getPara_option(){
            return this.para_option;
    }
    public void setData_type(Integer data_type){
            support.firePropertyChange("data_type", this.data_type, data_type);
            this.data_type = data_type;
    }
    public Integer getData_type(){
            return this.data_type;
    }
    public void setDescribe(String describe){
            support.firePropertyChange("describe", this.describe, describe);
            this.describe = describe;
    }
    public String getDescribe(){
            return this.describe;
    }
    public void setComp_code(String comp_code){
            support.firePropertyChange("comp_code", this.comp_code, comp_code);
            this.comp_code = comp_code;
    }
    public String getComp_code(){
            return this.comp_code;
    }
    public void setCopy_code(String copy_code){
            support.firePropertyChange("copy_code", this.copy_code, copy_code);
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }

}


