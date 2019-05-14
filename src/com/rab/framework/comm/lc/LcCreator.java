package com.rab.framework.comm.lc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.crypto.Cipher;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.rab.framework.comm.util.DateUtils;

/**
 * 
 * <P>Title: LicenseCreator</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-11-3</P>
 *
 */
public class LcCreator {
	/**
	 * storepass, �ο�keytool����
	 */
	private String storepass = "";  
	
	/**
	 * keypass, �ο�keytool����        
	 */
	private String keypass = "";   
	
	/**
	 * �����ܵ�������
	 */
	private String className = "com.rab.framework.domain.server.VHAppContextImp";
	
	/**
	 * ����ļ�Ŀ¼
	 */
	private File outDir = null;

	
	public String getStorepass() {
		return storepass;
	}

	public void setStorepass(String storepass) {
		this.storepass = storepass;
	}

	public String getKeypass() {
		return keypass;
	}

	public void setKeypass(String keypass) {
		this.keypass = keypass;
	}

	/**
     * license�ļ�����
     *
     * @param licenseFile
     */
    public void createEncryptLicense(String licenseFile, String outFile) {
        try {
            //1. ����license�����ļ�, ����LicenseObject����
            AuthorizationInfo authInfo = this.loadconfig(licenseFile);
            Map<String, AuthorizationItem> map = authInfo.getLicenseObjects();

            //2. ����LicenseObject����
            PrivateKey privKey = this.getPrivatekey();
            encrypt(map, privKey);

            //3. д�������ļ�
            createEncryptLicenseFile(authInfo, outFile);

            System.out.println("�����ļ�: " + new File(licenseFile).getAbsolutePath());
            System.out.println("����ļ�: " + new File(outFile).getAbsolutePath());
            System.out.println("\r\n�������!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ��Ϣ����
     *
     * @param map
     */
    private void encrypt(Map<String, AuthorizationItem> map, PrivateKey privKey) throws Exception {
        Iterator<String> iter = map.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            AuthorizationItem lo = (AuthorizationItem) map.get(key);
            encryptLicenseInfo(lo, privKey);
        }
    }

    /**
     * ����
     *
     * @param lo
     * @param privKey
     * @throws Exception
     */
    private void encryptLicenseInfo(AuthorizationItem lo, PrivateKey privKey) throws Exception {
    	 String strInfo = Utils.createLicenseInfo(lo);
    	byte[] data = strInfo.getBytes("UTF-8");
        
        //����ժҪ
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(data);
        byte[] digest = md.digest();
        
        //��ժҪ����
        Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		c2.init(Cipher.ENCRYPT_MODE, privKey);
		byte[] sig = c2.doFinal(digest); // ���ܺ������
        
        //base64����
        String b64Encoding = Base64.encodeBytes(sig);
        lo.setSignature(b64Encoding);
//        System.out.println("in = " + new String(data));
//        System.out.println("data.length = " + data.length);
//        System.out.println("digest.length = " + digest.length);
//        System.out.println("out = " + b64Encoding);
    }


    /**
     * ����˽��key����
     *
     * @return
     */
    private PrivateKey getPrivatekey() throws Exception {
		InputStream is = new ByteArrayInputStream(LcKeyStore.lcKeyStore);
		KeyStore ks = KeyStore.getInstance("JKS"); // ���������
		char[] kspwd = this.storepass.toCharArray(); // �������ʿ���
		char[] keypwd = this.keypass.toCharArray(); // ������ʿ���
		ks.load(is, kspwd); // ����֤��
		PrivateKey priv = (PrivateKey) ks.getKey("licenseKey", keypwd); // ��ȡ֤��˽Կ
		is.close();

        
        return priv;
    }


    /**
     * ��������ļ�
     *
     * @param authInfo
     * @param outFile
     */
    private void createEncryptLicenseFile(AuthorizationInfo authInfo, String outFile) throws Exception {
    	//������
        DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=dbFact.newDocumentBuilder();
		Document doc=builder.newDocument();
		Element root= doc.createElement("licenses");
		
        root.setAttribute("format", authInfo.getFormat());
        root.setAttribute("product", authInfo.getProduct());
        root.setAttribute("release", authInfo.getRelease());
		doc.appendChild(root);
		
        Map<String,AuthorizationItem> licenseObjects = authInfo.getLicenseObjects();
        Iterator<String> iter = licenseObjects.keySet().iterator();
        while (iter.hasNext()) {
            Object key = iter.next();
            AuthorizationItem lo = (AuthorizationItem) licenseObjects.get(key);
            Element license = doc.createElement("license");
            
            license.setAttribute("component", lo.getComponentName());

            if (lo.getExpiration() == null) {
                license.setAttribute("expiration", "unlimited");
            } else {
            	String dateStr = DateUtils.toDateStr(lo.getExpiration());
                license.setAttribute("expiration", dateStr);
            }


            license.setAttribute("licensee", lo.getLicensee());

            if(lo.getHardid() == null){
            	license.setAttribute("hardid", "unlimited");
            }
            else{
            	license.setAttribute("hardid", lo.getHardid());
            }
            
            
            license.setAttribute("signature", lo.getSignature());

            root.appendChild(license);
        }

        //����� String
		File file = new File(outFile);
		this.outDir = file.getParentFile();
		if(!outDir.exists()){
			outDir.mkdirs();
		}
        Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.setOutputProperty(OutputKeys.INDENT,"yes");
        trans.transform(new DOMSource(doc),new StreamResult(new FileOutputStream(file, false)));           

    }

    /**
     * ����ϵͳ�趨��license�ļ�����, ����license������Ϣ
     */
    private AuthorizationInfo loadconfig(String licensefilename) throws Exception {
        Map<String, AuthorizationItem> map = new HashMap<String, AuthorizationItem>();
        AuthorizationInfo authInfo = new AuthorizationInfo();
        authInfo.setLicenseObjects(map);

        DocumentBuilderFactory dbFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder=dbFact.newDocumentBuilder();
        Document doc = builder.parse(licensefilename);
        Element root = doc.getDocumentElement();
        
        authInfo.setFormat (root.getAttribute("format"));
        authInfo.setProduct(root.getAttribute("product"));
        authInfo.setRelease(root.getAttribute("release"));
        NodeList nodeList = root.getChildNodes();
        
        for(int i=0;i<nodeList.getLength();i++) {
            Node node = (Node) nodeList.item(i);
            if(node.getAttributes()==null){
            	continue;
            }
            
            String component  = node.getAttributes().getNamedItem("component").getNodeValue();
            String expiration = node.getAttributes().getNamedItem("expiration").getNodeValue();
            String licensee   = node.getAttributes().getNamedItem("licensee").getNodeValue();
            String hardid     = node.getAttributes().getNamedItem("hardid").getNodeValue();
            String signature  = node.getAttributes().getNamedItem("signature").getNodeValue();

            AuthorizationItem lo = new AuthorizationItem();
            //�������
            lo.setComponentName(component);


            //����ʱ��
            if (expiration.equalsIgnoreCase("unlimited")) {
                lo.setExpiration(null); //��������
            } 
            else {
                lo.setExpiration(DateUtils.parseDate(expiration.trim()));
            }

            //��Ȩ����
            lo.setLicensee(licensee);
            
            if(hardid.equals("unlimited")){
            	lo.setHardid(null);
            }
            else{
            	lo.setHardid(hardid);
            }
            
            //������Ϣ
            lo.setSignature(signature);

            map.put(component, lo);
        }

        return authInfo;
    }

    public void createEncryptClass(){
    	SecurityUtils su = new SecurityUtils();
    	su.encrypt(className, outDir.getAbsolutePath(), storepass, keypass);
    }
    
    protected void prompt() {
        StringBuffer sb = new StringBuffer();

        sb.append("license.xml�ļ����ܹ��� [v1.0 lfw 2010-10-15]\r\n");
        sb.append("\r\n");
        sb.append("�����ʽ: command -storepass <storepass> -keypass <keypass> in out\r\n");
        sb.append("\r\n");
        sb.append("����˵��: \r\n");
        sb.append("    storepass:  ����ֿ�ķ��ʿ��� \r\n");
        sb.append("    keypass:    ����ķ��ʿ��� \r\n");
        sb.append("    in:         �����ܵ�license�ļ�·��, Ĭ��Ϊ��ǰ·���µ���Ϊ \r\n");
        sb.append("                license.xml ���ļ�\r\n");
        sb.append("    out:        ���ܺ�����ļ�·��, Ĭ������ļ�Ϊ��ǰ·���µ���Ϊ \r\n");
        sb.append("                out.xml ���ļ�\r\n");

        System.out.println(sb.toString());
    }


    public static void main(String[] args) {
        LcCreator lc = new LcCreator();
        try {
            if ((args.length == 0 || args.length > 6)) {
                lc.prompt();
                System.exit(0);
            }

            if(!"-storepass".equalsIgnoreCase(args[0])){
            	lc.prompt();
            	System.exit(0);
            }

            if(!"-keypass".equalsIgnoreCase(args[2])){
            	lc.prompt();
            	System.exit(0);
            }

            lc.setStorepass(args[1]);
            lc.setKeypass(args[3]);
            
            String in = args[4];
            String out = args[5];
            
            lc.createEncryptLicense(in, out);
            lc.createEncryptClass();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

//keytool -genkey -alias tomcat -keyalg RSA -keystore e:/mykey -storepass 111111 -keypass 111111 
//keytool -export -alias tomcat -storepass 111111 -file e:/server.cer -keystore e:/mykey 