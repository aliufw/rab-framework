package com.rab.sys.sysmanager.info.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.Temporal;
import 	javax.persistence.TemporalType;
import 	java.util.Calendar;

/**
 * 业务实体表：t_sys_emp
 * 业务实体说明：
 * 生成时间：2010-10-20 14:04:58
 * 版权所有：
 */
@Entity
@Table(name = "t_sys_emp")
public class TSysEmpPO extends BasePersistenceObject{

    /**
     * 是否主键：是
     * 关联子表信息：【t_mate_dept_app_main : emp_id】  【t_mate_out_main : emp_id】  
     */
    @Id
    @Column(name = "emp_id" ,nullable=false, length=8)
    private Integer emp_id;

    /**
     */
    @Column(name = "emp_code" ,nullable=false, length=20)
    private String emp_code;

    /**
     */
    @Column(name = "emp_name" ,nullable=false, length=40)
    private String emp_name;

    /**
     */
    @Column(name = "id_code" ,nullable=true, length=18)
    private String id_code;

    /**
     */
    @Column(name = "emp_in_day" ,nullable=true, length=7)
    private Calendar emp_in_day;

    /**
     */
    @Column(name = "emp_birthday" ,nullable=true, length=7)
    private Calendar emp_birthday;

    /**
     */
    @Column(name = "emp_sex" ,nullable=false, length=1)
    private String emp_sex;

    /**
     */
    @Column(name = "nation" ,nullable=true, length=1)
    private Integer nation;

    /**
     */
    @Column(name = "dept_id" ,nullable=false, length=8)
    private Integer dept_id;

    /**
     */
    @Column(name = "title_code" ,nullable=true, length=20)
    private String title_code;

    /**
     */
    @Column(name = "duty_code" ,nullable=true, length=20)
    private String duty_code;

    /**
     */
    @Column(name = "emp_degree_code" ,nullable=true, length=20)
    private String emp_degree_code;

    /**
     */
    @Column(name = "work_phone" ,nullable=true, length=20)
    private String work_phone;

    /**
     */
    @Column(name = "mobile_phone" ,nullable=true, length=20)
    private String mobile_phone;

    /**
     */
    @Column(name = "email" ,nullable=true, length=30)
    private String email;

    /**
     */
    @Column(name = "bank_account" ,nullable=true, length=40)
    private String bank_account;

    /**
     */
    @Column(name = "is_stock" ,nullable=false, length=1)
    private Integer is_stock;

    /**
     */
    @Column(name = "spell" ,nullable=true, length=20)
    private String spell;

    /**
     */
    @Column(name = "emp_desc" ,nullable=true, length=200)
    private String emp_desc;

    /**
     */
    @Column(name = "pay_way" ,nullable=true, length=20)
    private String pay_way;

    /**
     */
    @Column(name = "is_pay" ,nullable=true, length=1)
    private Integer is_pay;

    /**
     */
    @Column(name = "nature_code" ,nullable=true, length=20)
    private String nature_code;

    /**
     */
    @Column(name = "emp_pol_code" ,nullable=true, length=20)
    private String emp_pol_code;

    /**
     */
    @Column(name = "emp_type_code" ,nullable=true, length=20)
    private String emp_type_code;

    /**
     */
    @Column(name = "image_path" ,nullable=true, length=50)
    private String image_path;

    /**
     */
    @Column(name = "password" ,nullable=true, length=20)
    private String password;

    /**
     */
    @Column(name = "bank_code" ,nullable=true, length=20)
    private String bank_code;

    /**
     */
    @Column(name = "is_stop" ,nullable=true, length=1)
    private Integer is_stop;


