/*
 * Created on 2004-10-12
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.rab.framework.comm.lc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.crypto.Cipher;

import com.rab.framework.comm.lc.hard.SystemInfo;

/**
 * 
 * <P>Title: LicenseManager</P>
 * <P>Description: </P>
 * <P>程序说明：</P>
 * <P> * license主类，该类完成如下功能：
 * 1. 加载license配置文件
 * 2. 检查文件信息的有效性
 * 3. 控制加载功能模块
 * 4. license授权控制信息运行期检查
</P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-3</P>
 *
 */
public class LcManagerImpl implements LcManager {
    //------------------------------------------------------------------- Fields
    /**
     * 静态私有属性, license文件名称, 该文件存放位置为系统classpath根路径处
     * licensefilename = "license.xml";
     */
    private final char[] file = {
    		'l',
    		'i',
    		'c',
    		'e',
    		'n',
    		's',
    		'e',
    		'.',
    		'x',
    		'm',
    		'l'
    		};

    /**
     * LicenseObject列表
     */
    private Map<String, AuthorizationItem> licenseObjects = null;

    /**
     * 有效license对象列表
     */
    private Map<String, AuthorizationItem> validateLicenseObjects = null;


    //------------------------------------------------------------------- 构造器
    protected LcManagerImpl() {

    }

    //------------------------------------------------------------------- public
    public void init(){
        //1. 加载license配置文件
        AuthorizationInfo authInfo = null;
        try {
			authInfo = Utils.loadconfig(new String(this.file));
		} catch (RuntimeException e) {
            this.output("license.xml 文件解析失败,请检查该文件是否正确!");
            System.exit(-1);
		}
		
        this.licenseObjects = authInfo.getLicenseObjects();

        //2. 检查文件信息的有效性
        if (!this.licenseValidate(this.licenseObjects)) {
            this.output("license.xml 文件信息有误! 请检查该文件是否被非法修改过!");
            System.exit(-1);
        }

        //3. 检查组件授权日期同当前日期是否一致
        this.validateLicenseObjects = this.checkExpiration(this.licenseObjects);

        //3. 检查硬件码同当前的设备是否一致
//        this.validateLicenseObjects = this.checkHardid(this.licenseObjects);

    }


    //------------------------------------------------------------------ private

    /**
     * 分析license文件的正确性,检查改文件是否被篡改过
     *
     * @return 正确: true  被非法修改过: false
     */
    private boolean licenseValidate(Map<String, AuthorizationItem> objects) {
        boolean flag = true;

        try {
    		CertificateFactory cff = CertificateFactory.getInstance("X.509");
    		InputStream is = new ByteArrayInputStream(LcKeyCer.lcKeyCer);
    	        
    		Certificate cf = cff.generateCertificate(is);
    		PublicKey pk1 = cf.getPublicKey(); //得到证书文件携带的公钥
    		Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //定义算法：RSA
    		c1.init(Cipher.DECRYPT_MODE, pk1);

            Iterator<String> iter = objects.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                AuthorizationItem lo = (AuthorizationItem) objects.get(key);
                String strInfo = Utils.createLicenseInfo(lo);
                byte[] byteInfo = strInfo.getBytes("UTF-8");

                //计算摘要
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(byteInfo);
                byte[] digest = md.digest();
                
//                String s = new String(byteInfo);
                byte[] sig = Base64.decode(lo.getSignature());
                byte[] msg2 = c1.doFinal(sig); // 解密后的数据
//              String s2 = new String(msg2,"utf-8");
//              flag = s.equalsIgnoreCase(s2);
                
//                System.out.println("=======================>>");
//                System.out.println(strInfo);
//
//                for(int i=0; i<byteInfo.length; i++){
//                	System.out.print(byteInfo[i] + ", ");
//                }
//                System.out.println();
//                for(int i=0; i<digest.length; i++){
//                	System.out.print(digest[i] + ", ");
//                }
//                System.out.println();
//                for(int i=0; i<msg2.length; i++){
//                	System.out.print(msg2[i] + ", ");
//                }
//                System.out.println("\r\n=======================<<");
                
                //如果摘要长度不相等，则表明文件可能被篡改，直接返回false
                if(digest.length != msg2.length){ 
                	flag = false;
                	break;
                }
                
                for(int i=0; i<digest.length; i++){ //判断两个摘要信息是否相等
                	if(digest[i] != msg2[i]){
                		flag = false;
                		break;
                	}
                }
                
                if (!flag) {
                	break;
                }
            }
        } catch (Exception e) {
        	flag = false;
        }

