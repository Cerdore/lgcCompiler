package lgcCompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Pcode {

	
    public Pcode(int f, int l, int a) {
        this.f = f;
        this.l = l;
        this.a = a;
    }
    
	//Pcodeָ����������
	public static final int arrayCount = 500;
	
	public static Pcode[] arrayPcode = new Pcode[arrayCount];
    
    public static final int LIT = 0;
    public static final int OPR = 1;
    public static final int LOD = 2;
    public static final int STO = 3;
    public static final int CAL = 4;
    public static final int INT = 5;
    public static final int JMP = 6;
    public static final int JPC = 7;

    //�����ŵ�����
    public static final String[] pcode = new String[]{"LIT", "OPR", "LOD", "STO", "CAL", "INT", "JMP", "JPC"};
    //���������ָ��
    public int f;
    //���ò���������Ĳ�β�
    public int l;
    //ָ�����
    public int a;
    
    public static  void printPcode( int len ){
    	BufferedWriter bufferWritter = null ; //����д���ļ���
    	File file = new File("PcodeTable.txt");
	       try {
	    	  if(!file.exists())
	    		  file.createNewFile();
			
			//true = append file
	    	  FileWriter fileWritter = new FileWriter(file.getName());
 	          bufferWritter = new BufferedWriter(fileWritter);
 	          bufferWritter.write("f		l		a \r\n");
 				for(int i = 1 ; i < len ;i++ ){
 					bufferWritter.write(pcode[arrayPcode[i].f] + "		" + arrayPcode[i].l + "		" + arrayPcode[i].a+"\r\n" );
 		    	}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				bufferWritter.flush();
				bufferWritter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    }
    
    

}