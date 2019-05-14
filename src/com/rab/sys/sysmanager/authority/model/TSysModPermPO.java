package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_mod_perm
 * 业务实体说明：
 * 生成时间：2010-10-18 14:53:15
 * 版权所有：
 */
@IdClass(TSysModPermPK.class)
@Entity
@Table(name = "t_sys_mod_perm")
public class TSysModPermPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "mod_code" ,nullable=false, length=20)
    private String mod_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "user_id" ,nullable=false, length=8)
    private Integer user_id;


    public void setMod_code(String mod_code){
            support.firePropertyChange("mod_code", this.mod_code, mod_code);
            this.mod_code = mod_code;
    }
    public String getMod_code(){
            return this.mod_code;
    }
    public void setUser_id(Integer user_id){
            support.firePropertyChange("user_id", this.user_id, user_id);
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }

}

class TSysModPermPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mod_code;
    private Integer user_id;
    public void setMod_code(String mod_code){
            this.mod_code = mod_code;
    }
    public String getMod_code(){
            return this.mod_code;
    }
    public void setUser_id(Integer user_id){
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }

}

