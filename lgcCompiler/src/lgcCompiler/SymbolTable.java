package lgcCompiler;

public class SymbolTable {
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
    public int searchSymbol(String name){
    	int i = tableptr ;
    	while(i>0){
    		if(table[i].name.equals(name)){
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
    	if(!tableswitch){
    		return ;
    	}
    	int i = 1 ;
    	int type ;
    	Item item = null  ;
    	while(i<=tableptr){
    		try{
    			item = table[i];
        		type = item.type ;
        		switch(type){
        		case constant :
        			System.out.println("name : " + item.name + "  type : constant   value : " + item.value );
        			break;
        		case variable :
        			System.out.println("name : " + item.name + "  type : variable   lev : " + item.lev 
        					+"   addr : "+item.addr );
        			break;
        		case procedure :
        			System.out.println("name : " + item.name + "  type : procedure   lev : " + item.lev 
        					+"   addr : "+item.addr + "   size : "+ item.size);
        			break;
        		}
        		i++;
    		}catch(Exception e){
    			System.out.println("��ʾ���ű����");
    			return;
    		}
    	}
    }
}
