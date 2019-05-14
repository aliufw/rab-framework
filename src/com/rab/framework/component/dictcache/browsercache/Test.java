package com.rab.framework.component.dictcache.browsercache;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.Properties;

public class Test {
	public int KMPIndex(byte[] data, byte[] mode, int startpos){
		int[] next = new int[mode.length];
		int i = startpos;
		int j = 0;
		
		getNext(mode,next);
		
		while(i<data.length && j<mode.length){
			if(j==-1 || data[i]==mode[j]){
				i++;
				j++;
			}
			else{
				j=next[j];
			}
		}
		
		if(j>=mode.length){
			return i-mode.length;
		}
		else{
			return -1;
		}
	}

	private void getNext(byte[] data, int[] next){
		int len = data.length;
		int i=0;
		int k=-1;
		next[0] = -1;
		while(i<len-1){
			if(k==-1 || data[i]==data[k]){
				i++;
				k++;
				next[i]=k;
			}
			else{
				k=next[k];
			}
		}
	}
	
//	private void output(byte[] data){
//	output(data, 0, data.length);
//}

//private void output(byte[] data, int offset, int len){
//	System.out.println("=============================");
//	System.out.println(new String(data, offset, len));
//	System.out.println("=============================");
//
//}
	
	public void test01(){
		BrowserCacheManager sd = new BrowserCacheManager();
		
		try {
			sd.setServerip("127.0.0.1");
			sd.setServerport("8090");
			sd.setManagerServletURI("/WebCodeCacheServlet");
			sd.setUpdateCyc("60");
			sd.setLocalrootdir("c:/");
			
			sd.initCacheManager();
//			sd.updateCacheTable();
			String s = sd.getCacheTable("t_dm_pz_pzssyy");
			System.out.println(s);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void test02(){
		String s1 = "<navi XYBZ=Y SSXY_DM=01 MC=×ÔÈ»ÔÖº¦¶ªÊ§ />";
		String s2 = "XY";
		
		byte[] data = s1.getBytes();
		int posB = 0;
		int posE = KMPIndex(data, s2.getBytes(),posB);
		
		System.out.println(s1);
		System.out.println(new String(data, posB, posE-posB));
		
		posB = posE + s2.getBytes().length;
		posE = KMPIndex(data, s2.getBytes(),posB);
		System.out.println(new String(data, posB, posE-posB));
	}
	
	public void test03(){
		Properties props = System.getProperties();
		Iterator<Object> iter = props.keySet().iterator();
		while(iter.hasNext()){
			String key = "" + iter.next();
			String value = props.getProperty(key);
			System.out.println(key + " = " + value);
		}
	}
	
	public void test04(){
		String uri = "http://127.0.0.1:8090/WebCodeCacheServlet";
		String parameter = "tablenames=T_DM_PZ_PZSSYY%2CT_DM_PZ_PZSSCLJGLX%2CT_DM_PZ_PZZB%2C&versions=-1%2C-1%2C-3%2C";
		String serverip = "127.0.0.1";
		int serverport = 8090;
		
		String request = "";
		request += "GET " + uri + "?" + parameter + " HTTP/1.1\r\n";
		request += "Accept: */*\r\n";
		request += "Content-Type: application/x-www-form-urlencoded\r\n";
		request += "User-Agent: Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)\r\n";
		request += "Host: " + serverip + ":" + serverport + "\r\n";
		request += "Pragma: no-cache\r\n";
		request += "Content-Length:" + parameter.getBytes().length + "\r\n";
		request += "Cache-Control: no-cache\r\n";
		request += "\r\n";
//		request += parameter;
		
		try {
			Socket socket = new Socket(serverip,serverport);
			OutputStream out = socket.getOutputStream();
			
			out.write(request.getBytes());

			InputStream is = socket.getInputStream();
			
			byte[] buffer = new byte[1024];
//			int len = is.read(buffer);
//			while(len > 0){
//				System.out.println(new String(buffer,0,len));
//				len = is.read(buffer);
//			}
			
			while(is.available() > 0){
				int len = is.read(buffer);
				System.out.println(new String(buffer,0,len));
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	public static void main(String[] a){
		Test t = new Test();
		t.test01();
	}

	
}
