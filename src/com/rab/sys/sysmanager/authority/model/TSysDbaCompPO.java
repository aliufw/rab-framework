package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * ҵ��ʵ���t_sys_dba_comp
 * ҵ��ʵ��˵����
 * ����ʱ�䣺2010-10-26 12:41:19
 * ��Ȩ���У�
 */
@IdClass(TSysDbaCompPK.class)
@Entity
@Table(name = "t_sys_dba_comp")
public class TSysDbaCompPO extends BasePersistenceObject{

    /**
     * �Ƿ���������
     * �����ӱ���Ϣ��
     */
    @Id
    @Column(name = "dba_id" ,nullable=false, length=8)
    private Integer dba_id;

    /**
     * �Ƿ���������
     * �����ӱ���Ϣ��
     */
    @Id
    @Column(name = "comp_id" ,nullable=false, length=8)
    private Integer comp_id;


    public void setDba_id(Integer dba_id){
            support.firePropertyChange("dba_id", this.dba_id, dba_id);
            this.dba_id = dba_id;
    }
    public Integer getDba_id(){
            return this.dba_id;
    }
    public void setComp_id(Integer comp_id){
            support.firePropertyChange("comp_id", this.comp_id, comp_id);
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }

}

class TSysDbaCompPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer dba_id;
    private Integer comp_id;
    public void setDba_id(Integer dba_id){
            this.dba_id = dba_id;
    }
    public Integer getDba_id(){
            return this.dba_id;
    }
    public void setComp_id(Integer comp_id){
            this.comp_id = comp_id;
    }
    public Integer getComp_id(){
            return this.comp_id;
    }

}