        return flag;
    }

    

    /**
     * 检查组件授权信息是否超期
     * 返回符合要求的组件授权信息
     *
     * @param objects 待检查的授权信息列表
     * @return 符合要求的授权信息列表
     */
    private Map<String, AuthorizationItem> checkExpiration(Map<String, AuthorizationItem> objects) {
    	if(true){
    		return objects;
    	}
    	
        Map<String, AuthorizationItem> map = new HashMap<String, AuthorizationItem>();
        Calendar current = Calendar.getInstance();
        Iterator<String> iter = objects.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            AuthorizationItem licenseObj = (AuthorizationItem) objects.get(key);

            if (licenseObj.getExpiration() == null //null：永不过期
                    || licenseObj.getExpiration().after(current)) {
                map.put(key, licenseObj);
            }
            else{
            	String name = licenseObj.getComponentName();
                this.output(name + " 模块授权已经过期,请和软件销售商联系! ");
            }
        }
        return map;
    }

    private Map<String, AuthorizationItem> checkHardid(Map<String, AuthorizationItem> objects) {
    	Map<String, AuthorizationItem> map = new HashMap<String, AuthorizationItem>();
    	
        Iterator<String> iter = objects.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            AuthorizationItem licenseObj = (AuthorizationItem) objects.get(key);

            String hardid = licenseObj.getHardid();
            if(hardid == null){
            	map.put(key, licenseObj);
            	continue;
            }
            
            String tmp = hardid.toUpperCase(); //先做大写转换处理,以便于字符串比较
        	int posB = tmp.indexOf(":");
        	String type = tmp.substring(0,posB);
        	String hardcode = tmp.substring(posB+1);
            
        	String currentHardcode = "";
        	SystemInfo si = new SystemInfo();
        	if("CPU".equals(type)){
        		currentHardcode = si.getCpuId();
        	}
        	else if("MAC".equals(type)){
        		currentHardcode = si.getNetMac();
        	}
        	else if("HD".equals(type)){
        		currentHardcode = si.getHdId();
        	}
        	else{
        		//授权文件有误，终止启动过程！
        		output("授权文件有误，终止启动过程！\r\n\r\n请检查授权文件的正确性！");
        		System.exit(-1);
        	}
            
        	currentHardcode = currentHardcode.toUpperCase(); //先做大写转换处理,以便于字符串比较
        	StringTokenizer st = new StringTokenizer(currentHardcode, ",");
        	boolean flag = false;
        	while(st.hasMoreElements()){
        		String currHardcode = "" + st.nextElement();
        		if(hardcode.equals(currHardcode)){
        			flag = true;
        			break;
        		}
        	}
        	if(flag){
        		map.put(key, licenseObj);
        	}
        	else{
            	String name = licenseObj.getComponentName();
                this.output(name + " 模块没有找到可验证的硬件标记，请检查硬件变更记录，或与软件销售商联系! ");
            }
        }

    	return map;
    }

    private void output(String msg){
    	String log = "\r\n";
    	log += "+----------------------------------------------------------+\r\n";
    	log += "+ 重要信息提示!!!!                                            \r\n";
    	log += "+ " + msg + "            \r\n";
    	log += "+----------------------------------------------------------+\r\n";
    	
    	Logger.log(log);
    }

    //---------------------------------------------------------- LcManager

    /**
     * 获取可以加载的组件的名称
     *
     * @return String[],包含了可以加载的组件的名称
     */
    public String[] getLicenseComponents() {
        Iterator<String> iter = this.validateLicenseObjects.keySet().iterator();
        ArrayList<String> al = new ArrayList<String>();
        while (iter.hasNext()) {
            al.add(iter.next());
        }
        String[] componentNames = new String[al.size()];
        componentNames = (String[]) al.toArray(componentNames);

        return componentNames;
    }


}
