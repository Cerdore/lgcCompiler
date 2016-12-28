package lgcCompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTable {
	
	BufferedWriter bufferWritter = null ; //负责写入文件。
	
	public int tableptr ; //当前符号表的项指针。
    public static final int tableMax = 100;         //符号表的大小
    public static final int symMax = 10;            //符号的最大长度
    public static final int addrMax = 1000000;      //最大允许的数值
    public static final int levMax = 3;             //最大允许过程嵌套声明层数[0,levmax]
    public static final int numMax = 14;            //number的最大位数
    public static boolean tableswitch = true;              //显示名字表与否
    public static final int constant = 0;           //常量
    public static final int variable = 1;           //变量
    public static final int procedure = 2;          //过程
    
    public static Item[] table = new Item[tableMax] ;
    
    public class Item{
        String name;                                             //名字
        int type;                                               //类型，const var or procedur
        int value;                                                 //数值，const使用
        int lev;                                                 //所处层，var和procedur使用
        int addr;                                                //地址，var和procedur使用
        int size;                                               //需要分配的数据区空间，仅procedure使用
		public Item() {
			super();
			name = "";
		}
    }
    
    //获得符号表某一项
    public Item getItem(int i){
    	if(table[i] == null ){
    		table[i] = new Item();
    	}
    	return table[i] ;
    }
    
    //填表操作
    //@param sym 要填入的符号
    //@param type 填入符号的类型
    //@param lev 填入符号的层次
    //@param dx 填入符号的偏移量
    public void enter(Symbol sym, int type, int lev, int dx) {
    	tableptr++ ;
    	Item item = getItem(tableptr);
    	item.name = sym.getId() ;
    	item.type = type ;
    	switch(type){
    	case constant :
    		item.value = sym.getNum();
    		item.lev = lev ;
    		break;
    	case variable :
    		item.lev = lev ;
    		item.addr = dx ;
    		break;
    	case procedure :
    		item.lev = lev ;
    		item.addr = dx;
    		break;
    	}
    }
    
    
    //查找名字为name的符号从后往前查找。
    public int searchSymbol(String name ,int lev ){
    	int i = tableptr ;
    	while(i>0){
    		if(table[i].name.equals(name) && table[i].lev <= lev){
    			return i ;
    		}
    		i--;
    	}
    	return 0 ;
    }
    
    /*
     * String name;                                             //名字
        int type;                                               //类型，const var or procedur
        int value;                                                 //数值，const使用
        int lev;                                                 //所处层，var和procedur使用
        int addr;                                                //地址，var和procedur使用
        int size;                                               //需要分配的数据区空间，仅procedure使用
     */
    
    public void printTable(){
    	creatFileStream();
    	if(!tableswitch){
    		return ;
    	}
    	int i = 1 ;
    	int type ;
    	Item item = null  ;
    	char[] name = new char[10] ;
    	try {
			bufferWritter.write("name \n     type      value     lev       addr      size\r\n");
			while(i<=tableptr){
	    			item = table[i];
	        		type = item.type ;		
	        		switch(type){
	        		case constant :
	        			printName(item.name);
	        			bufferWritter.write("constant  " + item.value +"\r\n" );
	        			break;
	        		case variable :
	        			printName(item.name);
	        			bufferWritter.write("variable            " + item.lev 
	        					+"          "+item.addr +"\r\n");
	        			break;
	        		case procedure :
	        			printName(item.name);
	        			bufferWritter.write("procedure           " + item.lev 
	        					+"          "+item.addr + "         "+ item.size+"\r\n");
	        			break;
	        		}
	        		i++;
	    	}
		} catch (Exception e1) {
			System.out.println("在输出符号表时出错");
			e1.printStackTrace();
		}finally{
			try {
				bufferWritter.flush();
				bufferWritter.close();
			} catch (IOException e) {
				System.out.println("在关闭BufferWriter时出错");
				e.printStackTrace();
			}
		}
    }
    
    
    private void creatFileStream(){
    	File file = new File("SymbolTable.txt");
    	       try {
    	    	  if(!file.exists())
				file.createNewFile();
				
				//true = append file
	    	      FileWriter fileWritter = new FileWriter(file.getName());
	    	            bufferWritter = new BufferedWriter(fileWritter);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    }
    
    
    private void printName(String s) throws IOException{
    	int len =s.length() ;
    	int n = 10 -len;
    	bufferWritter.write( s);
    	for(int i=0 ;i< n ;i++ ){
    		bufferWritter.write(" ");
    	}
    }
}
