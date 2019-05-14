package com.rab.sys.sysmanager.info.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_acct_subj
 * 业务实体说明：
 * 生成时间：2010-11-12 09:05:22
 * 版权所有：
 */
@IdClass(TSysAcctSubjPK.class)
@Entity
@Table(name = "t_sys_acct_subj")
public class SysAcctSubjPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "co_code" ,nullable=false, length=10)
    private String co_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "acct_subj_code" ,nullable=false, length=30)
    private String acct_subj_code;

    /**
     */
    @Column(name = "super_code" ,nullable=true, length=30)
    private String super_code;

    /**
     */
    @Column(name = "acct_subj_name" ,nullable=false, length=50)
    private String acct_subj_name;

    /**
     */
    @Column(name = "acct_subj_name_all" ,nullable=false, length=100)
    private String acct_subj_name_all;

    /**
     */
    @Column(name = "subj_type_code" ,nullable=false, length=20)
    private String subj_type_code;

    /**
     */
    @Column(name = "subj_nature_code" ,nullable=true, length=20)
    private String subj_nature_code;

    /**
     */
    @Column(name = "subj_level" ,nullable=false, length=3)
    private Integer subj_level;

    /**
     */
    @Column(name = "is_last" ,nullable=true, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "direction" ,nullable=false, length=1)
    private Integer direction;


    public void setCo_code(String co_code){
            support.firePropertyChange("co_code", this.co_code, co_code);
            this.co_code = co_code;
    }
    public String getCo_code(){
            return this.co_code;
    }
    public void setAcct_subj_code(String acct_subj_code){
            support.firePropertyChange("acct_subj_code", this.acct_subj_code, acct_subj_code);
            this.acct_subj_code = acct_subj_code;
    }
    public String getAcct_subj_code(){
            return this.acct_subj_code;
    }
    public void setSuper_code(String super_code){
            support.firePropertyChange("super_code", this.super_code, super_code);
            this.super_code = super_code;
    }
    public String getSuper_code(){
            return this.super_code;
    }
    public void setAcct_subj_name(String acct_subj_name){
            support.firePropertyChange("acct_subj_name", this.acct_subj_name, acct_subj_name);
            this.acct_subj_name = acct_subj_name;
    }
    public String getAcct_subj_name(){
            return this.acct_subj_name;
    }
    public void setAcct_subj_name_all(String acct_subj_name_all){
            support.firePropertyChange("acct_subj_name_all", this.acct_subj_name_all, acct_subj_name_all);
            this.acct_subj_name_all = acct_subj_name_all;
    }
    public String getAcct_subj_name_all(){
            return this.acct_subj_name_all;
    }
    public void setSubj_type_code(String subj_type_code){
            support.firePropertyChange("subj_type_code", this.subj_type_code, subj_type_code);
            this.subj_type_code = subj_type_code;
    }
    public String getSubj_type_code(){
            return this.subj_type_code;
    }
    public void setSubj_nature_code(String subj_nature_code){
            support.firePropertyChange("subj_nature_code", this.subj_nature_code, subj_nature_code);
            this.subj_nature_code = subj_nature_code;
    }
    public String getSubj_nature_code(){
            return this.subj_nature_code;
    }
    public void setSubj_level(Integer subj_level){
            support.firePropertyChange("subj_level", this.subj_level, subj_level);
            this.subj_level = subj_level;
    }
    public Integer getSubj_level(){
            return this.subj_level;
    }
    public void setIs_last(Integer is_last){
            support.firePropertyChange("is_last", this.is_last, is_last);
            this.is_last = is_last;
    }
    public Integer getIs_last(){
            return this.is_last;
    }
    public void setDirection(Integer direction){
            support.firePropertyChange("direction", this.direction, direction);
            this.direction = direction;
    }
    public Integer getDirection(){
            return this.direction;
    }

}

class TSysAcctSubjPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String co_code;
    private String acct_subj_code;
    public void setCo_code(String co_code){
            this.co_code = co_code;
    }
    public String getCo_code(){
            return this.co_code;
    }
    public void setAcct_subj_code(String acct_subj_code){
            this.acct_subj_code = acct_subj_code;
    }
    public String getAcct_subj_code(){
            return this.acct_subj_code;
    }

}

