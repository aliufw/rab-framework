package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_store_dict
 * 业务实体说明：
 * 生成时间：2010-10-21 15:02:19
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_store_dict")
public class TSysStoreDictPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "store_code" ,nullable=false, length=20)
    private String store_code;

    /**
     */
    @Column(name = "comp_id" ,nullable=false, length=8)
    private Integer comp_id;

    /**
     */
    @Column(name = "dept_id" ,nullable=true, length=8)
    private Integer dept_id;

    /**
     */
    @Column(name = "store_name" ,nullable=false, length=12)
    private String store_name;

    /**
     */
    @Column(name = "super_code" ,nullable=true, length=20)
    private String super_code;

    /**
     */
    @Column(name = "spell" ,nullable=true, length=8)
    private String spell;

    /**
     */
    @Column(name = "address" ,nullable=true, length=100)
    private String address;

    /**
     */
    @Column(name = "tel" ,nullable=true, length=20)
    private String tel;

    /**
     */
    @Column(name = "mobile" ,nullable=true, length=12)
    private String mobile;

    /**
     */
    @Column(name = "emp_code" ,nullable=true, length=40)
    private String emp_code;

    /**
     */
    @Column(name = "ispos" ,nullable=false, length=1)
    private Integer ispos;

    /**
     */
    @Column(name = "wh_flag" ,nullable=false, length=2)
    private Integer wh_flag;

    /**
     */
    @Column(name = "store_type" ,nullable=false, length=10)
    private String store_type;

    /**
     */
    @Column(name = "store_level" ,nullable=false, length=2)
    private Integer store_level;

    /**
     */
    @Column(name = "store_flag" ,nullable=true, length=3)
    private String store_flag;

    /**
     */
    @Column(name = "is_last" ,nullable=false, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "is_stop" ,nullable=false, length=1)
    private Integer is_stop;

    /**
     */
    @Column(name = "m_note" ,nullable=true, length=50)
    private String m_note;


    public void setStore_code(String store_code){
            support.firePropertyChange("store_code", this.store_code, store_code);
            this.store_code = store_code;
    }
    public String getStore_code(){
            return this.store_code;
    }
    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setDept_id(Integer dept_id){
            support.firePropertyChange("dept_id", this.dept_id, dept_id);
            this.dept_id = dept_id;
    }
    public Integer getDept_id(){
            return this.dept_id;
    }
    public void setStore_name(String store_name){
            support.firePropertyChange("store_name", this.store_name, store_name);
            this.store_name = store_name;
    }
    public String getStore_name(){
            return this.store_name;
    }
    public void setSuper_code(String super_code){
            support.firePropertyChange("super_code", this.super_code, super_code);
            this.super_code = super_code;
    }
    public String getSuper_code(){
            return this.super_code;
    }
    public void setSpell(String spell){
            support.firePropertyChange("spell", this.spell, spell);
            this.spell = spell;
    }
    public String getSpell(){
            return this.spell;
    }
    public void setAddress(String address){
            support.firePropertyChange("address", this.address, address);
            this.address = address;
    }
    public String getAddress(){
            return this.address;
    }
    public void setTel(String tel){
            support.firePropertyChange("tel", this.tel, tel);
            this.tel = tel;
    }
    public String getTel(){
            return this.tel;
    }
    public void setMobile(String mobile){
            support.firePropertyChange("mobile", this.mobile, mobile);
            this.mobile = mobile;
    }
    public String getMobile(){
            return this.mobile;
    }
    public void setEmp_code(String emp_code){
            support.firePropertyChange("emp_code", this.emp_code, emp_code);
            this.emp_code = emp_code;
    }
    public String getEmp_code(){
            return this.emp_code;
    }
    public void setIspos(Integer ispos){
            support.firePropertyChange("ispos", this.ispos, ispos);
            this.ispos = ispos;
    }
    public Integer getIspos(){
            return this.ispos;
    }
    public void setWh_flag(Integer wh_flag){
            support.firePropertyChange("wh_flag", this.wh_flag, wh_flag);
            this.wh_flag = wh_flag;
    }
    public Integer getWh_flag(){
            return this.wh_flag;
    }
    public void setStore_type(String store_type){
            support.firePropertyChange("store_type", this.store_type, store_type);
            this.store_type = store_type;
    }
    public String getStore_type(){
            return this.store_type;
    }
    public void setStore_level(Integer store_level){
            support.firePropertyChange("store_level", this.store_level, store_level);
            this.store_level = store_level;
    }
    public Integer getStore_level(){
            return this.store_level;
    }
    public void setStore_flag(String store_flag){
            support.firePropertyChange("store_flag", this.store_flag, store_flag);
            this.store_flag = store_flag;
    }
    public String getStore_flag(){
            return this.store_flag;
    }
    public void setIs_last(Integer is_last){
            support.firePropertyChange("is_last", this.is_last, is_last);
            this.is_last = is_last;
    }
    public Integer getIs_last(){
            return this.is_last;
    }
    public void setIs_stop(Integer is_stop){
            support.firePropertyChange("is_stop", this.is_stop, is_stop);
            this.is_stop = is_stop;
    }
    public Integer getIs_stop(){
            return this.is_stop;
    }
    public void setM_note(String m_note){
            support.firePropertyChange("m_note", this.m_note, m_note);
            this.m_note = m_note;
    }
    public String getM_note(){
            return this.m_note;
    }

}


