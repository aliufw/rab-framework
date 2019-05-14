package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_user_perm
 * 业务实体说明：
 * 生成时间：2010-10-21 16:11:51
 * 版权所有：
 */
@IdClass(TSysUserPermPK.class)
@Entity
@Table(name = "t_sys_user_perm")
public class TSysUserPermPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "user_id" ,nullable=false, length=8)
    private Integer user_id;

    @Column(name = "copy_code" ,nullable=true, length=3)
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


    public void setUser_id(Integer user_id){
            support.firePropertyChange("user_id", this.user_id, user_id);
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
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

class TSysUserPermPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer user_id;
    private Integer func_id;
    public void setUser_id(Integer user_id){
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }
    public void setFunc_id(Integer func_id){
            this.func_id = func_id;
    }
    public Integer getFunc_id(){
            return this.func_id;
    }

}

