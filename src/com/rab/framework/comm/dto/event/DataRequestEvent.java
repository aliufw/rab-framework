/**
 * 
 */
package com.rab.framework.comm.dto.event;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.rab.framework.comm.log.LogFactory;
import com.rab.framework.comm.log.LogWritter;
import com.rab.framework.comm.pagination.PaginationMetaData;
import com.rab.framework.comm.util.Constants;
import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.web.action.vo.ComponentType;
import com.rab.framework.web.action.vo.ComponentVO;
import com.rab.framework.web.action.vo.data.AttrVO;
import com.rab.framework.web.action.vo.data.DataVO;
import com.rab.framework.web.action.vo.data.FormVO;
import com.rab.framework.web.action.vo.data.GridVO;
import com.rab.framework.web.action.vo.data.PropertiesVO;
import com.rab.framework.web.action.vo.data.TDSVO;

/**
 * 
 * <P>Title: DataRequestEvent</P>
 * <P>Description: </P>
 * <P>����˵�������ݴ����࣬����������ݵ�ҵ��㡣</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author jingyang</P>
 * <P>version 1.0</P>
 * <P>2010-7-13</P>
 *
 */

public class DataRequestEvent extends BaseRequestEvent {
	/**
	 * ���л�����
	 */
	private static final long serialVersionUID = -253972111339667604L;
	private final static LogWritter logger = LogFactory.getLogger(DataRequestEvent.class);

	public DataRequestEvent(String transactionID, String sessionID, ComponentVO componentVO) {
		super(transactionID, sessionID);
		this.componentVO = componentVO;
	}	
	
	private ComponentVO componentVO;
	
	private boolean checkComponentVO(){
		if(componentVO == null || componentVO.getData() == null || componentVO.getData().size() == 0){
			logger.error("componentVO����Ϊ��");
			return false;
		}
		return true;
	}
	
