package com.rab.sys.sysmanager.dict.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_xt_hcbxx
 * 业务实体说明：
 * 生成时间：2010-10-25 16:46:55
 * 版权所有：
 */
@Entity
@Table(name = "t_xt_hcbxx")
public class TXtHcbxxPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "bm_mc" ,nullable=false,length=20)
	
    private String bm_mc;

    /**
     */
    @Column(name = "gx_xh" ,nullable=true,length=6)
	
    private Integer gx_xh;

    /**
     */
    @Column(name = "cachetype" ,nullable=true,length=1)
	
    private Integer cachetype;

    /**
     */
    @Column(name = "bm_ms" ,nullable=true,length=20)
	
    private String bm_ms;

    /**
     */
    @Column(name = "orderby" ,nullable=true,length=30)
	
    private String orderby;

    /**
     */
    @Column(name = "descbj" ,nullable=true,length=1)
	
    private Integer descbj;

    /**
     */
    @Column(name = "codename" ,nullable=false,length=20)
	
    private String codename;

    /**
     */
    @Column(name = "valuename" ,nullable=false,length=20)
	
    private String valuename;


    public void setBm_mc(String bm_mc){
            support.firePropertyChange("bm_mc", this.bm_mc, bm_mc);
            this.bm_mc = bm_mc;
    }
    public String getBm_mc(){
            return this.bm_mc;
    }
    public void setGx_xh(Integer gx_xh){
            support.firePropertyChange("gx_xh", this.gx_xh, gx_xh);
            this.gx_xh = gx_xh;
    }
    public Integer getGx_xh(){
            return this.gx_xh;
    }
    public void setCachetype(Integer cachetype){
            support.firePropertyChange("cachetype", this.cachetype, cachetype);
            this.cachetype = cachetype;
    }
    public Integer getCachetype(){
            return this.cachetype;
    }
    public void setBm_ms(String bm_ms){
            support.firePropertyChange("bm_ms", this.bm_ms, bm_ms);
            this.bm_ms = bm_ms;
    }
    public String getBm_ms(){
            return this.bm_ms;
    }
    public void setOrderby(String orderby){
            support.firePropertyChange("orderby", this.orderby, orderby);
            this.orderby = orderby;
    }
    public String getOrderby(){
            return this.orderby;
    }
    public void setDescbj(Integer descbj){
            support.firePropertyChange("descbj", this.descbj, descbj);
            this.descbj = descbj;
    }
    public Integer getDescbj(){
            return this.descbj;
    }
    public void setCodename(String codename){
            support.firePropertyChange("codename", this.codename, codename);
            this.codename = codename;
    }
    public String getCodename(){
            return this.codename;
    }
    public void setValuename(String valuename){
            support.firePropertyChange("valuename", this.valuename, valuename);
            this.valuename = valuename;
    }
    public String getValuename(){
            return this.valuename;
    }

}


