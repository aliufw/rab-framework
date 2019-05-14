package com.rab.framework.comm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class TreeCreatorJson {

    //���ݻ�����,���д�ŵ��� Node ����
    private List<Map<String, Object>> al_DataBuffer = null;

    //������
    public TreeCreatorJson() {
    }
    
    private Map<String, String> checkNameMap(Map<String, String> nameMap){
    	if(nameMap == null){
    		Map<String, String> newNameMap = new HashMap<String, String>();			
    		newNameMap.put("code", "code");
    		newNameMap.put("leaf", "leaf");
    		newNameMap.put("pcode", "pcode");
    		newNameMap.put("caption", "caption");
    		newNameMap.put("checked", "checked");
			return newNameMap;
		}else{
			if(!nameMap.containsKey("code")){
				nameMap.put("code", "code");
			}
			if(!nameMap.containsKey("leaf")){
				nameMap.put("leaf", "leaf");
			}
			if(!nameMap.containsKey("pcode")){
				nameMap.put("pcode", "pcode");
			}
			if(!nameMap.containsKey("caption")){
				nameMap.put("caption", "caption");
			}
			if(!nameMap.containsKey("checked")){
				nameMap.put("checked", "checked");
			}
			return nameMap;
		}
    }

	public List<Map<String, Object>> getJsonTree(List<Map<String, Object>> nodes,
			List<Map<String, Object>> checkDatas, Map<String, String> nameMap) throws Exception {
		nameMap = checkNameMap(nameMap);
		if(nodes == null || nodes.size() == 0){
			return null;
		}
		al_DataBuffer = nodes;

		List<Map<String, Object>> rootNodes = getRootNode(nameMap);
		List<Map<String, Object>> treeNodes = new ArrayList<Map<String, Object>>();
		for(Map<String, Object> rootNode : rootNodes){
			Map<String, Object> treeMap = createElement(rootNode, checkDatas,
					nameMap);
			// ��������
			createTree(treeMap, rootNode, checkDatas,nameMap);
			treeNodes.add(treeMap);
		}
		al_DataBuffer = null;
		return treeNodes;
	}
	
	   //��������
    private void createTree(Map<String, Object> jtMap, Map<String, Object> node, List<Map<String, Object>> checkDatas, Map<String, String> nameMap) throws Exception {
        //û���ӽڵ�
        for(String key : node.keySet()){
        	if(key.equalsIgnoreCase(nameMap.get("leaf"))){
        		if((node.get(key) instanceof Boolean && (Boolean)node.get(key)) || (node.get(key) instanceof Integer && (Integer)node.get(key) == 1)){
        			return;
        		}
        	}
        }
        
        List<Map<String, Object>> al = this.getChildNodes( node, nameMap);
        Map[] children = new HashMap[al.size()];
        for(int i=0;i<al.size();i++){
        	Map<String, Object> childNode = al.get(i);
        	children[i] = createElement(childNode, checkDatas, nameMap);  
            
            //�ݹ����
            createTree(children[i],childNode, checkDatas, nameMap);
        }
        
        jtMap.put("children", children);
    }

      
    //��������Ԫ�ؽڵ�
	private Map<String, Object> createElement(Map<String, Object> node,  List<Map<String, Object>> checkDatas,
			Map<String, String> nameMap) throws Exception {
		if(node == null){
			return null;
		}
		Map<String, Object> result = new HashMap<String, Object>();
		for (String key : nameMap.keySet()) {
			if (key.equalsIgnoreCase("leaf")) {
				// �Ƿ�Ҷ��
				for(String nodeKey : node.keySet()){
					if(nodeKey.equalsIgnoreCase(nameMap.get(key))){
						Object leafValue = node.get(nodeKey);
						if (leafValue instanceof Boolean) {
							// boolean����Ҷ�ӱ�־����
							result.put("leaf", leafValue);
							if ((Boolean) leafValue) {
								result.put("expandable", false);
							} else {
								result.put("expandable", true);
							}
						} else if (leafValue instanceof Integer) {
							// ��boolean����Ҷ�ӱ�־����
							if (((Integer) leafValue).intValue() == 1) {
								result.put("leaf", true);
								result.put("expandable", false);
							} else {
								result.put("leaf", false);
								result.put("expandable", true);
							}
						}else if (leafValue instanceof Object) {
							// ��boolean����Ҷ�ӱ�־����
							if ("1".equals(leafValue.toString()) || "true".equals(leafValue.toString())) {
								result.put("leaf", true);
								result.put("expandable", false);
							} else {
								result.put("leaf", false);
								result.put("expandable", true);
							}
						}
					}
				}
			} else {
				// String����
				for(String nodeKey : node.keySet()){
					if(nodeKey.equalsIgnoreCase(nameMap.get(key))){
						result.put(key.toLowerCase(), node.get(nodeKey));
					}
				}
			}

			// Ϊ�˷�ֹchecked��leaf�ж�ʹ��һ��boolean�����ݣ������������else��֧
			if (key.equalsIgnoreCase("checked")) {
				for(String nodeKey : node.keySet()){
					if(nodeKey.equalsIgnoreCase(nameMap.get(key))){
						Object checkValue = node.get(nodeKey);
						if (checkValue instanceof Boolean) {
							result.put("checked", checkValue);
						}else if (checkValue instanceof String) {
							result.put("checked", Boolean.parseBoolean((String)checkValue));
						}				
					}
				}
			}
		}
		for(String key : node.keySet()){
			if(!result.containsKey(key.toLowerCase())){
				result.put(key.toLowerCase(), node.get(key));
			}
		}
		
		if(checkDatas != null && checkDatas.size() > 0){
			String idKey = nameMap.get("code");
			String checkedKey = nameMap.get("checked");
			//�������ڵ��checked״̬λ
			for(Map<String, Object> checkNode : checkDatas){
				// ��־��ǰ���ڵ��Ƿ��빹��ڵ�ƥ��
				boolean equalFlag = false;
				for(String key : checkNode.keySet()){
					if(key.equalsIgnoreCase(idKey) && checkNode.get(key).toString().equalsIgnoreCase(result.get("code").toString())){
						//��ǰ����ڵ��ڹ��˼��д��ڣ���Ҫ����
						equalFlag = true;
						result.put("checked", Boolean.TRUE);						
//						for(String key1 : checkNode.keySet()){
//							if(key1.equalsIgnoreCase(checkedKey)){
//								Boolean checkValue = (Boolean)checkNode.get(key1);
//									result.put("checked", (Boolean)result.get("checked") && checkValue);
//							}
//						}						
					}
				}
				if(equalFlag){
					// ��ǰ���ڵ��빹��ڵ�ƥ�䣬Ϊ����ڵ��������
					for(String key : checkNode.keySet()){
						if(!result.keySet().contains(key.toLowerCase())){
							result.put(key.toLowerCase(), checkNode.get(key));	
						}
					}
					
				}
			}
			if(!result.keySet().contains("checked")){
				result.put("checked", Boolean.FALSE);	
			}
		}
		
		return result;
	}
     
    /**
     * ȡ���ڵ�
     * @return �˵������ڵ�
     */
	private List<Map<String, Object>> getRootNode(Map<String, String> nameMap)
			throws Exception {
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
		if (al_DataBuffer != null && al_DataBuffer.size() > 0) {
			for (int i = 0; i < al_DataBuffer.size(); i++) {
				Map<String, Object> node = al_DataBuffer.get(i);
				for (String key : node.keySet()) {
					if (key.equalsIgnoreCase(nameMap.get("pcode"))) {
						if(node.get(key) == null){
							result.add(node);
						}else{
							List<Map<String, Object>> tempList = new ArrayList<Map<String, Object>>();
							for (int j = 0; j < al_DataBuffer.size(); j++) {
								Map<String, Object> node1 = al_DataBuffer.get(j);
								for (String key1 : node1.keySet()) {
									if (key1.equalsIgnoreCase(nameMap.get("code"))) {
										if ((node.get(key) == null && node.get(key) == node1
												.get(key1))
												|| (node.get(key).toString()
														.equalsIgnoreCase(node1
																.get(key1) == null ? null
																: node1.get(key1)
																		.toString()))) {
											tempList.add(node1);
										}
									}
								}
							}
							if (tempList.size() == 0) {
								result.add(node);
							}
						}
						
					}

				}
			}
		}

		return result;
	}
	
  
    //ȡһ���ڵ���ӽڵ�
    private List<Map<String, Object>> getChildNodes(Map<String, Object> node, Map<String, String> nameMap) throws Exception{
        List<Map<String, Object>> al = new ArrayList<Map<String, Object>>();

        String idName = nameMap.get("code");
        String pidName = nameMap.get("pcode");
        for(int i=0;i<al_DataBuffer.size();i++){
			for(String key : (al_DataBuffer.get(i)).keySet()){
				if(key.equalsIgnoreCase(pidName)){
					for(String key1 : node.keySet()){
						if(key1.equalsIgnoreCase(idName)){
							if((al_DataBuffer.get(i)).get(key).equals(node.get(key1))){
								al.add(al_DataBuffer.get(i));
							}
						}
					}
				}
			}
        }
        
        return al;
    }

}