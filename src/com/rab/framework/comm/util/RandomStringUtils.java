package com.rab.framework.comm.util;

import java.util.Random;

/**
 * 
 * <P>Title: RandomStringUtils</P>
 * <P>Description: </P>
 * <P>����˵����</P>
 * <P></P>
 *
 * <P>Copyright: Copyright (c) 2011 </P>
 * <P>@author lfw</P>
 * <P>version 1.0</P>
 * <P>2010-8-9</P>
 *
 */
public class RandomStringUtils {

    private static final Random RANDOM = new Random();

    /**
     * ����ָ�����ȵ�����ַ����������ͱ���Integer�ɱ�ʾ������������Ϊ��ֵ�������ַ�Ϊ�ز�
     * @param count   �����ַ����ĳ��ȣ��ַ�������
     * @return        ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�String s = RandomStringUtils.random(10);
     * <br>����ֵ��s = "�b?�����?��?�Kݏ";
     * <br>������һ�ѹ����������ܶ����ף�������
     */
    public  static String random(int count) {
        return random(count, false, false);
    }

    /**
     * ����ָ�����ȵ�ASCII�ַ�����ַ���
     * @param count   �����ַ����ĳ��ȣ��ַ�������
     * @return        ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�String s = RandomStringUtils.randomAscii(20);
     * <br>����ֵ��s = "w ;-_bz?wN]8q10?P B";
     * <br>����ֵ�ɿ���ASCII�ַ����
     */
    public  static String randomAscii(int count) {
        return random(count, 32, 127, false, false);
    }

    /**
     * ����ָ�����ȵ�Ӣ����ĸ����ַ���
     * @param count   �����ַ����ĳ��ȣ��ַ�������
     * @return        ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�String s = RandomStringUtils.randomAlphabetic(20);
     * <br>����ֵ��s = "yGImRoOBvBLNaFihDoVo";
     * <br>����ֵ�ɴ�СдӢ����ĸ���
     */
    public  static String randomAlphabetic(int count) {
        return random(count, true, false);
    }

    /**
     * ����ָ�����ȵ����֡�Ӣ����ĸ��ϵ�����ַ���
     * @param count   �����ַ����ĳ��ȣ��ַ�������
     * @return        ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�String s = RandomStringUtils.randomAlphanumeric(20);
     * <br>����ֵ��s = "MevThO39xlK6b6Asg3Uq";
     * <br>����ֵ�����֡�Ӣ����ĸ������
     */
    public  static String randomAlphanumeric(int count) {
        return random(count, true, true);
    }

    /**
     * ����ָ�����ȵ���������ַ���
     * @param count  �����ַ����ĳ��ȣ��ַ�������
     * @return       ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�String s = RandomStringUtils.randomNumeric(20);
     * <br>����ֵ��s = "23547933728487290410";
     * <br>����ֵ���������
     */
    public  static String randomNumeric(int count) {
        return random(count, false, true);
    }

    /**
     * ����ָ�����ȵ�����ַ���
     * @param count    �����ַ����ĳ��ȣ��ַ�������
     * @param letters  true �����ַ������Ƿ���԰�����ĸ�ַ�
     * @param numbers  true �����ַ������Ƿ���԰��������ַ�
     * @return         ���ع��������ַ���
     */
    private  static String random(int count, boolean letters, boolean numbers) {
        return random(count, 0, 0, letters, numbers);
    }

    /**
     * ����ָ�����ȵ�����ַ���
     * @param count    �����ַ����ĳ��ȣ��ַ�������
     * @param start    �����ַ��������زĵ���Сֵ(�ַ�����ֵ)
     * @param end      �����ַ��������زĵ����ֵ(�ַ�����ֵ)
     * @param letters  true �����ַ������Ƿ���԰�����ĸ�ַ�
     * @param numbers  true �����ַ������Ƿ���԰��������ַ�
     * @return         ���ع��������ַ���
     */
    private  static String random(int count, int start, int end, boolean letters, boolean numbers) {
        return random(count, start, end, letters, numbers, null);
    }

