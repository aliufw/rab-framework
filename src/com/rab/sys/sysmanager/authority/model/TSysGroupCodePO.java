package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * 业务实体表：t_sys_group_code
 * 业务实体说明：
 * 生成时间：2010-10-23 12:45:19
 * 版权所有：
 */
@IdClass(TSysGroupCodePK.class)
@Entity
@Table(name = "t_sys_group_code")
public class TSysGroupCodePO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "group_id" ,nullable=false,length=8)
    private Integer group_id;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "table_id" ,nullable=false,length=20)
    private String table_id;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "comp_id" ,nullable=true,length=8)
    private Integer comp_id;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "copy_code" ,nullable=false,length=3)
    private String copy_code;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Column(name = "acct_year" ,nullable=true,length=4)
    private String acct_year;

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "code" ,nullable=false,length=20)
    private String code;

    /**
     */
    @Column(name = "is_read" ,nullable=true,length=1)
    private Integer is_read ;

    /**
     */
    @Column(name = "is_write" ,nullable=true,length=1)
    private Integer is_write;
    
    @Transient
    private String column_code;
    
    public String getColumn_code() {
		return column_code;
	}
	public void setColumn_code(String columnCode) {
		column_code = columnCode;
	}
	public void setGroup_id(Integer group_id){
            support.firePropertyChange("group_id", this.group_id, group_id);
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }
    public void setTable_id(String table_id){
            support.firePropertyChange("table_id", this.table_id, table_id);
            this.table_id = table_id;
    }
    public String getTable_id(){
            return this.table_id;
    }
    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setCopy_code(String copy_code){
            support.firePropertyChange("copy_code", this.copy_code, copy_code);
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }
    public void setAcct_year(String acct_year){
            support.firePropertyChange("acct_year", this.acct_year, acct_year);
            this.acct_year = acct_year;
    }
    public String getAcct_year(){
            return this.acct_year;
    }
    public void setCode(String code){
            support.firePropertyChange("code", this.code, code);
            this.code = code;
    }
    public String getCode(){
            return this.code;
    }
    public void setIs_read(Integer is_read){
            support.firePropertyChange("is_read", this.is_read, is_read);
            this.is_read = is_read;
    }
    public Integer getIs_read(){
            return this.is_read;
    }
    public void setIs_write(Integer is_write){
            support.firePropertyChange("is_write", this.is_write, is_write);
            this.is_write = is_write;
    }
    public Integer getIs_write(){
            return this.is_write;
    }

}

class TSysGroupCodePK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer group_id;
    private String table_id;
    private Integer comp_id;
    private String copy_code;
    private String code;
    
    public void setGroup_id(Integer group_id){
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }
    public void setTable_id(String table_id){
            this.table_id = table_id;
    }
    public String getTable_id(){
            return this.table_id;
    }
    public void setComp_id(Integer comp_id){
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setCopy_code(String copy_code){
            this.copy_code = copy_code;
    }
    public String getCopy_code(){
            return this.copy_code;
    }

    public void setCode(String code){
            this.code = code;
    }
    public String getCode(){
            return this.code;
    }

}

