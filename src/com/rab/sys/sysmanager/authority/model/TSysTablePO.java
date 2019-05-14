package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_table
 * 业务实体说明：
 * 生成时间：2010-10-23 12:45:18
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_table")
public class TSysTablePO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "table_id" ,nullable=false,length=50)
    private String table_id;

    /**
     */
    @Column(name = "table_name" ,nullable=false,length=20)
    private String table_name;

    /**
     */
    @Column(name = "code_field" ,nullable=true,length=20)
    private String code_field;

    /**
     */
    @Column(name = "name_field" ,nullable=true,length=20)
    private String name_field;

    /**
     */
    @Column(name = "level_field" ,nullable=true,length=20)
    private String level_field;

    /**
     */
    @Column(name = "handler_class" ,nullable=true,length=100)
    private String handler_class;

    /**
     */
    @Column(name = "is_disc" ,nullable=true,length=1)
    private Integer is_disc = new Integer(0);

    /**
     */
    @Column(name = "is_power" ,nullable=true,length=1)
    private Integer is_power = new Integer(0);

    /**
     */
    @Column(name = "table_level" ,nullable=false,length=3)
    private Integer table_level = new Integer(0);

    /**
     */
    @Column(name = "is_self" ,nullable=true,length=1)
    private Integer is_self = new Integer(0);

    /**
     */
    @Column(name = "is_year" ,nullable=true,length=1)
    private Integer is_year = new Integer(0);

    /**
     */
    @Column(name = "year_field" ,nullable=true,length=40)
    private String year_field = "0";

    /**
     */
    @Column(name = "year_type" ,nullable=true,length=3)
    private Integer year_type;

    /**
     */
    @Column(name = "is_view" ,nullable=true,length=1)
    private Integer is_view = new Integer(0);

    /**
     */
    @Column(name = "code_data_type" ,nullable=false,length=3)
    private Integer code_data_type = new Integer(1);

    /**
     */
    @Column(name = "is_stat" ,nullable=true,length=1)
    private Integer is_stat = new Integer(0);

    /**
     */
    @Column(name = "is_mod" ,nullable=true,length=1)
    private Integer is_mod = new Integer(0);

    /**
     */
    @Column(name = "mod_sql" ,nullable=true,length=50)
    private String mod_sql;


    public void setTable_id(String table_id){
            support.firePropertyChange("table_id", this.table_id, table_id);
            this.table_id = table_id;
    }
    public String getTable_id(){
            return this.table_id;
    }
    public void setTable_name(String table_name){
            support.firePropertyChange("table_name", this.table_name, table_name);
            this.table_name = table_name;
    }
    public String getTable_name(){
            return this.table_name;
    }
    public void setCode_field(String code_field){
            support.firePropertyChange("code_field", this.code_field, code_field);
            this.code_field = code_field;
    }
    public String getCode_field(){
            return this.code_field;
    }
    public void setName_field(String name_field){
            support.firePropertyChange("name_field", this.name_field, name_field);
            this.name_field = name_field;
    }
    public String getName_field(){
            return this.name_field;
    }
    public void setLevel_field(String level_field){
            support.firePropertyChange("level_field", this.level_field, level_field);
            this.level_field = level_field;
    }
    public String getLevel_field(){
            return this.level_field;
    }
    public void setHandler_class(String handler_class){
            support.firePropertyChange("handler_class", this.handler_class, handler_class);
            this.handler_class = handler_class;
    }
    public String getHandler_class(){
            return this.handler_class;
    }
    public void setIs_disc(Integer is_disc){
            support.firePropertyChange("is_disc", this.is_disc, is_disc);
            this.is_disc = is_disc;
    }
    public Integer getIs_disc(){
            return this.is_disc;
    }
    public void setIs_power(Integer is_power){
            support.firePropertyChange("is_power", this.is_power, is_power);
            this.is_power = is_power;
    }
    public Integer getIs_power(){
            return this.is_power;
    }
    public void setTable_level(Integer table_level){
            support.firePropertyChange("table_level", this.table_level, table_level);
            this.table_level = table_level;
    }
    public Integer getTable_level(){
            return this.table_level;
    }
    public void setIs_self(Integer is_self){
            support.firePropertyChange("is_self", this.is_self, is_self);
            this.is_self = is_self;
    }
    public Integer getIs_self(){
            return this.is_self;
    }
    public void setIs_year(Integer is_year){
            support.firePropertyChange("is_year", this.is_year, is_year);
            this.is_year = is_year;
    }
    public Integer getIs_year(){
            return this.is_year;
    }
    public void setYear_field(String year_field){
            support.firePropertyChange("year_field", this.year_field, year_field);
            this.year_field = year_field;
    }
    public String getYear_field(){
            return this.year_field;
    }
    public void setYear_type(Integer year_type){
            support.firePropertyChange("year_type", this.year_type, year_type);
            this.year_type = year_type;
    }
    public Integer getYear_type(){
            return this.year_type;
    }
    public void setIs_view(Integer is_view){
            support.firePropertyChange("is_view", this.is_view, is_view);
            this.is_view = is_view;
    }
    public Integer getIs_view(){
            return this.is_view;
    }
    public void setCode_data_type(Integer code_data_type){
            support.firePropertyChange("code_data_type", this.code_data_type, code_data_type);
            this.code_data_type = code_data_type;
    }
    public Integer getCode_data_type(){
            return this.code_data_type;
    }
    public void setIs_stat(Integer is_stat){
            support.firePropertyChange("is_stat", this.is_stat, is_stat);
            this.is_stat = is_stat;
    }
    public Integer getIs_stat(){
            return this.is_stat;
    }
    public void setIs_mod(Integer is_mod){
            support.firePropertyChange("is_mod", this.is_mod, is_mod);
            this.is_mod = is_mod;
    }
    public Integer getIs_mod(){
            return this.is_mod;
    }
    public void setMod_sql(String mod_sql){
            support.firePropertyChange("mod_sql", this.mod_sql, mod_sql);
            this.mod_sql = mod_sql;
    }
    public String getMod_sql(){
            return this.mod_sql;
    }

}


