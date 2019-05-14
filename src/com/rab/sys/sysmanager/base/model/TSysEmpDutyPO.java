package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_emp_duty
 * 业务实体说明：
 * 生成时间：2010-10-21 15:02:19
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_emp_duty")
public class TSysEmpDutyPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "duty_code" ,nullable=false, length=20)
    private String duty_code;

    /**
     */
    @Column(name = "duty_name" ,nullable=false, length=20)
    private String duty_name;

    /**
     */
    @Column(name = "duty_desc" ,nullable=true, length=40)
    private String duty_desc;

    /**
     */
    @Column(name = "duty_level" ,nullable=false, length=2)
    private Integer duty_level;

    /**
     */
    @Column(name = "spell" ,nullable=true, length=8)
    private String spell;

    /**
     */
    @Column(name = "is_last" ,nullable=false, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "is_stop" ,nullable=false, length=1)
    private Integer is_stop;


    public void setDuty_code(String duty_code){
            support.firePropertyChange("duty_code", this.duty_code, duty_code);
            this.duty_code = duty_code;
    }
    public String getDuty_code(){
            return this.duty_code;
    }
    public void setDuty_name(String duty_name){
            support.firePropertyChange("duty_name", this.duty_name, duty_name);
            this.duty_name = duty_name;
    }
    public String getDuty_name(){
            return this.duty_name;
    }
    public void setDuty_desc(String duty_desc){
            support.firePropertyChange("duty_desc", this.duty_desc, duty_desc);
            this.duty_desc = duty_desc;
    }
    public String getDuty_desc(){
            return this.duty_desc;
    }
    public void setDuty_level(Integer duty_level){
            support.firePropertyChange("duty_level", this.duty_level, duty_level);
            this.duty_level = duty_level;
    }
    public Integer getDuty_level(){
            return this.duty_level;
    }
    public void setSpell(String spell){
            support.firePropertyChange("spell", this.spell, spell);
            this.spell = spell;
    }
    public String getSpell(){
            return this.spell;
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

}


