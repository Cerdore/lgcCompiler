package lgcCompiler;
//这是符号类，用了存储编译器的保留字等
public class Symbol {

	//各类符号码
	
	////////////////////以下保留字 按字母顺序排列////////////////////////////////////////////////
    public static final int _begin = 0;                  
    public static final int _call = 1;               
    public static final int _const = 2;                
    public static final int _do = 3;              
    public static final int _else = 4;              
    public static final int _end = 5;                 
    public static final int _if = 6;          
    public static final int _odd = 7;          
    public static final int _procedure = 8;                 
    public static final int _read = 9;                
    public static final int _repeat = 10;                 
    public static final int _then = 11;                 
    public static final int _until = 12;  
    public static final int _var = 13;                
    public static final int _while = 14;                
    public static final int _write = 15;            
    ////////////////////以上为保留字/////////////////////////////////////////////////////
    
    //////////////////////以下为运算符//////////////////////////////
    public static final int _null = 16;       //NULL
    public static final int _plus = 17;       //+
    public static final int _minus = 18;      //-
    public static final int __mul = 19;       //*
    public static final int _div = 20;        // /
    public static final int _eql = 21;        //=
    public static final int _neq = 22;        //!=
    public static final int _less = 23;       //<
    public static final int _leq = 24;        //<=
    public static final int _gtr = 25;        //>
    public static final int _geq = 26;        //>=
    public static final int _lparen =27;      //(
    public static final int _rparen = 28;     //)
    public static final int _comma =29;       //:
    public static final int _semicolon = 30;  //;
    public static final int _peroid = 31;     //.
    public static final int _become=32;       //:=
///////////////////////////以上为运算符///////////////////////////////////////////   
    
    public static final int _number = 33;     //数字
    public static final int _ident = 34;      //标识符
    
    public static final int symnum = 35;      //符号码的个数 
    
    
    public static final String[] word = new String[]{
            "begin","call" , "const"    , "do" ,
            "else"  ,"end" ,"if"   , "odd", 
            "procedure", "read","repeat","then",
            "until" , "var", "while"    , "write" };
    
    private int symtype;       //符号码

    private String id;         //标志符号名字；
    
    private int num;           //数值的大小
    
    
    

    /**
     * 构造具有特定符号码的符号
     *
     * @param stype
     */
    public Symbol(int stype) {
        symtype = stype;
        id = "";
        num = 0;
    }




	public int getSymtype() {
		return symtype;
	}




	public void setSymtype(int symtype) {
		this.symtype = symtype;
	}




	public String getId() {
		return id;
	}




	public void setId(String id) {
		this.id = id;
	}




	public int getNum() {
		return num;
	}




	public void setNum(int num) {
		this.num = num;
	}
    
    
    
    
    
    
    
}
