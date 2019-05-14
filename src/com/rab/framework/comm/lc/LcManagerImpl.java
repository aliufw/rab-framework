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
 * <P>����˵����</P>
 * <P> * license���࣬����������¹��ܣ�
 * 1. ����license�����ļ�
 * 2. ����ļ���Ϣ����Ч��
 * 3. ���Ƽ��ع���ģ��
 * 4. license��Ȩ������Ϣ�����ڼ��
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
     * ��̬˽������, license�ļ�����, ���ļ����λ��Ϊϵͳclasspath��·����
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
     * LicenseObject�б�
     */
    private Map<String, AuthorizationItem> licenseObjects = null;

    /**
     * ��Чlicense�����б�
     */
    private Map<String, AuthorizationItem> validateLicenseObjects = null;


    //------------------------------------------------------------------- ������
    protected LcManagerImpl() {

    }

    //------------------------------------------------------------------- public
    public void init(){
        //1. ����license�����ļ�
        AuthorizationInfo authInfo = null;
        try {
			authInfo = Utils.loadconfig(new String(this.file));
		} catch (RuntimeException e) {
            this.output("license.xml �ļ�����ʧ��,������ļ��Ƿ���ȷ!");
            System.exit(-1);
		}
		
        this.licenseObjects = authInfo.getLicenseObjects();

        //2. ����ļ���Ϣ����Ч��
        if (!this.licenseValidate(this.licenseObjects)) {
            this.output("license.xml �ļ���Ϣ����! ������ļ��Ƿ񱻷Ƿ��޸Ĺ�!");
            System.exit(-1);
        }

        //3. ��������Ȩ����ͬ��ǰ�����Ƿ�һ��
        this.validateLicenseObjects = this.checkExpiration(this.licenseObjects);

        //3. ���Ӳ����ͬ��ǰ���豸�Ƿ�һ��
//        this.validateLicenseObjects = this.checkHardid(this.licenseObjects);

    }


    //------------------------------------------------------------------ private

    /**
     * ����license�ļ�����ȷ��,�����ļ��Ƿ񱻴۸Ĺ�
     *
     * @return ��ȷ: true  ���Ƿ��޸Ĺ�: false
     */
    private boolean licenseValidate(Map<String, AuthorizationItem> objects) {
        boolean flag = true;

        try {
    		CertificateFactory cff = CertificateFactory.getInstance("X.509");
    		InputStream is = new ByteArrayInputStream(LcKeyCer.lcKeyCer);
    	        
    		Certificate cf = cff.generateCertificate(is);
    		PublicKey pk1 = cf.getPublicKey(); //�õ�֤���ļ�Я���Ĺ�Կ
    		Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //�����㷨��RSA
    		c1.init(Cipher.DECRYPT_MODE, pk1);

            Iterator<String> iter = objects.keySet().iterator();
            while (iter.hasNext()) {
                Object key = iter.next();
                AuthorizationItem lo = (AuthorizationItem) objects.get(key);
                String strInfo = Utils.createLicenseInfo(lo);
                byte[] byteInfo = strInfo.getBytes("UTF-8");

                //����ժҪ
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(byteInfo);
                byte[] digest = md.digest();
                
//                String s = new String(byteInfo);
                byte[] sig = Base64.decode(lo.getSignature());
                byte[] msg2 = c1.doFinal(sig); // ���ܺ������
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
                
                //���ժҪ���Ȳ���ȣ�������ļ����ܱ��۸ģ�ֱ�ӷ���false
                if(digest.length != msg2.length){ 
                	flag = false;
                	break;
                }
                
                for(int i=0; i<digest.length; i++){ //�ж�����ժҪ��Ϣ�Ƿ����
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
     * ��������Ȩ��Ϣ�Ƿ���
     * ���ط���Ҫ��������Ȩ��Ϣ
     *
     * @param objects ��������Ȩ��Ϣ�б�
     * @return ����Ҫ�����Ȩ��Ϣ�б�
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

            if (licenseObj.getExpiration() == null //null����������
                    || licenseObj.getExpiration().after(current)) {
                map.put(key, licenseObj);
            }
            else{
            	String name = licenseObj.getComponentName();
                this.output(name + " ģ����Ȩ�Ѿ�����,��������������ϵ! ");
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
            
            String tmp = hardid.toUpperCase(); //������дת������,�Ա����ַ����Ƚ�
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
        		//��Ȩ�ļ�������ֹ�������̣�
        		output("��Ȩ�ļ�������ֹ�������̣�\r\n\r\n������Ȩ�ļ�����ȷ�ԣ�");
        		System.exit(-1);
        	}
            
        	currentHardcode = currentHardcode.toUpperCase(); //������дת������,�Ա����ַ����Ƚ�
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
                this.output(name + " ģ��û���ҵ�����֤��Ӳ����ǣ�����Ӳ�������¼�����������������ϵ! ");
            }
        }

    	return map;
    }

    private void output(String msg){
    	String log = "\r\n";
    	log += "+----------------------------------------------------------+\r\n";
    	log += "+ ��Ҫ��Ϣ��ʾ!!!!                                            \r\n";
    	log += "+ " + msg + "            \r\n";
    	log += "+----------------------------------------------------------+\r\n";
    	
    	Logger.log(log);
    }

    //---------------------------------------------------------- LcManager

    /**
     * ��ȡ���Լ��ص����������
     *
     * @return String[],�����˿��Լ��ص����������
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
