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
 * <P>程序说明：数据传输类，传输组件数据到业务层。</P>
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
	 * 序列化编码
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
			logger.error("componentVO不能为空");
			return false;
		}
		return true;
	}
	
	/**
	 * 根据组件名称获得WebfrmFrom组件原始解析数据<br>
	 * 组件类型：WebfrmFrom<br>
	 * 此方法返回原始数据结构Map，对应的key为表单中填写控件的name，value为控件的值。
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return 原始解析数据
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
	 * 获得所有WebfrmFrom组件原始解析数据<br>
	 * 组件类型：WebfrmFrom<br>
	 * 此方法返回原始数据结构Map，对应的key为表单中填写控件的name，value为控件的值。
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return 原始解析数据
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
	 * 根据组件名称获得WebfrmFrom中的数据。<br>
	 * 组件类型：WebfrmFrom<br>
	 * 此方法能将数据自动填充到BO当中，调用时传入Class。
	 * 
	 * @param widgetName
	 *            组件名称
	 * @param clz
	 *            BO Class
	 * @return BO实例
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
			logger.error("实例化BO出错：" + e.getMessage(), e);
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
	 * 根据组件名称获得WebfrmFrom中的数据。<br>
	 * 组件类型：WebfrmFrom<br>
	 * 此方法与{@link #getForm(String, Class)}类似，需要用户在前端传入BO Class对应的beanName。 <br>
	 * code:
	 * <hr>
	 * <code></code>
	 * <hr>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getForm(String, Class)
	 * @param widgetName
	 *            组件名称
	 * @return BO Class
	 */
	
	public Object getForm(String widgetName) throws  Exception {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		FormVO formVO = (FormVO) componentVO.getData().get(widgetName);
		String beanName = formVO.getBeanName();
		if (beanName == null || beanName.length() == 0) {
			logger.error("没有指定beanName");
			throw new Exception("没有指定beanName");
		}
		Class<?> c = null;
		try {
			c = Class.forName(beanName);
		} catch (ClassNotFoundException e) {
			logger.error("没有找到beanName对应的类 " + e.getMessage(), e);
			throw e;
		}
		return getForm(widgetName, c);
	}

	/**
	 * 根据组件名称获得原始解析数据。<br>
	 * 组件类型：WebfrmFrom<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return {@link FormVO}
	 */
	public FormVO getFormMetaData(String widgetName) {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		return componentVO.getData().get(widgetName) == null ? null : (FormVO)componentVO.getData().get(widgetName);
	}
	
	/**
	 * 根据组件名称，获得WebfrmAttr组件的原始数据。 <br>
	 * 传入组件名称，将返回value。
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return attr的originalValue
	 */
	public Object getAttr(String widgetName){
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}	
		return componentVO.getData().get(widgetName) == null ? null : ((AttrVO)componentVO.getData().get(widgetName)).getOriginalValue();
	}
	
	/**
	 * 获得全部WebfrmAttr组件的原始数据。 <br>
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
	 * 根据组件名称获得标记为insert的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class，填充到用户BO中，返回BO List。<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getInsertTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "insert");
	}
	
	/**
	 * 根据组件名称获得标记为update的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class，填充到用户BO中，返回BO List。<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getUpdateTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "update");
	}
	
	/**
	 * 根据组件名称获得标记为delete的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class，填充到用户BO中，返回BO List。<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @param clz
	 *            BO Class
	 * @return BO List
	 */
	public List<?> getDeleteTable(String widgetName, Class<?> clz)
			throws Exception {
		return getTable(widgetName, clz, "delete");
	}
	
	/**
	 * 根据组件名称获得前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class，填充到用户BO中，返回BO List。<br>
	 * 
	 * @param widgetName
	 *            组件名称
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
							logger.error("实例化BO出错： " + e.getMessage(), e);
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
	 * 根据组件名称获得前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class beanName，填充到用户BO中，返回BO List。<br>
	 * 与{@link #getTable(String, Class)}不同的是，BO Class的声明需要从前端获取。<br>
	 * <br>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            组件名称
	 * @return BO List
	 */
	public List<?> getTable(String widgetName) throws Exception {
		return getTable(widgetName, "");
	}
	
	/**
	 * 根据组件名称获得标记为insert的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class beanName，填充到用户BO中，返回BO List。<br>
	 * 与{@link #getTable(String, Class)}不同的是，BO Class的声明需要从前端获取。<br>
	 * <br>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            组件名称
	 * @return BO List
	 */
	public List<?> getInsertTable(String widgetName) throws Exception {
		return getTable(widgetName, "insert");
	}
	
	/**
	 * 根据组件名称获得标记为update的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class beanName，填充到用户BO中，返回BO List。<br>
	 * 与{@link #getTable(String, Class)}不同的是，BO Class的声明需要从前端获取。<br>
	 * <br>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            组件名称
	 * @return BO List
	 */
	public List<?> getUpdateTable(String widgetName) throws Exception {
		return getTable(widgetName, "update");
	}
	
	/**
	 * 根据组件名称获得标记为delete的前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class beanName，填充到用户BO中，返回BO List。<br>
	 * 与{@link #getTable(String, Class)}不同的是，BO Class的声明需要从前端获取。<br>
	 * <br>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            组件名称
	 * @return BO List
	 */
	public List<?> getDeleteTable(String widgetName) throws Exception {
		return getTable(widgetName, "delete");
	}
	
	/**
	 * 根据组件名称获得前端组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 此方法根据用户传入的Class beanName，填充到用户BO中，返回BO List。<br>
	 * 与{@link #getTable(String, Class)}不同的是，BO Class的声明需要从前端获取。<br>
	 * <br>
	 * 如果未获取到beanName，框架将抛出异常。
	 * 
	 * @see #getTable(String, Class)
	 * @param widgetName
	 *            组件名称
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
			logger.error("没有指定beanname");
			throw new  Exception("没有指定beanname");
		}
		Class<?> clz = null;
		try {
			clz = Class.forName(beanName);
		} catch (ClassNotFoundException e) {
			logger.error("没有找到beanName对应的类：" + e.getMessage(), e);
			throw e;
		}
		return getTable(widgetName, clz, status);
	}
	
	/**
	 * 根据表格组件名称获得分页信息数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return {@link PaginationMetaData}
	 */
	public PaginationMetaData getTablePageInfo(String widgetName){
		if(!checkComponentVO()){
			return new PaginationMetaData();
		}
		if(componentVO.getData().containsKey(widgetName) && componentVO.getData().get(widgetName) != null){
			return ((GridVO) componentVO.getData().get(widgetName)).getPageInfo() == null ? new PaginationMetaData() : ((GridVO) componentVO.getData().get(widgetName)).getPageInfo();
		}else{
			logger.debug("所取widgetName为：" + widgetName + "的组件不存在!");
			return new PaginationMetaData();
		}
		
	}
	
	/**
	 * 根据组件名称，获得WebfrmGrid组件数据模型。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return {@link GridVO}
	 */
	public GridVO getTableMetaData(String widgetName) {
		if(!checkComponentVO() || !componentVO.getData().containsKey(widgetName)){
			return null;
		}
		return componentVO.getData().get(widgetName) == null ? null : (GridVO) componentVO.getData().get(widgetName);
	}
	
	/**
	 * 根据组件名称获得标记为insert的WebfrmGrid组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 返回的List中是二维结构，外层List表示表格的每一行，内层Map表示存放表格中每一行中每个Cell的数据。Key为数据库中的字段名称，
	 * value是对应值的名值对的Map。<br>
	 * 用法同{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            组件名称
	 * @return 原始数据
	 */
	public List<Map<String, Object>> getInsertTableData(String widgetName){
		return getTableData(widgetName, "insert");
	}
	
	/**
	 * 根据组件名称获得标记为delete的WebfrmGrid组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 返回的List中是二维结构，外层List表示表格的每一行，内层Map表示存放表格中每一行中每个Cell的数据。Key为数据库中的字段名称，
	 * value是对应值的名值对的Map。<br>
	 * 用法同{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            组件名称
	 * @return 原始数据
	 */
	public List<Map<String, Object>> getDeleteTableData(String widgetName){
		return getTableData(widgetName, "delete");
	}
	
	/**
	 * 根据组件名称获得标记为update的WebfrmGrid组件数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 返回的List中是二维结构，外层List表示表格的每一行，内层Map表示存放表格中每一行中每个Cell的数据。Key为数据库中的字段名称，
	 * value是对应值的名值对的Map。<br>
	 * 用法同{@link #getTableData(String)}
	 * 
	 * @see #getTableData(String)
	 * @param widgetName
	 *            组件名称
	 * @return 原始数据
	 */
	public List<Map<String, Object>> getUpdateTableData(String widgetName){
		return getTableData(widgetName, "update");
	}

	/**
	 * 根据组件名称，获得WebfrmGrid组件原始解析数据。<br>
	 * 组件类型：WebfrmGrid<br>
	 * 返回的List中是二维结构，外层List表示表格的每一行，内层Map表示存放表格中每一行中每个Cell的数据。Key为数据库中的字段名称，
	 * value是对应值。<br>
	 * 
	 * 
	 * @param widgetName
	 *            组件名称
	 * @return 原始解析数据
	 */
	public List<Map<String, Object>> getTableData(String widgetName) {
		return getTableData(widgetName, null);
	}
	
	
	/**
	 * 根据组件名称获得Properties格式的数据，一般用在MultSelect组件的数据提交。<br>
	 * 组件类型：MultSelect等<br>
	 * code:
	 * <hr>
	 * <code></code>
	 * <hr>
	 * @param widgetName
	 *            组件名称
	 * @return Properties 对象
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
	 * 按指定的类型调用set方法给BO赋值 <br>
	 * 
	 * @param value
	 *            需要转换的字符串
	 * @param bo
	 *            需要赋值的BO对象
	 * @param method
	 *            set方法
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
