package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_group
 * 业务实体说明：
 * 生成时间：2010-10-25 18:06:25
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_group")
public class TSysGroupPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "group_id" ,nullable=false, length=8)
    private Integer group_id;

    /**
     */
    @Column(name = "group_code" ,nullable=false, length=20)
    private String group_code;

    /**
     */
    @Column(name = "group_name" ,nullable=false, length=20)
    private String group_name;

    /**
     */
    @Column(name = "group_desc" ,nullable=true, length=40)
    private String group_desc;

    /**
     */
    @Column(name = "dba_id" ,nullable=false, length=8)
    private Integer dba_id;


    public void setGroup_id(Integer group_id){
            support.firePropertyChange("group_id", this.group_id, group_id);
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }
    public void setGroup_code(String group_code){
            support.firePropertyChange("group_code", this.group_code, group_code);
            this.group_code = group_code;
    }
    public String getGroup_code(){
            return this.group_code;
    }
    public void setGroup_name(String group_name){
            support.firePropertyChange("group_name", this.group_name, group_name);
            this.group_name = group_name;
    }
    public String getGroup_name(){
            return this.group_name;
    }
    public void setGroup_desc(String group_desc){
            support.firePropertyChange("group_desc", this.group_desc, group_desc);
            this.group_desc = group_desc;
    }
    public String getGroup_desc(){
            return this.group_desc;
    }
    public void setDba_id(Integer dba_id){
            support.firePropertyChange("dba_id", this.dba_id, dba_id);
            this.dba_id = dba_id;
    }
    public Integer getDba_id(){
            return this.dba_id;
    }

}