    /**
     * ���ݸ������ַ�����Ϊ�ز��ַ�������������ַ���
     * @param count   �����ַ����ĳ��ȣ��ַ�������
     * @param set     �����ַ��������ز��ַ�����
     * @return        ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�
     * <br>Stirng c = "abcdefg";
     * <br>String s = RandomStringUtils.random(20,c);
     * <br>����ֵ��s = "ffbeaefbbdaafecagegc";
     * <br>����ֵ�ɸ����ز��ַ����
     */
    public  static String random(int count, String set) {
        return random(count, set.toCharArray());
    }

    /**
     * ���ݸ����ز��ַ�������������ַ���
     * @param count �����ַ����ĳ��ȣ��ַ�������
     * @param set   �����ַ��������ز��ַ�����
     * @return      ���ع��������ַ���
     * <br>
     * <br>����
     * <br>���ã�
     * <br>char c[] = "abcdefg".toCharArray();
     * <br>String s = RandomStringUtils.random(20,c);
     * <br>����ֵ��s = "gaeacgeebagacgdbfcff";
     * <br>����ֵ�ɸ����ز��ַ����
     */
    public  static String random(int count, char[] set) {
        return random(count, 0, set.length - 1, false, false, set);
    }

    /**
     * ����ָ�����ȵ�����ַ���
     * @param count    �����ַ����ĳ��ȣ��ַ�������
     * @param start    �����ַ��������زĵ���Сֵ(�ַ�����ֵ)
     * @param end      �����ַ��������زĵ����ֵ(�ַ�����ֵ)
     * @param letters  true �����ַ������Ƿ���԰�����ĸ�ַ�
     * @param numbers  true �����ַ������Ƿ���԰��������ַ�
     * @param set      �����ַ��������ز��ַ�����
     * @return         ���ع��������ַ���
     *
     * <br>����˵����
     * <br>�α���count,start,endӦΪ�Ǹ�������
     * <br>���ز��ַ�����set�ǿ�ʱ���α���start��endȡֵӦΪset����Ч�±�
     * <br>�����ַ���ʧ��ʱ������null
     */
    private  static String random(int count, int start, int end, boolean letters, boolean numbers, char[] set) {
        StringBuffer buffer = new StringBuffer();

        if(count <=0 || start <0 || end < 0 || start > end){
            return null;
        }
        if(set != null && (start > set.length || end - start + 1 > set.length)){
            return null;
        }
        if(set != null && end >= set.length){
            return null;
        }

        //������ݵ�ȡֵ��Χ
        int gap = 0;             //�������ȡֵ��Χ
        if( (start == 0) && (end == 0) ) {
            //Ĭ��Ϊ��' '(�ո�)���ַ�'z'��asciiֵ
            end = (int)'z';
            start = (int)' ';
            gap = end - start+1;             //�������ȡֵ��Χ
            if(!letters && !numbers) {
                //������������ƣ���ȡֵ��ΧΪ����������Integer�Ŀɱ�ʾ��Χ
                start = 0;
                end = Integer.MAX_VALUE;
                gap = end - start;             //�������ȡֵ��Χ
            }
        }
        else{
            gap = end - start+1;             //�������ȡֵ��Χ
        }

        while(count-- != 0) {
            char ch;
            if(set == null) {
                ch = (char)(RANDOM.nextInt(gap) + start);
            }
            else {
                ch = set[RANDOM.nextInt(gap) + start];
            }

            if( (letters && numbers && Character.isLetterOrDigit(ch)) ||//����Ϊ�ַ������֣�����Ϊ�ַ�������
                (letters && Character.isLetter(ch)) ||                  //����Ϊ�ַ�������Ϊ�ַ�
                (numbers && Character.isDigit(ch)) ||                   //����Ϊ���֣�����Ϊ����
                (!letters && !numbers)                                  //���岻�޶��ַ�������
              )
            {
                buffer.append( ch );
            }
            else {
                count++;//
            }
        }
        return buffer.toString();
    }
}