	/**
	 * ����������ƻ��WebfrmFrom���ԭʼ��������<br>
	 * ������ͣ�WebfrmFrom<br>
	 * �˷�������ԭʼ���ݽṹMap����Ӧ��keyΪ������д�ؼ���name��valueΪ�ؼ���ֵ��
	 * 
	 * @param widgetName
	 *            �������
	 * @return ԭʼ��������
	 */
	public Map<String, Object> getFormData(String widgetName){
		if(!checkComponentVO()){
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object> ();
		if(componentVO.getData().containsKey(widgetName) && componentVO.getData().get(widgetName) != null){
			if(((FormVO)componentVO.getData().get(widgetName)).getData() != null){
				for(String key : ((FormVO)componentVO.getData().get(widgetName)).getData().keySet()){
					result.put(key, ((FormVO)componentVO.getData().get(widgetName)).getData().get(key).get(Constants.originalValueName));
				}
				return result;
			}else{
				return null;
			}
			
		}else{
			return null;
		}
	}
	
	/**
	 * �������WebfrmFrom���ԭʼ��������<br>
	 * ������ͣ�WebfrmFrom<br>
	 * �˷�������ԭʼ���ݽṹMap����Ӧ��keyΪ������д�ؼ���name��valueΪ�ؼ���ֵ��
	 * 
	 * @param widgetName
	 *            �������
	 * @return ԭʼ��������
	 */
	public Map<String, Object> getFormDatas(){
		if(!checkComponentVO()){
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object> ();
		for(DataVO dataVO : componentVO.getData().values()){
			if(dataVO != null && dataVO.getType().equals(ComponentType.WebfrmForm)){
				for(String key : ((FormVO)dataVO).getData().keySet()){
					result.put(key, ((FormVO)dataVO).getData().get(key).get(Constants.originalValueName));
				}
				
			}
		}
		return result;
	}

	/**
	 * ����������ƻ��WebfrmFrom�е����ݡ�<br>
	 * ������ͣ�WebfrmFrom<br>
	 * �˷����ܽ������Զ���䵽BO���У�����ʱ����Class��
	 * 
	 * @param widgetName
	 *            �������
	 * @param clz
	 *            BO Class
	 * @return BOʵ��
	 */
	public Object getForm(String widgetName, Class<?> clz) throws Exception{
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		FormVO formVO = (FormVO) componentVO.getData().get(widgetName);
		Object result;
		try {
			result = clz.newInstance();
		} catch (Exception e) {
			logger.error("ʵ����BO����" + e.getMessage(), e);
			throw e;
		} 
		
		Method[] methods = clz.getMethods();
		for (String fieldName : formVO.getData().keySet()) {
			for (Method method : methods) {
				if (method.getName().equalsIgnoreCase("set" + fieldName)) {
					try {
						setData(formVO.getData().get(fieldName).get(Constants.originalValueName), result, method);
					}  catch (IllegalArgumentException e) {
						
					} catch (IllegalAccessException e) {
						
					}
				}
			}
		}

		return result;
	}

	/**
	 * ����������ƻ��WebfrmFrom�е����ݡ�<br>
	 * ������ͣ�WebfrmFrom<br>
	 * �˷�����{@link #getForm(String, Class)}���ƣ���Ҫ�û���ǰ�˴���BO Class��Ӧ��beanName�� <br>
	 * code:
	 * <hr>
	 * <code></code>
	 * <hr>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getForm(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO Class
	 */
	
	public Object getForm(String widgetName) throws  Exception {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		FormVO formVO = (FormVO) componentVO.getData().get(widgetName);
		String beanName = formVO.getBeanName();
		if (beanName == null || beanName.length() == 0) {
			logger.error("û��ָ��beanName");
			throw new Exception("û��ָ��beanName");
		}
		Class<?> c = null;
		try {
			c = Class.forName(beanName);
		} catch (ClassNotFoundException e) {
			logger.error("û���ҵ�beanName��Ӧ���� " + e.getMessage(), e);
			throw e;
		}
		return getForm(widgetName, c);
	}

	/**
	 * ����������ƻ��ԭʼ�������ݡ�<br>
	 * ������ͣ�WebfrmFrom<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @return {@link FormVO}
	 */
	public FormVO getFormMetaData(String widgetName) {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		return componentVO.getData().get(widgetName) == null ? null : (FormVO)componentVO.getData().get(widgetName);
	}
	
	/**
	 * ����������ƣ����WebfrmAttr�����ԭʼ���ݡ� <br>
	 * ����������ƣ�������value��
	 * 
	 * @param widgetName
	 *            �������
	 * @return attr��originalValue
	 */
	public Object getAttr(String widgetName){
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}	
		return componentVO.getData().get(widgetName) == null ? null : ((AttrVO)componentVO.getData().get(widgetName)).getOriginalValue();
	}
	
	/**
	 * ���ȫ��WebfrmAttr�����ԭʼ���ݡ� <br>
	 * 
	 * 
	 * @return Map<String,Object> 
	 */
	public Map<String,Object> getAttrs(){
		Map<String,Object> map = new HashMap<String,Object>(); 
		if(!checkComponentVO()){
			return null;
		}	
		for(String key : componentVO.getData().keySet()){
			if(componentVO.getData().get(key) != null && ComponentType.WebfrmAttr.equals(componentVO.getData().get(key).getType())){
				map.put(key, componentVO.getData().get(key) == null ? null : ((AttrVO)componentVO.getData().get(key)).getOriginalValue());
			}
		}
		
		return map;
	}
	/**
	 * ����������ƻ�ñ��Ϊinsert��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class����䵽�û�BO�У�����BO List��<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getInsertTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "insert");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊupdate��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class����䵽�û�BO�У�����BO List��<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getUpdateTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "update");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊdelete��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class����䵽�û�BO�У�����BO List��<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getDeleteTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "delete");
	}
	
	/**
	 * ����������ƻ��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class����䵽�û�BO�У�����BO List��<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, null);
	}
	
	private List<?> getTable(String widgetName, Class<?> clz, String status)
			throws Exception {
		if (!checkComponentVO() || !componentVO.getData().containsKey(widgetName)) {
			return null;
		}
		List<Object> result = new ArrayList<Object>();
		GridVO gridVO = (GridVO) componentVO.getData().get(widgetName);
		if (gridVO != null && gridVO.getTrs() != null) {
			for (TDSVO tds : gridVO.getTrs()) {
				if (status == null || "".equals(status)
						|| (status.length() > 0 && status.equalsIgnoreCase(tds.getStatus()))) {
					Method[] methods = clz.getMethods();
					if (tds.getTds() != null && tds.getTds().size() > 0) {
						Object bo;
						try {
							bo = clz.newInstance();
						} catch (Exception e) {
							logger.error("ʵ����BO���� " + e.getMessage(), e);
							throw e;
						}
						for (String fieldName : tds.getTds().keySet()) {
							for (Method method : methods) {
								if (method.getName().equalsIgnoreCase(
										"set" + fieldName)) {
									try {
										setData(tds.getTds().get(fieldName)
												.get(Constants.originalValueName),
												bo, method);
									} catch (IllegalArgumentException e) {

									} catch (IllegalAccessException e) {

									}
								}
							}
						}
						result.add(bo);
					}
				}
			}
		}

		return result;
	}
	/**
	 * ����������ƻ��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class beanName����䵽�û�BO�У�����BO List��<br>
	 * ��{@link #getTable(String, Class)}��ͬ���ǣ�BO Class��������Ҫ��ǰ�˻�ȡ��<br>
	 * <br>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO List
	 */
	public List<?> getTable(String widgetName) throws Exception {
		return getTable(widgetName, "");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊinsert��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class beanName����䵽�û�BO�У�����BO List��<br>
	 * ��{@link #getTable(String, Class)}��ͬ���ǣ�BO Class��������Ҫ��ǰ�˻�ȡ��<br>
	 * <br>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO List
	 */
	public List<?> getInsertTable(String widgetName) throws Exception {
		return getTable(widgetName, "insert");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊupdate��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class beanName����䵽�û�BO�У�����BO List��<br>
	 * ��{@link #getTable(String, Class)}��ͬ���ǣ�BO Class��������Ҫ��ǰ�˻�ȡ��<br>
	 * <br>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO List
	 */
	public List<?> getUpdateTable(String widgetName) throws Exception {
		return getTable(widgetName, "update");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊdelete��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class beanName����䵽�û�BO�У�����BO List��<br>
	 * ��{@link #getTable(String, Class)}��ͬ���ǣ�BO Class��������Ҫ��ǰ�˻�ȡ��<br>
	 * <br>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO List
	 */
	public List<?> getDeleteTable(String widgetName) throws Exception {
		return getTable(widgetName, "delete");
	}
	
	/**
	 * ����������ƻ��ǰ��������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * �˷��������û������Class beanName����䵽�û�BO�У�����BO List��<br>
	 * ��{@link #getTable(String, Class)}��ͬ���ǣ�BO Class��������Ҫ��ǰ�˻�ȡ��<br>
	 * <br>
	 * ���δ��ȡ��beanName����ܽ��׳��쳣��
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            �������
	 * @return BO List
	 */
	private List<?> getTable(String widgetName, String status) throws Exception {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		GridVO gridVO = componentVO.getData().get(widgetName) == null ? null : (GridVO)componentVO.getData().get(widgetName);

		String beanName = null;
		if(gridVO != null){
			beanName = gridVO.getBeanName();
		}
			
		if (beanName == null || beanName.length() == 0) {
			logger.error("û��ָ��beanname");
			throw new  Exception("û��ָ��beanname");
		}
		Class<?> clz = null;
		try {
			clz = Class.forName(beanName);
		} catch (ClassNotFoundException e) {
			logger.error("û���ҵ�beanName��Ӧ���ࣺ" + e.getMessage(), e);
			throw e;
		}
		return getTable(widgetName, clz, status);
	}
	
	/**
	 * ���ݱ��������ƻ�÷�ҳ��Ϣ���ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @return {@link PaginationMetaData}
	 */
	public PaginationMetaData getTablePageInfo(String widgetName){
		if(!checkComponentVO()){
			return new PaginationMetaData();
		}
		if(componentVO.getData().containsKey(widgetName) && componentVO.getData().get(widgetName) != null){
			return ((GridVO) componentVO.getData().get(widgetName)).getPageInfo() == null ? new PaginationMetaData() : ((GridVO) componentVO.getData().get(widgetName)).getPageInfo();
		}else{
			logger.debug("��ȡwidgetNameΪ��" + widgetName + "�����������!");
			return new PaginationMetaData();
		}
		
	}
	
	/**
	 * ����������ƣ����WebfrmGrid�������ģ�͡�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * 
	 * @param widgetName
	 *            �������
	 * @return {@link GridVO}
	 */
	public GridVO getTableMetaData(String widgetName) {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		return componentVO.getData().get(widgetName) == null ? null : (GridVO) componentVO.getData().get(widgetName);
	}
	
	/**
	 * ����������ƻ�ñ��Ϊinsert��WebfrmGrid������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * ���ص�List���Ƕ�ά�ṹ�����List��ʾ����ÿһ�У��ڲ�Map��ʾ��ű����ÿһ����ÿ��Cell�����ݡ�KeyΪ���ݿ��е��ֶ����ƣ�
	 * value�Ƕ�Ӧֵ����ֵ�Ե�Map��<br>
	 * �÷�ͬ{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            �������
	 * @return ԭʼ����
	 */
	public List<Map<String, Object>> getInsertTableData(String widgetName){
		return getTableData(widgetName, "insert");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊdelete��WebfrmGrid������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * ���ص�List���Ƕ�ά�ṹ�����List��ʾ����ÿһ�У��ڲ�Map��ʾ��ű����ÿһ����ÿ��Cell�����ݡ�KeyΪ���ݿ��е��ֶ����ƣ�
	 * value�Ƕ�Ӧֵ����ֵ�Ե�Map��<br>
	 * �÷�ͬ{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            �������
	 * @return ԭʼ����
	 */
	public List<Map<String, Object>> getDeleteTableData(String widgetName){
		return getTableData(widgetName, "delete");
	}
	
	/**
	 * ����������ƻ�ñ��Ϊupdate��WebfrmGrid������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * ���ص�List���Ƕ�ά�ṹ�����List��ʾ����ÿһ�У��ڲ�Map��ʾ��ű����ÿһ����ÿ��Cell�����ݡ�KeyΪ���ݿ��е��ֶ����ƣ�
	 * value�Ƕ�Ӧֵ����ֵ�Ե�Map��<br>
	 * �÷�ͬ{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            �������
	 * @return ԭʼ����
	 */
	public List<Map<String, Object>> getUpdateTableData(String widgetName){
		return getTableData(widgetName, "update");
	}

	/**
	 * ����������ƣ����WebfrmGrid���ԭʼ�������ݡ�<br>
	 * ������ͣ�WebfrmGrid<br>
	 * ���ص�List���Ƕ�ά�ṹ�����List��ʾ����ÿһ�У��ڲ�Map��ʾ��ű����ÿһ����ÿ��Cell�����ݡ�KeyΪ���ݿ��е��ֶ����ƣ�
	 * value�Ƕ�Ӧֵ��<br>
	 * 
	 * 
	 * @param widgetName
	 *            �������
	 * @return ԭʼ��������
	 */
	public List<Map<String, Object>> getTableData(String widgetName) {
		return getTableData(widgetName, null);
	}
	
	
	/**
	 * ����������ƻ��Properties��ʽ�����ݣ�һ������MultSelect����������ύ��<br>
	 * ������ͣ�MultSelect��<br>
	 * code:
	 * <hr>
	 * <code></code>
	 * <hr>
	 * @param widgetName
	 *            �������
	 * @return Properties ����
	 */
	
	public Properties getProperties(String widgetName) throws  Exception {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		
		PropertiesVO propertiesVO = (PropertiesVO) componentVO.getData().get(widgetName);

		Properties props = propertiesVO.getProps();
		
		return props;

	}
	
	private List<Map<String, Object>> getTableData(String widgetName, String status) {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>> ();
		GridVO gridVO = componentVO.getData().get(widgetName) == null ? null : (GridVO) componentVO.getData().get(widgetName);
		if(gridVO != null && gridVO.getTrs() != null){
			for (TDSVO tds : gridVO.getTrs()) {
				if(tds != null && tds.getTds() != null){
					if(status == null || (status != null && status.length() > 0 && status.equalsIgnoreCase(tds.getStatus()))){
						Map<String, Object> tempMap = new HashMap<String, Object>();
						for(String key : tds.getTds().keySet()){
							tempMap.put(key, tds.getTds().get(key).get(Constants.originalValueName));
						}
						result.add(tempMap);						
					}					
				}				
			}
		}				
		return result;
	}
	
	
	/**
	 * ��ָ�������͵���set������BO��ֵ <br>
	 * 
	 * @param value
	 *            ��Ҫת�����ַ���
	 * @param bo
	 *            ��Ҫ��ֵ��BO����
	 * @param method
	 *            set����
	 */
	private void setData(Object value, Object bo, Method method)
			throws Exception {

		if (value != null && !"".equals(value) && bo != null) {
			if (value instanceof String) {

				String typeName = method.getParameterTypes()[0].getName(); 
				if (typeName.equals("java.lang.String")) {
					method.invoke(bo, value.toString());
				} else if (typeName.equals("java.lang.Integer")) {
					method.invoke(bo, Integer.parseInt(value.toString()));
				} else if (typeName.equals("java.lang.Float")) {
					method.invoke(bo, Float.valueOf(value.toString()));
				} else if (typeName.equals("java.lang.Long")) {
					method.invoke(bo, Long.valueOf(value.toString()));
				} else if (typeName.equals("java.lang.Double")) {
					method.invoke(bo, Double.valueOf(value.toString()));
				} else if (typeName.equals("java.sql.Date")) {
					method.invoke(bo, DateUtils.parseToDate(value.toString()));
				} else if (typeName.equals("java.sql.Timestamp")) {
					method.invoke(bo, DateUtils.parseToTimestamp(value.toString()));
				} else if (typeName.equals("java.util.Calendar")) {
					method.invoke(bo, DateUtils.parseDate(value.toString()));
				} else if (typeName.equals("java.math.BigDecimal")) {
					method.invoke(bo, new BigDecimal(value.toString()));
				} else if (typeName.equals("int")) {
					method.invoke(bo, Integer.parseInt(value.toString()));
				} else if (typeName.equals("float")) {
					method.invoke(bo, Float.parseFloat(value.toString()));
				} else if (typeName.equals("double")) {
					method.invoke(bo, Double.parseDouble(value.toString()));
				} else if (typeName.equals("char")) {
					method.invoke(bo, value.toString().charAt(0));
				}
			}
		}
	}
}
