package com.rab.framework.domain.po;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.rab.framework.dao.PersistenceDAOImpl;
import com.rab.framework.domain.session.DomainSession;

/**
 * 
 * <P>Title: BaseBusinessObject</P>
 * <P>Description: </P>
 * <P>程序说明：业务对象（BO）基类</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-7-3</P>
 *
 */
public abstract class BasePersistenceObject implements PropertyChangeListener, PersistenceObject{

	/**
	 * 序列化编号
	 */
	private static final long serialVersionUID = -3640919653531086382L;

	/**
	 * 数据访问对象，一个BO只能有一个
	 */
	protected PersistenceDAOImpl dao;
	
	/**
	 * 录入时间，增加插入时间和修改时间字段，以备自动记录插入时间和修改时间使用
	 */
	protected Calendar lrsj;
	
	/**
	 * 修改时间，增加插入时间和修改时间字段，以备自动记录插入时间和修改时间使用
	 */
	protected Calendar xgsj;
	
	/**
	 * 上下文引用
	 */
	protected transient DomainSession domainSession = null;

	/**
	 * 用来支持对各种BO的 Set方法的访问
	 */
	protected PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * 用来保存更新时属性的状态 ；key － 属性名称，String类型 ， value － 属性的新值 ， Object类型
	 */
	protected Map<String, Object> status = new HashMap<String, Object>();

	protected BasePersistenceObject() {
		support.addPropertyChangeListener(this);
	}
	
	public Calendar getLrsj() {
		return lrsj;
	}

	public void setLrsj(Calendar lrsj) {
		this.lrsj = lrsj;
	}

	public Calendar getXgsj() {
		return xgsj;
	}

	public void setXgsj(Calendar xgsj) {
		this.xgsj = xgsj;
	}

	public void setDao(PersistenceDAOImpl dao) {
		this.dao = dao;
	}

	public void setDomainSession(DomainSession domainSession) {
		this.domainSession = domainSession;
	}
	
	/**
	 * 实现PropertyChangeListener接口的方法，其将属性的更改状态保存在status属性中；
	 *
	 * @param evt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evt) {
//		logger.debug("用户将属性" + evt.getPropertyName() +"由" + evt.getOldValue() + "变为" + evt.getNewValue());
		status.put(evt.getPropertyName(), evt.getNewValue());
	}

	protected void firePropertyChange(String name, Object oldObj, Object newObj) {
		support.firePropertyChange(name, oldObj, newObj);
	}

	protected void firePropertyChange(String name, int oldObj, int newObj) {
		support.firePropertyChange(name, oldObj, newObj);
	}

	protected void firePropertyChange(String name, long oldObj, long newObj) {
		support.firePropertyChange(name, new Long(oldObj), new Long(newObj));
	}

	protected void firePropertyChange(String name, boolean oldObj,
			boolean newObj) {
		support.firePropertyChange(name, oldObj, newObj);
	}

	protected void firePropertyChange(String name, short oldObj, short newObj) {
		support.firePropertyChange(name, oldObj, newObj);
	}

	protected void firePropertyChange(String name, float oldObj, float newObj) {
		support.firePropertyChange(name, new Float(oldObj), new Float(newObj));
	}

	protected void firePropertyChange(String name, double oldObj, double newObj) {
		support.firePropertyChange(name, new Double(oldObj),
						new Double(newObj));
	}

	public Map<String, Object> getStatus() {
		return status;
	}

}
