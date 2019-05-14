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
 * <P>程序说明：</P>
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
	 * storepass, 参考keytool参数
	 */
	private String storepass = "";  
	
	/**
	 * keypass, 参考keytool参数        
	 */
	private String keypass = "";   
	
	/**
	 * 被加密的类名称
	 */
	private String className = "com.rab.framework.domain.server.VHAppContextImp";
	
	/**
	 * 输出文件目录
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
     * license文件加密
     *
     * @param licenseFile
     */
    public void createEncryptLicense(String licenseFile, String outFile) {
        try {
            //1. 加载license配置文件, 生成LicenseObject对象
            AuthorizationInfo authInfo = this.loadconfig(licenseFile);
            Map<String, AuthorizationItem> map = authInfo.getLicenseObjects();

            //2. 处理LicenseObject对象
            PrivateKey privKey = this.getPrivatekey();
            encrypt(map, privKey);

            //3. 写回配置文件
            createEncryptLicenseFile(authInfo, outFile);

            System.out.println("输入文件: " + new File(licenseFile).getAbsolutePath());
            System.out.println("输出文件: " + new File(outFile).getAbsolutePath());
            System.out.println("\r\n处理完毕!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 信息加密
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
     * 加密
     *
     * @param lo
     * @param privKey
     * @throws Exception
     */
    private void encryptLicenseInfo(AuthorizationItem lo, PrivateKey privKey) throws Exception {
    	 String strInfo = Utils.createLicenseInfo(lo);
    	byte[] data = strInfo.getBytes("UTF-8");
        
        //计算摘要
        MessageDigest md = MessageDigest.getInstance("SHA");
        md.update(data);
        byte[] digest = md.digest();
        
        //对摘要加密
        Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		c2.init(Cipher.ENCRYPT_MODE, privKey);
		byte[] sig = c2.doFinal(digest); // 加密后的数据
        
        //base64编码
        String b64Encoding = Base64.encodeBytes(sig);
        lo.setSignature(b64Encoding);
//        System.out.println("in = " + new String(data));
//        System.out.println("data.length = " + data.length);
//        System.out.println("digest.length = " + digest.length);
//        System.out.println("out = " + b64Encoding);
    }


    /**
     * 返回私有key对象
     *
     * @return
     */
    private PrivateKey getPrivatekey() throws Exception {
		InputStream is = new ByteArrayInputStream(LcKeyStore.lcKeyStore);
		KeyStore ks = KeyStore.getInstance("JKS"); // 加载密码库
		char[] kspwd = this.storepass.toCharArray(); // 密码库访问口令
		char[] keypwd = this.keypass.toCharArray(); // 密码访问口令
		ks.load(is, kspwd); // 加载证书
		PrivateKey priv = (PrivateKey) ks.getKey("licenseKey", keypwd); // 获取证书私钥
		is.close();

        
        return priv;
    }


    /**
     * 输出加密文件
     *
     * @param authInfo
     * @param outFile
     */
    private void createEncryptLicenseFile(AuthorizationInfo authInfo, String outFile) throws Exception {
    	//创建根
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

        //输出到 String
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
     * 根据系统设定的license文件名称, 加载license配置信息
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
            //组件名称
            lo.setComponentName(component);


            //超期时间
            if (expiration.equalsIgnoreCase("unlimited")) {
                lo.setExpiration(null); //永不过期
            } 
            else {
                lo.setExpiration(DateUtils.parseDate(expiration.trim()));
            }

            //授权对象
            lo.setLicensee(licensee);
            
            if(hardid.equals("unlimited")){
            	lo.setHardid(null);
            }
            else{
            	lo.setHardid(hardid);
            }
            
            //加密信息
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

        sb.append("license.xml文件加密工具 [v1.0 lfw 2010-10-15]\r\n");
        sb.append("\r\n");
        sb.append("命令格式: command -storepass <storepass> -keypass <keypass> in out\r\n");
        sb.append("\r\n");
        sb.append("参数说明: \r\n");
        sb.append("    storepass:  密码仓库的访问口令 \r\n");
        sb.append("    keypass:    密码的访问口令 \r\n");
        sb.append("    in:         待加密的license文件路径, 默认为当前路径下的名为 \r\n");
        sb.append("                license.xml 的文件\r\n");
        sb.append("    out:        加密后输出文件路径, 默认输出文件为当前路径下的名为 \r\n");
        sb.append("                out.xml 的文件\r\n");

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