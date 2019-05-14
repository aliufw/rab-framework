package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_group_perm
 * 业务实体说明：
 * 生成时间：2010-10-21 16:09:49
 * 版权所有：
 */
@IdClass(TSysGroupPermPK.class)
@Entity
@Table(name = "t_sys_group_perm")
public class TSysGroupPermPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "group_id" ,nullable=false, length=8)
    private Integer group_id;
 
    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "copy_code" ,nullable=false, length=3)
    private String copy_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "func_id" ,nullable=false, length=6)
    private Integer func_id;

    /**
     */
    @Column(name = "mod_code" ,nullable=true, length=20)
    private String mod_code;


    public void setGroup_id(Integer group_id){
            support.firePropertyChange("group_id", this.group_id, group_id);
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }
    public void setCopy_code(String copy_code){
            support.firePropertyChange("copy_code", this.copy_code, copy_code);
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setFunc_id(Integer func_id){
            support.firePropertyChange("func_id", this.func_id, func_id);
            this.func_id = func_id;
    }
    public Integer getFunc_id(){
            return this.func_id;
    }
    public void setMod_code(String mod_code){
            support.firePropertyChange("mod_code", this.mod_code, mod_code);
            this.mod_code = mod_code;
    }
    public String getMod_code(){
            return this.mod_code;
    }

}

class TSysGroupPermPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer group_id;
    private String copy_code;
    private Integer func_id;
    public void setGroup_id(Integer group_id){
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }
    public void setCopy_code(String copy_code){
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setFunc_id(Integer func_id){
            this.func_id = func_id;
    }
    public Integer getFunc_id(){
            return this.func_id;
    }

}

