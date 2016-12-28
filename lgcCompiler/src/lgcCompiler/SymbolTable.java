package lgcCompiler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SymbolTable {
	
	BufferedWriter bufferWritter = null ; //����д���ļ���
	
	public int tableptr ; //��ǰ���ű����ָ�롣
    public static final int tableMax = 100;         //���ű�Ĵ�С
    public static final int symMax = 10;            //���ŵ���󳤶�
    public static final int addrMax = 1000000;      //����������ֵ
    public static final int levMax = 3;             //����������Ƕ����������[0,levmax]
    public static final int numMax = 14;            //number�����λ��
    public static boolean tableswitch = true;              //��ʾ���ֱ����
    public static final int constant = 0;           //����
    public static final int variable = 1;           //����
    public static final int procedure = 2;          //����
    
    public static Item[] table = new Item[tableMax] ;
    
    public class Item{
        String name;                                             //����
        int type;                                               //���ͣ�const var or procedur
        int value;                                                 //��ֵ��constʹ��
        int lev;                                                 //�����㣬var��procedurʹ��
        int addr;                                                //��ַ��var��procedurʹ��
        int size;                                               //��Ҫ������������ռ䣬��procedureʹ��
		public Item() {
			super();
			name = "";
		}
    }
    
    //��÷��ű�ĳһ��
    public Item getItem(int i){
    	if(table[i] == null ){
    		table[i] = new Item();
    	}
    	return table[i] ;
    }
    
    //������
    //@param sym Ҫ����ķ���
    //@param type ������ŵ�����
    //@param lev ������ŵĲ��
    //@param dx ������ŵ�ƫ����
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
    
    
    //��������Ϊname�ķ��ŴӺ���ǰ���ҡ�
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
     * String name;                                             //����
        int type;                                               //���ͣ�const var or procedur
        int value;                                                 //��ֵ��constʹ��
        int lev;                                                 //�����㣬var��procedurʹ��
        int addr;                                                //��ַ��var��procedurʹ��
        int size;                                               //��Ҫ������������ռ䣬��procedureʹ��
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
			System.out.println("��������ű�ʱ����");
			e1.printStackTrace();
		}finally{
			try {
				bufferWritter.flush();
				bufferWritter.close();
			} catch (IOException e) {
				System.out.println("�ڹر�BufferWriterʱ����");
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