    public void setEmp_id(Integer emp_id){
            support.firePropertyChange("emp_id", this.emp_id, emp_id);
            this.emp_id = emp_id;
    }
    public Integer getEmp_id(){
            return this.emp_id;
    }
    public void setEmp_code(String emp_code){
            support.firePropertyChange("emp_code", this.emp_code, emp_code);
            this.emp_code = emp_code;
    }
    public String getEmp_code(){
            return this.emp_code;
    }
    public void setEmp_name(String emp_name){
            support.firePropertyChange("emp_name", this.emp_name, emp_name);
            this.emp_name = emp_name;
    }
    public String getEmp_name(){
            return this.emp_name;
    }
    public void setId_code(String id_code){
            support.firePropertyChange("id_code", this.id_code, id_code);
            this.id_code = id_code;
    }
    public String getId_code(){
            return this.id_code;
    }
    public void setEmp_in_day(Calendar emp_in_day){
            support.firePropertyChange("emp_in_day", this.emp_in_day, emp_in_day);
            this.emp_in_day = emp_in_day;
    }
    public Calendar getEmp_in_day(){
            return this.emp_in_day;
    }
    public void setEmp_birthday(Calendar emp_birthday){
            support.firePropertyChange("emp_birthday", this.emp_birthday, emp_birthday);
            this.emp_birthday = emp_birthday;
    }
    public Calendar getEmp_birthday(){
            return this.emp_birthday;
    }
    public void setEmp_sex(String emp_sex){
            support.firePropertyChange("emp_sex", this.emp_sex, emp_sex);
            this.emp_sex = emp_sex;
    }
    public String getEmp_sex(){
            return this.emp_sex;
    }
    public void setNation(Integer nation){
            support.firePropertyChange("nation", this.nation, nation);
            this.nation = nation;
    }
    public Integer getNation(){
            return this.nation;
    }
    public void setDept_id(Integer dept_id){
            support.firePropertyChange("dept_id", this.dept_id, dept_id);
            this.dept_id = dept_id;
    }
    public Integer getDept_id(){
            return this.dept_id;
    }
    public void setTitle_code(String title_code){
            support.firePropertyChange("title_code", this.title_code, title_code);
            this.title_code = title_code;
    }
    public String getTitle_code(){
            return this.title_code;
    }
    public void setDuty_code(String duty_code){
            support.firePropertyChange("duty_code", this.duty_code, duty_code);
            this.duty_code = duty_code;
    }
    public String getDuty_code(){
            return this.duty_code;
    }
    public void setEmp_degree_code(String emp_degree_code){
            support.firePropertyChange("emp_degree_code", this.emp_degree_code, emp_degree_code);
            this.emp_degree_code = emp_degree_code;
    }
    public String getEmp_degree_code(){
            return this.emp_degree_code;
    }
    public void setWork_phone(String work_phone){
            support.firePropertyChange("work_phone", this.work_phone, work_phone);
            this.work_phone = work_phone;
    }
    public String getWork_phone(){
            return this.work_phone;
    }
    public void setMobile_phone(String mobile_phone){
            support.firePropertyChange("mobile_phone", this.mobile_phone, mobile_phone);
            this.mobile_phone = mobile_phone;
    }
    public String getMobile_phone(){
            return this.mobile_phone;
    }
    public void setEmail(String email){
            support.firePropertyChange("email", this.email, email);
            this.email = email;
    }
    public String getEmail(){
            return this.email;
    }
    public void setBank_account(String bank_account){
            support.firePropertyChange("bank_account", this.bank_account, bank_account);
            this.bank_account = bank_account;
    }
    public String getBank_account(){
            return this.bank_account;
    }
    public void setIs_stock(Integer is_stock){
            support.firePropertyChange("is_stock", this.is_stock, is_stock);
            this.is_stock = is_stock;
    }
    public Integer getIs_stock(){
            return this.is_stock;
    }
    public void setSpell(String spell){
            support.firePropertyChange("spell", this.spell, spell);
            this.spell = spell;
    }
    public String getSpell(){
            return this.spell;
    }
    public void setEmp_desc(String emp_desc){
            support.firePropertyChange("emp_desc", this.emp_desc, emp_desc);
            this.emp_desc = emp_desc;
    }
    public String getEmp_desc(){
            return this.emp_desc;
    }
    public void setPay_way(String pay_way){
            support.firePropertyChange("pay_way", this.pay_way, pay_way);
            this.pay_way = pay_way;
    }
    public String getPay_way(){
            return this.pay_way;
    }
    public void setIs_pay(Integer is_pay){
            support.firePropertyChange("is_pay", this.is_pay, is_pay);
            this.is_pay = is_pay;
    }
    public Integer getIs_pay(){
            return this.is_pay;
    }
    public void setNature_code(String nature_code){
            support.firePropertyChange("nature_code", this.nature_code, nature_code);
            this.nature_code = nature_code;
    }
    public String getNature_code(){
            return this.nature_code;
    }
    public void setEmp_pol_code(String emp_pol_code){
            support.firePropertyChange("emp_pol_code", this.emp_pol_code, emp_pol_code);
            this.emp_pol_code = emp_pol_code;
    }
    public String getEmp_pol_code(){
            return this.emp_pol_code;
    }
    public void setEmp_type_code(String emp_type_code){
            support.firePropertyChange("emp_type_code", this.emp_type_code, emp_type_code);
            this.emp_type_code = emp_type_code;
    }
    public String getEmp_type_code(){
            return this.emp_type_code;
    }
    public void setImage_path(String image_path){
            support.firePropertyChange("image_path", this.image_path, image_path);
            this.image_path = image_path;
    }
    public String getImage_path(){
            return this.image_path;
    }
    public void setPassword(String password){
            support.firePropertyChange("password", this.password, password);
            this.password = password;
    }
    public String getPassword(){
            return this.password;
    }
    public void setBank_code(String bank_code){
            support.firePropertyChange("bank_code", this.bank_code, bank_code);
            this.bank_code = bank_code;
    }
    public String getBank_code(){
            return this.bank_code;
    }
    public void setIs_stop(Integer is_stop){
            support.firePropertyChange("is_stop", this.is_stop, is_stop);
            this.is_stop = is_stop;
    }
    public Integer getIs_stop(){
            return this.is_stop;
    }

}


