package com.rab.sys.sysmanager.info.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	java.math.BigDecimal;

/**
 * 业务实体表：t_sys_dept
 * 业务实体说明：
 * 生成时间：2010-10-22 10:06:20
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_dept")
public class TSysDeptPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "dept_id" ,nullable=false, length=8)
    private Integer dept_id;

    /**
     */
    @Column(name = "dept_code" ,nullable=false, length=20)
    private String dept_code;

    /**
     */
    @Column(name = "comp_id" ,nullable=false, length=8)
    private Integer comp_id;

    /**
     */
    @Column(name = "attr_code" ,nullable=true, length=20)
    private String attr_code;

    /**
     */
    @Column(name = "dept_name" ,nullable=false, length=40)
    private String dept_name;

    /**
     */
    @Column(name = "dept_name_all" ,nullable=true, length=100)
    private String dept_name_all;

    /**
     */
    @Column(name = "super_id" ,nullable=true, length=8)
    private Integer super_id;

    /**
     */
    @Column(name = "dept_level" ,nullable=false, length=2)
    private Integer dept_level;

    /**
     */
    @Column(name = "spell" ,nullable=true, length=20)
    private String spell;

    /**
     */
    @Column(name = "arrt_code" ,nullable=true, length=20)
    private String arrt_code;

    /**
     */
    @Column(name = "type_code" ,nullable=true, length=20)
    private String type_code;

    /**
     */
    @Column(name = "kind_code" ,nullable=false, length=20)
    private String kind_code;

    /**
     */
    @Column(name = "is_func" ,nullable=false, length=1)
    private Integer is_func;

    /**
     */
    @Column(name = "is_budg" ,nullable=false, length=1)
    private Integer is_budg;

    /**
     */
    @Column(name = "is_last" ,nullable=false, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "is_stock" ,nullable=false, length=1)
    private Integer is_stock;

    /**
     */
    @Column(name = "is_stop" ,nullable=false, length=1)
    private Integer is_stop;

    /**
     */
    @Column(name = "is_outer" ,nullable=true, length=1)
    private Integer is_outer;

    /**
     */
    @Column(name = "inout_type_code" ,nullable=true, length=20)
    private String inout_type_code;

    /**
     */
    @Column(name = "proportion" ,nullable=true, precision=3,scale=2 )
    private BigDecimal proportion;

    /**
     */
    @Column(name = "path_code" ,nullable=true, length=50)
    private String path_code;

    /**
     */
    @Column(name = "is_all" ,nullable=true, length=1)
    private Integer is_all;

    /**
     */
    @Column(name = "custom_code" ,nullable=true, length=50)
    private String custom_code;

    /**
     */
    @Column(name = "cbcs_dept" ,nullable=true, length=50)
    private String cbcs_dept;

    /**
     */
    @Column(name = "is_service" ,nullable=true, length=1)
    private Integer is_service;


    public void setDept_id(Integer dept_id){
            support.firePropertyChange("dept_id", this.dept_id, dept_id);
            this.dept_id = dept_id;
    }
    public Integer getDept_id(){
            return this.dept_id;
    }
    public void setDept_code(String dept_code){
            support.firePropertyChange("dept_code", this.dept_code, dept_code);
            this.dept_code = dept_code;
    }
    public String getDept_code(){
            return this.dept_code;
    }
    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setAttr_code(String attr_code){
            support.firePropertyChange("attr_code", this.attr_code, attr_code);
            this.attr_code = attr_code;
    }
    public String getAttr_code(){
            return this.attr_code;
    }
    public void setDept_name(String dept_name){
            support.firePropertyChange("dept_name", this.dept_name, dept_name);
            this.dept_name = dept_name;
    }
    public String getDept_name(){
            return this.dept_name;
    }
    public void setDept_name_all(String dept_name_all){
            support.firePropertyChange("dept_name_all", this.dept_name_all, dept_name_all);
            this.dept_name_all = dept_name_all;
    }
    public String getDept_name_all(){
            return this.dept_name_all;
    }
    public void setSuper_id(Integer super_id){
            support.firePropertyChange("super_id", this.super_id, super_id);
            this.super_id = super_id;
    }
    public Integer getSuper_id(){
            return this.super_id;
    }
    public void setDept_level(Integer dept_level){
            support.firePropertyChange("dept_level", this.dept_level, dept_level);
            this.dept_level = dept_level;
    }
    public Integer getDept_level(){
            return this.dept_level;
    }
    public void setSpell(String spell){
            support.firePropertyChange("spell", this.spell, spell);
            this.spell = spell;
    }
    public String getSpell(){
            return this.spell;
    }
    public void setArrt_code(String arrt_code){
            support.firePropertyChange("arrt_code", this.arrt_code, arrt_code);
            this.arrt_code = arrt_code;
    }
    public String getArrt_code(){
            return this.arrt_code;
    }
    public void setType_code(String type_code){
            support.firePropertyChange("type_code", this.type_code, type_code);
            this.type_code = type_code;
    }
    public String getType_code(){
            return this.type_code;
    }
    public void setKind_code(String kind_code){
            support.firePropertyChange("kind_code", this.kind_code, kind_code);
            this.kind_code = kind_code;
    }
    public String getKind_code(){
            return this.kind_code;
    }
    public void setIs_func(Integer is_func){
            support.firePropertyChange("is_func", this.is_func, is_func);
            this.is_func = is_func;
    }
    public Integer getIs_func(){
            return this.is_func;
    }
    public void setIs_budg(Integer is_budg){
            support.firePropertyChange("is_budg", this.is_budg, is_budg);
            this.is_budg = is_budg;
    }
    public Integer getIs_budg(){
            return this.is_budg;
    }
    public void setIs_last(Integer is_last){
            support.firePropertyChange("is_last", this.is_last, is_last);
            this.is_last = is_last;
    }
    public Integer getIs_last(){
            return this.is_last;
    }
    public void setIs_stock(Integer is_stock){
            support.firePropertyChange("is_stock", this.is_stock, is_stock);
            this.is_stock = is_stock;
    }
    public Integer getIs_stock(){
            return this.is_stock;
    }
    public void setIs_stop(Integer is_stop){
            support.firePropertyChange("is_stop", this.is_stop, is_stop);
            this.is_stop = is_stop;
    }
    public Integer getIs_stop(){
            return this.is_stop;
    }
    public void setIs_outer(Integer is_outer){
            support.firePropertyChange("is_outer", this.is_outer, is_outer);
            this.is_outer = is_outer;
    }
    public Integer getIs_outer(){
            return this.is_outer;
    }
    public void setInout_type_code(String inout_type_code){
            support.firePropertyChange("inout_type_code", this.inout_type_code, inout_type_code);
            this.inout_type_code = inout_type_code;
    }
    public String getInout_type_code(){
            return this.inout_type_code;
    }
    public void setProportion(BigDecimal proportion){
            support.firePropertyChange("proportion", this.proportion, proportion);
            this.proportion = proportion;
    }
    public BigDecimal getProportion(){
            return this.proportion;
    }
    public void setPath_code(String path_code){
            support.firePropertyChange("path_code", this.path_code, path_code);
            this.path_code = path_code;
    }
    public String getPath_code(){
            return this.path_code;
    }
    public void setIs_all(Integer is_all){
            support.firePropertyChange("is_all", this.is_all, is_all);
            this.is_all = is_all;
    }
    public Integer getIs_all(){
            return this.is_all;
    }
    public void setCustom_code(String custom_code){
            support.firePropertyChange("custom_code", this.custom_code, custom_code);
            this.custom_code = custom_code;
    }
    public String getCustom_code(){
            return this.custom_code;
    }
    public void setCbcs_dept(String cbcs_dept){
            support.firePropertyChange("cbcs_dept", this.cbcs_dept, cbcs_dept);
            this.cbcs_dept = cbcs_dept;
    }
    public String getCbcs_dept(){
            return this.cbcs_dept;
    }
    public void setIs_service(Integer is_service){
            support.firePropertyChange("is_service", this.is_service, is_service);
            this.is_service = is_service;
    }
    public Integer getIs_service(){
            return this.is_service;
    }

}


