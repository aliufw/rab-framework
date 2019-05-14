package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_user
 * 业务实体说明：用户表
 * 生成时间：2010-10-29 10:03:28
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_user")
public class TSysUserPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "user_id" ,nullable=false, length=8)
    private Integer user_id;

    /**
     */
    @Column(name = "user_code" ,nullable=false, length=20)
    private String user_code;

    /**
     */
    @Column(name = "user_name" ,nullable=false, length=40)
    private String user_name;

    /**
     */
    @Column(name = "password" ,nullable=false, length=15)
    private String password;

    /**
     */
    @Column(name = "user_desc" ,nullable=true, length=40)
    private String user_desc;

    /**
     */
    @Column(name = "sj_id" ,nullable=false, length=8)
    private Integer sj_id;

    /**
     */
    @Column(name = "is_dba" ,nullable=true, length=1)
    private Integer is_dba;

    /**
     */
    @Column(name = "emp_id" ,nullable=true, length=8)
    private Integer emp_id;

    /**
     */
    @Column(name = "is_stop" ,nullable=true, length=1)
    private Integer is_stop;

    /**
     */
    @Column(name = "scbj" ,nullable=true, length=1)
    private Integer scbj;


    public void setUser_id(Integer user_id){
            support.firePropertyChange("user_id", this.user_id, user_id);
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }
    public void setUser_code(String user_code){
            support.firePropertyChange("user_code", this.user_code, user_code);
            this.user_code = user_code;
    }
    public String getUser_code(){
            return this.user_code;
    }
    public void setUser_name(String user_name){
            support.firePropertyChange("user_name", this.user_name, user_name);
            this.user_name = user_name;
    }
    public String getUser_name(){
            return this.user_name;
    }
    public void setPassword(String password){
            support.firePropertyChange("password", this.password, password);
            this.password = password;
    }
    public String getPassword(){
            return this.password;
    }
    public void setUser_desc(String user_desc){
            support.firePropertyChange("user_desc", this.user_desc, user_desc);
            this.user_desc = user_desc;
    }
    public String getUser_desc(){
            return this.user_desc;
    }
    public void setSj_id(Integer sj_id){
            support.firePropertyChange("sj_id", this.sj_id, sj_id);
            this.sj_id = sj_id;
    }
    public Integer getSj_id(){
            return this.sj_id;
    }
    public void setIs_dba(Integer is_dba){
            support.firePropertyChange("is_dba", this.is_dba, is_dba);
            this.is_dba = is_dba;
    }
    public Integer getIs_dba(){
            return this.is_dba;
    }
    public void setEmp_id(Integer emp_id){
            support.firePropertyChange("emp_id", this.emp_id, emp_id);
            this.emp_id = emp_id;
    }
    public Integer getEmp_id(){
            return this.emp_id;
    }
    public void setIs_stop(Integer is_stop){
            support.firePropertyChange("is_stop", this.is_stop, is_stop);
            this.is_stop = is_stop;
    }
    public Integer getIs_stop(){
            return this.is_stop;
    }
    public void setScbj(Integer scbj){
            support.firePropertyChange("scbj", this.scbj, scbj);
            this.scbj = scbj;
    }
    public Integer getScbj(){
            return this.scbj;
    }

}


