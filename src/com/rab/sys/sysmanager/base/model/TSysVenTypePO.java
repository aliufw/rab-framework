package com.rab.sys.sysmanager.base.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_ven_type
 * 业务实体说明：
 * 生成时间：2010-10-21 15:02:19
 * 版权所有：
 */
@IdClass(TSysVenTypePK.class)
@Entity
@Table(name = "t_sys_ven_type")
public class TSysVenTypePO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "ven_type_code" ,nullable=false, length=12)
    private String ven_type_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "comp_id" ,nullable=false, length=20)
    private String comp_id;

    /**
     */
    @Column(name = "ven_type_name" ,nullable=false, length=20)
    private String ven_type_name;

    /**
     */
    @Column(name = "sys_level" ,nullable=false, length=4)
    private Integer sys_level;

    /**
     */
    @Column(name = "spell" ,nullable=false, length=8)
    private String spell;

    /**
     */
    @Column(name = "is_last" ,nullable=true, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "is_stop" ,nullable=false, length=1)
    private Integer is_stop;


    public void setVen_type_code(String ven_type_code){
            support.firePropertyChange("ven_type_code", this.ven_type_code, ven_type_code);
            this.ven_type_code = ven_type_code;
    }
    public String getVen_type_code(){
            return this.ven_type_code;
    }
    public void setComp_id(String comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public String getComp_id(){
            return this.comp_id;
    }
    public void setVen_type_name(String ven_type_name){
            support.firePropertyChange("ven_type_name", this.ven_type_name, ven_type_name);
            this.ven_type_name = ven_type_name;
    }
    public String getVen_type_name(){
            return this.ven_type_name;
    }
    public void setSys_level(Integer sys_level){
            support.firePropertyChange("sys_level", this.sys_level, sys_level);
            this.sys_level = sys_level;
    }
    public Integer getSys_level(){
            return this.sys_level;
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

class TSysVenTypePK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ven_type_code;
    private String comp_id;
    public void setVen_type_code(String ven_type_code){
            this.ven_type_code = ven_type_code;
    }
    public String getVen_type_code(){
            return this.ven_type_code;
    }
    public void setComp_id(String comp_id){
            this.comp_id = comp_id;
    }
    public String getComp_id(){
            return this.comp_id;
    }

}

