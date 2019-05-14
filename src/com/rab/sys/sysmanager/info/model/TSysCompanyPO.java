package com.rab.sys.sysmanager.info.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

/**
 * 业务实体表：t_sys_company
 * 业务实体说明：
 * 生成时间：2010-10-26 16:54:17
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_company")
public class TSysCompanyPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：
     */
    @Id
    @Column(name = "comp_id" ,nullable=false, length=8)
    private Integer comp_id;

    /**
     */
    @Column(name = "comp_code" ,nullable=false, length=20)
    private String comp_code;

    /**
     */
    @Column(name = "super_id" ,nullable=true, length=8)
    private Integer super_id;

    /**
     */
    @Column(name = "comp_name" ,nullable=false, length=60)
    private String comp_name;

    /**
     */
    @Column(name = "prov" ,nullable=true, length=20)
    private String prov;

    /**
     */
    @Column(name = "city" ,nullable=true, length=20)
    private String city;

    /**
     */
    @Column(name = "is_count" ,nullable=false, length=1)
    private Integer is_count;

    /**
     */
    @Column(name = "address" ,nullable=true, length=100)
    private String address;

    /**
     */
    @Column(name = "dis_email" ,nullable=true, length=30)
    private String dis_email;

    /**
     */
    @Column(name = "link_phone" ,nullable=true, length=20)
    private String link_phone;

    /**
     */
    @Column(name = "linkman" ,nullable=true, length=40)
    private String linkman;

    /**
     */
    @Column(name = "tax_no" ,nullable=true, length=20)
    private String tax_no;

    /**
     */
    @Column(name = "comp_leader" ,nullable=true, length=40)
    private String comp_leader;

    /**
     */
    @Column(name = "acc_manager" ,nullable=true, length=40)
    private String acc_manager;

    /**
     */
    @Column(name = "level_code" ,nullable=false, length=200)
    private String level_code;

    /**
     */
    @Column(name = "is_last" ,nullable=false, length=1)
    private Integer is_last;

    /**
     */
    @Column(name = "spell" ,nullable=true, length=8)
    private String spell;

    /**
     */
    @Column(name = "is_stop" ,nullable=true, length=1)
    private Integer is_stop;


    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }
    public void setComp_code(String comp_code){
            support.firePropertyChange("comp_code", this.comp_code, comp_code);
            this.comp_code = comp_code;
    }
    public String getComp_code(){
            return this.comp_code;
    }
    public void setSuper_id(Integer super_id){
            support.firePropertyChange("super_id", this.super_id, super_id);
            this.super_id = super_id;
    }
    public Integer getSuper_id(){
            return this.super_id;
    }
    public void setComp_name(String comp_name){
            support.firePropertyChange("comp_name", this.comp_name, comp_name);
            this.comp_name = comp_name;
    }
    public String getComp_name(){
            return this.comp_name;
    }
    public void setProv(String prov){
            support.firePropertyChange("prov", this.prov, prov);
            this.prov = prov;
    }
    public String getProv(){
            return this.prov;
    }
    public void setCity(String city){
            support.firePropertyChange("city", this.city, city);
            this.city = city;
    }
    public String getCity(){
            return this.city;
    }
    public void setIs_count(Integer is_count){
            support.firePropertyChange("is_count", this.is_count, is_count);
            this.is_count = is_count;
    }
    public Integer getIs_count(){
            return this.is_count;
    }
    public void setAddress(String address){
            support.firePropertyChange("address", this.address, address);
            this.address = address;
    }
    public String getAddress(){
            return this.address;
    }
    public void setDis_email(String dis_email){
            support.firePropertyChange("dis_email", this.dis_email, dis_email);
            this.dis_email = dis_email;
    }
    public String getDis_email(){
            return this.dis_email;
    }
    public void setLink_phone(String link_phone){
            support.firePropertyChange("link_phone", this.link_phone, link_phone);
            this.link_phone = link_phone;
    }
    public String getLink_phone(){
            return this.link_phone;
    }
    public void setLinkman(String linkman){
            support.firePropertyChange("linkman", this.linkman, linkman);
            this.linkman = linkman;
    }
    public String getLinkman(){
            return this.linkman;
    }
    public void setTax_no(String tax_no){
            support.firePropertyChange("tax_no", this.tax_no, tax_no);
            this.tax_no = tax_no;
    }
    public String getTax_no(){
            return this.tax_no;
    }
    public void setComp_leader(String comp_leader){
            support.firePropertyChange("comp_leader", this.comp_leader, comp_leader);
            this.comp_leader = comp_leader;
    }
    public String getComp_leader(){
            return this.comp_leader;
    }
    public void setAcc_manager(String acc_manager){
            support.firePropertyChange("acc_manager", this.acc_manager, acc_manager);
            this.acc_manager = acc_manager;
    }
    public String getAcc_manager(){
            return this.acc_manager;
    }
    public void setLevel_code(String level_code){
            support.firePropertyChange("level_code", this.level_code, level_code);
            this.level_code = level_code;
    }
    public String getLevel_code(){
            return this.level_code;
    }
    public void setIs_last(Integer is_last){
            support.firePropertyChange("is_last", this.is_last, is_last);
            this.is_last = is_last;
    }
    public Integer getIs_last(){
            return this.is_last;
    }
    public void setSpell(String spell){
            support.firePropertyChange("spell", this.spell, spell);
            this.spell = spell;
    }
    public String getSpell(){
            return this.spell;
    }
    public void setIs_stop(Integer is_stop){
            support.firePropertyChange("is_stop", this.is_stop, is_stop);
            this.is_stop = is_stop;
    }
    public Integer getIs_stop(){
            return this.is_stop;
    }

}


