package com.rab.sys.sysmanager.authority.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.rab.framework.domain.po.BasePersistenceObject;

import 	javax.persistence.IdClass;
import 	java.io.Serializable;

/**
 * ҵ��ʵ���t_sys_user_group
 * ҵ��ʵ��˵����
 * ����ʱ�䣺2010-10-25 16:53:14
 * ��Ȩ���У�
 */
@IdClass(TSysUserGroupPK.class)
@Entity
@Table(name = "t_sys_user_group")
public class TSysUserGroupPO extends BasePersistenceObject{

    /**
     * �Ƿ���������
     * �����ӱ���Ϣ��
     */
    @Id
    @Column(name = "user_id" ,nullable=false, length=8)
    private Integer user_id;

    /**
     * �Ƿ���������
     * �����ӱ���Ϣ��
     */
    @Id
    @Column(name = "group_id" ,nullable=false, length=8)
    private Integer group_id;


    public void setUser_id(Integer user_id){
            support.firePropertyChange("user_id", this.user_id, user_id);
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }
    public void setGroup_id(Integer group_id){
            support.firePropertyChange("group_id", this.group_id, group_id);
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }

}

class TSysUserGroupPK  implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer user_id;
    private Integer group_id;
    public void setUser_id(Integer user_id){
            this.user_id = user_id;
    }
    public Integer getUser_id(){
            return this.user_id;
    }
    public void setGroup_id(Integer group_id){
            this.group_id = group_id;
    }
    public Integer getGroup_id(){
            return this.group_id;
    }

}

