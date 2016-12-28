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
    
	//Pcode指令的最大数量
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

    //各符号的名字
    public static final String[] pcode = new String[]{"LIT", "OPR", "LOD", "STO", "CAL", "INT", "JMP", "JPC"};
    //虚拟机代码指令
    public int f;
    //引用层与声明层的层次差
    public int l;
    //指令参数
    public int a;
    
    public static  void printPcode( int len ){
    	BufferedWriter bufferWritter = null ; //负责写入文件。
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