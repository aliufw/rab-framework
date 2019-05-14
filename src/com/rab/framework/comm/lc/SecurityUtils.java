package com.rab.framework.comm.lc;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;

import javax.crypto.Cipher;

import com.rab.framework.comm.util.FileUtils;


public class SecurityUtils {
	
	public void encrypt(String className, String outDir, String storepass, String keypass){
		try {
			byte[] data = this.getClassBytes(className);
			PrivateKey privKey = this.getPrivatekey(storepass, keypass);
			Cipher c2 = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			c2.init(Cipher.ENCRYPT_MODE, privKey);
			
			byte[] outData = new byte[0];
			int pos = 0;
			int len = 117;
			while(pos < data.length-1){
				if(pos + len > data.length-1){
					len = data.length-pos;
				}
				
				byte[] tmp = new byte[len];
				System.arraycopy(data, pos, tmp, 0, len);

				byte[] sig = c2.doFinal(tmp); // 加密后的数据
				
				byte[] tmp2 = new byte[outData.length + 128];
				System.arraycopy(outData, 0, tmp2, 0, outData.length);
				System.arraycopy(sig, 0, tmp2, outData.length, 128);
				outData = tmp2;
				
				pos += len;
//				System.out.println(sig.length + "\t" + pos + "\t" + len);
				
			}

			String outFile = className.replace(".", "/") + ".class";
			String path = outFile.substring(0, outFile.lastIndexOf("/"));
			String fileName = outFile.substring(outFile.lastIndexOf("/")+1);
			File dir = new File(outDir, path);
			if(!dir.exists()){
				dir.mkdirs();
			}
			File file = new File(dir, fileName);
			FileOutputStream out = new FileOutputStream(file);
			out.write(outData);
			out.flush();
			out.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public byte[] decrypt(byte[] data) throws Exception{
		byte[]  ret = new byte[0];
		
  		CertificateFactory cff = CertificateFactory.getInstance("X.509");
		InputStream is = new ByteArrayInputStream(LcKeyCer.lcKeyCer);
	        
		Certificate cf = cff.generateCertificate(is);
		PublicKey pk1 = cf.getPublicKey(); //得到证书文件携带的公钥
		Cipher c1 = Cipher.getInstance("RSA/ECB/PKCS1Padding"); //定义算法：RSA
		c1.init(Cipher.DECRYPT_MODE, pk1);

		for(int i=0; i<data.length; i+=128){
			byte[] tmp = new byte[128];
			System.arraycopy(data, i, tmp, 0, 128);
			
			byte[] decryptData = c1.doFinal(tmp); // 解密后的数据
			
			byte[] tmp2 = new byte[ret.length + decryptData.length];
			System.arraycopy(ret, 0, tmp2, 0, ret.length);
			System.arraycopy(decryptData, 0, tmp2, ret.length, decryptData.length);
			ret = tmp2;
		}
		
		return ret;
	}
	
    private PrivateKey getPrivatekey(String storepass, String keypass) throws Exception {
        PrivateKey priv = null;

		InputStream is = new ByteArrayInputStream(LcKeyStore.lcKeyStore);
		KeyStore ks = KeyStore.getInstance("JKS"); // 加载证书库
		char[] kspwd = storepass.toCharArray(); // 证书库密码
		char[] keypwd = keypass.toCharArray(); // 证书密码
		ks.load(is, kspwd); // 加载证书
		priv = (PrivateKey) ks.getKey("licenseKey", keypwd); // 获取证书私钥
		is.close();

        
        return priv;
    } 

	
    private byte[] getClassBytes(String className) throws Exception{
    	
    	String path = className.replace(".", "/") + ".class";
    	
    	FileUtils fileUtils = new FileUtils(path);
		InputStream in = fileUtils.getInputStream();
//		FileInputStream in = new FileInputStream(file);
		
		byte[] data = new byte[0];
		byte[] buffer = new byte[1024];
		int len = in.read(buffer);
		while(len > 0){
			byte[] tmp = new byte[data.length + len];
			System.arraycopy(data, 0, tmp, 0, data.length);
			System.arraycopy(buffer, 0, tmp, data.length, len);
			data = tmp;
			len = in.read(buffer);
		}

		return data;
    }

    public static void main(String[] argvs){
    	SecurityUtils su = new SecurityUtils();
    	String className = "com.rab.framework.domain.server.VHAppContextImp";
    	su.encrypt(className, "d:/", "***","***");
    }
    
}
