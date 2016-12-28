package lgcCompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Error {
	
	BufferedWriter bufferedWriter = null ;
	
    public  int errCount = 0;
    public static final String[] errInfo = new String[]{
        "",
        "1.Ӧ��=������:=",
        "2.=��ӦΪ��",
        "3.��ʶ����ӦΪ=",
        "4.const,var,procedure ��ӦΪ��ʶ��",
        "5.©�����Ż�ֺ�",
        "6.����˵����ķ��Ų���ȷ",
        "7.ӦΪ���",
        "8.������������ķ��Ų���ȷ",
        "9.ӦΪ���",
        "10.���֮��©�ֺ�",
        "11.��ʶ��δ˵��",
        "12.�����������������ֵ",
        "13.ӦΪ��ֵ�����:=",
        "14.ӦΪ��ʶ��",
        "15.���ɵ��ó��������",
        "16.ӦΪthen",
        "17.ӦΪ�ֺŻ�end",
        "18.ӦΪdo",
        "19.����ķ��Ų���ȷ",
        "20.ӦΪ��ϵ�����",
        "21.���ʽ�ڲ����й��̱�ʶ��",
        "22.©������",
        "23.���Ӻ󲻿�Ϊ�˷���",
        "24.���ʽ�����Դ˷��ſ�ʼ",
        "25.�����̫��",
        "26.Not Defined Yet",
        "27.Not Defined Yet",
        "28.Not Defined Yet",
        "29.Not Defined Yet",
        "30.Not Defined Yet",    
        "31.��Խ��",
        "32.Ƕ�ײ�������",
        "33.��ʽ����ӦΪ������",
        "34.��ʽ����ӦΪ������",
        "35.����δ����",
        "36.����̫����",
        "37.δ֪����",
        "38.ȱ��until",
    };

    /**
     * ��ӡ������Ϣ
     * @param errcode 
     */
    public  void report(int errcode,int line) {
    	creatFileStream();
    	
        try {
            bufferedWriter.write("#### line( "+line+" ):" + errInfo[errcode] + "  #####");
            errCount++;
        }catch(Exception e){
            System.out.println("#####�ڴ�����ʱ������####");
        }finally{
			try {
				bufferedWriter.flush();
				bufferedWriter.close();
			} catch (IOException e) {
				System.out.println("#####�رմ����ļ�ʱ������####");
			}
		}

    }
    
    private void creatFileStream(){
    	File file = new File("ErrorTable.txt");
    	       try {
    	    	  if(!file.exists())
				file.createNewFile();
				
				//true = append file
	    	      FileWriter fileWritter = new FileWriter(file.getName());
	    	      bufferedWriter = new BufferedWriter(fileWritter);
				
			} catch (IOException e) {
				System.out.println("#####�򿪴����ļ�ʱ������####");
			}
    }
}
