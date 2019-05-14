package com.rab.framework.comm.lc;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import com.rab.framework.comm.util.DateUtils;
import com.rab.framework.comm.util.FileUtils;

/**
 * 
 * <P>Title: LicenseUtils</P>
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
public class Utils {
    /**
     * 创建公钥/私钥对
     *
     * @return
     */
    public static KeyPair createKeyPair() throws Exception {
        KeyPair keyPair = null;

        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DSA");
        SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
        random.setSeed(1000);
        keyGen.initialize(1024, random);

        keyPair = keyGen.generateKeyPair();

        /**
         * 将公钥信息保存在文件中
         */
        FileOutputStream out1 = new FileOutputStream("public.key");
        out1.write(keyPair.getPublic().getEncoded());
        out1.flush();
        out1.close();
        out1.close();


        /**
         * 将私钥对象序列化到文件中
         */
        FileOutputStream out2 = new FileOutputStream("private.key");
        ObjectOutputStream oout2 = new ObjectOutputStream(out2);
        oout2.writeObject(keyPair.getPrivate());
        oout2.flush();
        oout2.close();
        out2.close();

        return keyPair;
    }

    public static byte[] getDataFromFile(String file) {
        byte[] data = new byte[0];

        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] buffer = new byte[1024];
            int len = fis.read(buffer);
            while (len > 0) {
                byte[] tmp = new byte[data.length + len];
                System.arraycopy(data, 0, tmp, 0, data.length);
                System.arraycopy(buffer, 0, tmp, data.length, len);
                data = tmp;
                len = fis.read(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return data;
    }

    /**
     * 根据系统设定的license文件名称, 加载license配置信息
     */
    public static AuthorizationInfo loadconfig(String licensefilename) {
        Map<String,AuthorizationItem> map = new HashMap<String,AuthorizationItem>();
        AuthorizationInfo authInfo = new AuthorizationInfo();
        authInfo.setLicenseObjects(map);

        FileUtils fu = new FileUtils(licensefilename);
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(fu.getInputStream());

            Element root = doc.getRootElement();

            authInfo.setFormat(root.getAttributeValue("format"));
            authInfo.setProduct(root.getAttributeValue("product"));
            authInfo.setRelease(root.getAttributeValue("release"));

            List<Element> list = root.getChildren();
            for (int i = 0; i < list.size(); i++) {
                Element license = (Element) list.get(i);
                String component = license.getAttributeValue("component");
                String expiration = license.getAttributeValue("expiration");
                String licensee = license.getAttributeValue("licensee");
                String signature = license.getAttributeValue("signature");
                String hardid = license.getAttributeValue("hardid");

                AuthorizationItem lo = new AuthorizationItem();
                
                //组件名称
                lo.setComponentName(component);

                //超期时间
                if (expiration.equalsIgnoreCase("unlimited")) {
                    lo.setExpiration(null); //永不过期
                } else {
                    lo.setExpiration(DateUtils.parseDate(expiration.trim()));
                }

                //授权对象
                lo.setLicensee(licensee);

                //硬件码
                if (hardid.equalsIgnoreCase("unlimited")) {
                    lo.setHardid(null);
                } else {
                	lo.setHardid(hardid);
                }

                //加密信息
                lo.setSignature(signature);

                map.put(component, lo);
            }
        } catch (Exception e) {
            map.clear();
        }

        return authInfo;
    }

    /**
     * 将licenseObject信息转换为加密用的标识字符串返回
     *
     * @param lo
     * @return
     */
    public static String createLicenseInfo(AuthorizationItem lo) {
        String component = lo.getComponentName();
        Calendar expiration = lo.getExpiration();
        String licensee = lo.getLicensee();
        String hardid = lo.getHardid();
        
        String sum = "";
        sum += "#" + component;

        if (expiration == null) {
            sum += "#" + "unlimited";
        } else {
            sum += "#" + expiration.get(Calendar.YEAR) + "-"
                    + (expiration.get(Calendar.MONTH) + 1) + "-"
                    + expiration.get(Calendar.DATE);
        }

        sum += "#" + licensee;

        if(hardid == null){
        	sum += "#" + "unlimited";
        }
        else{
        	String tmp = hardid.toUpperCase();
        	int posB = tmp.indexOf(":");
        	String hardcode = tmp.substring(posB+1);
        	
        	sum += "#" + hardcode;
        }
        
        sum += "#";

        return sum;
    }

    /**
     * 数据输出
     *
     * @param info
     * @param data
     */
    public static void print(String info, byte[] data) {
        System.out.print(info);
        System.out.println("  len=" + data.length + "; ");

        for (int i = 0; i < data.length; i++) {
            int b = (data[i] + 256) % 256;
            String s = "" + b;
            if (b < 10) {
                s = "  " + b;
            } else if (b < 100) {
                s = " " + b;
            }

            if (i > 0 && (i + 1) % 5 == 0) {
                System.out.println("(byte)" + s + ", ");
            } else {
                System.out.print("(byte)" + s + ", ");
            }
        }
        System.out.println();
    }

	public static void paseLicenseKey(){
		String file = "c:/test/licenseKeyStore";
		
		try {
			InputStream in = new FileInputStream(file);
			
			byte[] buff = new byte[10240];
			int len = in.read(buff);
			while(len > 0){
				
				for(int i=0; i<len; i++){
					int b = buff[i];
					
					if(b < 0){
						b += 256;
					}
					
					String s = "";
					if(b < 10){
						s = "(byte)  " + b + ",  ";
					}
					else if(b < 100){
						s = "(byte) " + b + ",  ";
					}
					else{
						s = "(byte)" + b + ",  ";
					}
					
					if((i+1) % 5 == 0){
						System.out.println(s);
					}
					else{
						System.out.print(s);
					}
				}
				
				len = in.read(buff);
			}
			in.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	    
    public static void main(String[] argvs) {
        try {
            Utils.paseLicenseKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
