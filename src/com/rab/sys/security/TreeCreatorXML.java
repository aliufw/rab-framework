package com.rab.sys.security;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import com.rab.framework.comm.security.FuncRightResource;
import com.rab.framework.comm.security.MenuNodeComparator;


public class TreeCreatorXML {
//    private final String IMAGE_FOLDER_OPEN = "../images/folderopen.gif";
//    private final String IMAGE_FOLDER_CLOSE = "../images/folderclose.gif";
//    private final String IMAGE_LEAF_OPEN = "../images/pageopen.gif";
//    private final String IMAGE_LEAF_CLOSE = "../images/pageclose.gif";
//    private final String IMAGE_ROOT = "../images/root.gif";

    //���ݻ�����,���д�ŵ��� Node ����
    private List<FuncRightResource> al_DataBuffer = null;

    //������
    public TreeCreatorXML() {
    }

    public StringBuffer getXMLTree(List<FuncRightResource> nodes){
    	//����
    	Collections.sort(nodes, new MenuNodeComparator());
    	
        al_DataBuffer = nodes;
        Document doc = new Document();
        
        FuncRightResource rootNode = this.getRootNode();
        //������
        Element rootElement = this.createElement(rootNode);
        doc.addContent(rootElement);
        
        //��������
        createTree(rootElement,rootNode);

        //����� String
        StringWriter sw = new StringWriter();
        try {
        	Format format = Format.getPrettyFormat();
        	format.setEncoding("UTF-8");
        	XMLOutputter outputter = new XMLOutputter(format);
        	outputter.output(doc, sw);
        	sw.flush();
        	sw.close();
        }
        catch (java.io.IOException e) {
            e.printStackTrace();
        }

        return sw.getBuffer();
    }

    //��������
    private void createTree(Element elm,FuncRightResource node){
        if(node==null || isLeafNode(node)){ //û���ӽڵ�
            return;
        }
        List<FuncRightResource> al = this.getChildNodes(node);
        for(int i=0;i<al.size();i++){
            FuncRightResource childNode = (FuncRightResource)al.get(i);
            Element childElement = this.createElement(childNode);
            elm.getChild("children").addContent(childElement);
            //�ݹ����
            createTree(childElement,childNode);
        }
    }

    //����xml�ļ�����Ԫ�ؽڵ�
    private Element createElement(FuncRightResource node){
        Element elm = new Element("entity");
        elm.setAttribute("id", node.getFuncId());
        elm.addContent(new Element("text").setText(node.getPermName()));
        //elm.addContent(new Element("qtip").setText(node.getPermName()));
        elm.addContent(new Element("href").setText(node.getFuncUri()));
        boolean isLeaf = false;
        if(node.getFuncType() == 1){
        	isLeaf = true;
        }
        elm.addContent(new Element("leaf").setText(Boolean.valueOf(isLeaf).toString()));
//        if(node.getParentid().equals("0")){
//            elm.addContent(new Element("icon").setText(this.IMAGE_ROOT));
//            elm.addContent(new Element("iconCls").setText(this.IMAGE_ROOT));
//        }
//        else if(MenuNode.MENU_NODE_TYPE_DIR.equals(node.getType())){
//            elm.addContent(new Element("icon").setText(this.IMAGE_FOLDER_OPEN));
//        	elm.addContent(new Element("iconCls").setText(this.IMAGE_FOLDER_CLOSE));
//        }
//        else{
//            elm.addContent(new Element("icon").setText(this.IMAGE_LEAF_OPEN));
//        	elm.addContent(new Element("iconCls").setText(this.IMAGE_LEAF_CLOSE));
//        }

        elm.addContent(new Element("children"));
        
        return elm;
    }

    /**
     * �ж��Ƿ�ΪҶ�ӽڵ�
     * @param node  �ڵ����
     * @return  ��:true  ��:false
     */
    private boolean isLeafNode(FuncRightResource node){
    	if(node.getFuncType() == 1){
    		return true;
    	}
    	else{
    		return false;
    	}
    }

    /**
     * ȡ���ڵ�
     * @return �˵������ڵ�
     */
    private FuncRightResource getRootNode(){
        FuncRightResource node = null;
        for(int i=0;i<al_DataBuffer.size();i++){
            node = (FuncRightResource)al_DataBuffer.get(i);
            if(node.getParentId().equals("0")){
                break;
            }
        }
        return node;
    }

    //ȡһ���ڵ���ӽڵ�
    private List<FuncRightResource> getChildNodes(FuncRightResource node){
        List<FuncRightResource> al = new ArrayList<FuncRightResource>();
        String nodeID = node.getFuncId();

        for(int i=0;i<al_DataBuffer.size();i++){
            FuncRightResource tmp = (FuncRightResource)al_DataBuffer.get(i);
            if(nodeID.trim().equals(tmp.getParentId())){
                al.add(tmp);
            }
        }

        return al;
    }

}