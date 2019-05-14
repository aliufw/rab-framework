package com.rab.framework.comm.lc;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class Test {

	public void test01(){
		try {
			FileOutputStream out = new FileOutputStream("d:/store");
			out.write(LcKeyStore.lcKeyStore);
			out.flush();
			out.close();

			FileOutputStream out2 = new FileOutputStream("d:/cer");
			out2.write(LcKeyCer.lcKeyCer);
			out2.flush();
			out2.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void test02(){
		try {
			FileInputStream in = new FileInputStream("e:/server.cer");
			byte[] data = new byte[10240];
			int len = in.read(data);
//			System.out.println(len);
			
			for(int i=0; i<len; i++){
				int b = data[i];
				if(b<0){
					b += 256;
				}
				
				String s = "";
				if(b<10){
					s += "  " + b;
				}
				else if(b<100){
					s += " " + b;
				}
				else{
					s += "" + b;
				}
				
				s = "(byte)" + s;
				
				if((i+1)%5 == 0){
					System.out.println(s + ", ");
//					System.out.print("\t");
				}
				else{
					System.out.print(s + ", ");
				}
			}
//			System.out.println((byte)129);
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		Test t = new Test();
		t.test02();
//		System.out.println(0%5);
	}
	//keytool -genkey -alias tomcat -keyalg RSA -keystore e:/mykey -storepass 111111 -keypass 111111 
	//keytool -export -alias tomcat -storepass 111111 -file e:/server.cer -keystore e:/mykey 
}
