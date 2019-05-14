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
 * <P>����˵����ҵ�����BO������</P>
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
	 * ���л����
	 */
	private static final long serialVersionUID = -3640919653531086382L;

	/**
	 * ���ݷ��ʶ���һ��BOֻ����һ��
	 */
	protected PersistenceDAOImpl dao;
	
	/**
	 * ¼��ʱ�䣬���Ӳ���ʱ����޸�ʱ���ֶΣ��Ա��Զ���¼����ʱ����޸�ʱ��ʹ��
	 */
	protected Calendar lrsj;
	
	/**
	 * �޸�ʱ�䣬���Ӳ���ʱ����޸�ʱ���ֶΣ��Ա��Զ���¼����ʱ����޸�ʱ��ʹ��
	 */
	protected Calendar xgsj;
	
	/**
	 * ����������
	 */
	protected transient DomainSession domainSession = null;

	/**
	 * ����֧�ֶԸ���BO�� Set�����ķ���
	 */
	protected PropertyChangeSupport support = new PropertyChangeSupport(this);

	/**
	 * �����������ʱ���Ե�״̬ ��key �� �������ƣ�String���� �� value �� ���Ե���ֵ �� Object����
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
	 * ʵ��PropertyChangeListener�ӿڵķ������佫���Եĸ���״̬������status�����У�
	 *
	 * @param evt PropertyChangeEvent
	 */
	public void propertyChange(PropertyChangeEvent evt) {
//		logger.debug("�û�������" + evt.getPropertyName() +"��" + evt.getOldValue() + "��Ϊ" + evt.getNewValue());
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
