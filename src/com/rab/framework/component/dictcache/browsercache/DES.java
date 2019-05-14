package com.rab.framework.component.dictcache.browsercache;

import java.security.NoSuchAlgorithmException;
import java.security.Security;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 
 * <P>Title: DES</P>
 * <P>Description: </P>
 * <P>����˵����DES���ܵģ��ļ��й�����������,���ܡ�����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-6-16</P>
 *
 */
public class DES {
    private String Algorithm = "DES";
    private SecretKey deskey;
    private Cipher cipher;
    private byte[] encryptorData;
    private byte[] decryptorData;

    /**
     * ��ʼ�� DES ʵ��
     */
    public DES() {
          init();
    }

    /**
     * ��ʼ�� DES �����㷨��һЩ����
     */
    public void init() {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        try {
             //  keygen = KeyGenerator.getInstance(Algorithm);
          //     deskey = keygen.generateKey();
//          ������Կ
           String key = "12345678"; 
   	       byte[]   keyByte   =   key.getBytes();   
   	       DESKeySpec   keySpec   =   new   DESKeySpec(keyByte);   
   	       SecretKeyFactory   keyFactory   =   SecretKeyFactory.getInstance(Algorithm);   
   	       deskey = keyFactory.generateSecret(keySpec);   
          
           cipher = Cipher.getInstance(Algorithm);
            
               
         }
         catch(NoSuchAlgorithmException ex){
            ex.printStackTrace();
        }
         catch(NoSuchPaddingException ex){
            ex.printStackTrace();
        }catch(Exception ex){
        	ex.printStackTrace();
        }
       }

    /**
     * �� byte[] ���м���
     * @param datasource Ҫ���ܵ�����
     * @return ���ؼ��ܺ�� byte ����
     */
     public byte[] createEncryptor(byte[] datasource) {
        try {
         cipher.init(Cipher.ENCRYPT_MODE, deskey);
             encryptorData = cipher.doFinal(datasource);
        }
        catch(java.security.InvalidKeyException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.BadPaddingException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.IllegalBlockSizeException ex){
            ex.printStackTrace();
        }
        return encryptorData;
     }
     
     /**
      * ���ַ�������
      * 
     * @param datasource
     * @return
     * @throws Exception
     */
    public byte[] createEncryptor(String datasource) throws Exception{
        return createEncryptor(datasource.getBytes());
     }
     
    /**
     * �� datasource ������н���
     * @param datasource Ҫ���ܵ�����
     * @return ���ؽ��ܺ�� byte[]
     */
     public byte[] createDecryptor(byte[] datasource) {
        try {
         cipher.init(Cipher.DECRYPT_MODE, deskey);
           decryptorData = cipher.doFinal(datasource);
        }
        catch(java.security.InvalidKeyException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.BadPaddingException ex){
            ex.printStackTrace();
        }
        catch(javax.crypto.IllegalBlockSizeException ex){
            ex.printStackTrace();
        }
        return decryptorData;        
     }
     
     /**
      * 
      * �� DES ���ܹ��� byte����ת��Ϊ�ַ���
      * 
     * @param dataByte
     * @return
     */
    public String byteToString(byte[] dataByte)
     {      
      String returnStr = null;
         BASE64Encoder be = new BASE64Encoder();
         returnStr = be.encode(dataByte);
         return returnStr;      
     }
     /**
      * 
      * ���ַ���ת��ΪDES�㷨���Խ��ܵ�byte����
      * 
     * @param dataByte
     * @return
     * @throws Exception
     */
    public  byte[] stringToByte(String datasource)throws Exception  {         
         BASE64Decoder bd = new BASE64Decoder();
         byte[] sorData = bd.decodeBuffer(datasource);        
         return sorData;     
     }
     /**
      * ��� byte����
      * 
     * @param data
     */
    public void printByte(byte[] data)
     {
     System.out.println("*********��ʼ����ֽ���**********"); 
     System.out.println("�ֽ���: "+data.toString());
      for(int i = 0 ; i < data.length ; i++){
       System.out.println("�� "+i+"�ֽ�Ϊ��"+ data[i]);
      }
      System.out.println("*********��������ֽ���**********");
     }
    
     public static void main(String args[])throws Exception
     {
      //����Դ����
     String encryptorString = "�������������ֶ��������������DES����";     

      DES des  = new DES();
//      System.out.println("����ǰ��byte����"+encryptorString.getBytes().length);
//      //���ܻ�õ�byte����
//      byte[] encryptorByte  = des.createEncryptor(encryptorString);
//      System.out.println("���ܻ��byte���ȣ�"+encryptorByte.length);
//      //���ܺ��byte[] ת�������ַ���
//      String byteToString = des.byteToString(encryptorByte);        
//      
//      System.out.println("����ǰ�����ݣ�"+encryptorString);
//      System.out.println("���ܺ��byte[]");
//      des.printByte(encryptorByte);
//      System.out.println("���ܺ�����ݣ�"+byteToString);
//      
//      /*
//       * ���Զ��ַ�������һϵ�еĴ���
//       */
//      
//      //���ܺ���ַ���
//     String decryptorString = null;
//     
//      //��byteToStringת��Ϊԭ����byte[]
//      byte[] stringToByte = des.stringToByte(byteToString);
//      //��stringToByte���ܺ��byte[]
//      byte[]decryptorByte = des.createDecryptor(stringToByte);
//      //���ܺ��byte[]ת��Ϊԭ�����ַ���
//      decryptorString = new String(decryptorByte);
//      
//      System.out.println("����ǰ�����ݣ�"+byteToString);
//
//     // System.out.println("ת�����Ľ��ܵ�byte[]");
//      des.printByte(stringToByte);
//      //System.out.println("���ܺ�����ݣ�"+decryptorString);
      
       byte[]  test1= encryptorString.getBytes();
       System.out.println("����ǰ���byte���ȣ�"+test1.length);
       byte[] encryptorByte  = des.createEncryptor(test1);
       System.out.println("���ܻ��byte���ȣ�"+encryptorByte.length);
       byte[] decryptorByte = des.createDecryptor(encryptorByte);
       System.out.println("�����ܻ��byte���ȣ�"+decryptorByte.length);
       System.out.println("���ܻ��String��"+new String(decryptorByte));
   }

     }
