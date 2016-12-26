package lgcCompiler;

import java.util.BitSet;

import lgcCompiler.SymbolTable.Item;

public class Grammatical {
	private Symbol sym ; // 当前符号。
	private Lexical lexical ;//词法分析器。
	private SymbolTable table ;//符号表。
	private Error error ; //错误处理程序。
	
	//Pcode指令的指针
	private int arrayPtr = 1;
	
	private BitSet declFirSet ; //声明的FIRST集
	
	private BitSet statFirSet ; //语句的FIRST集
	
	private BitSet facFirSet ; //因子的FIRST集
	
	private int dx = 0 ;//当前作用域中相对于基指针的偏移量。
	
	public Grammatical(Lexical lexical,SymbolTable table){
		this.lexical = lexical ;
		this.table = table ;
		this.error = new Error() ;
		
		/*
		 * 初始化声明的FIRST集：
		 * <常量说明部分> ::= const<常量定义>{,<常量定义>};
		 * var<标识符>{,<标识符>};
		 * <过程说明部分> ::= <过程首部><分程序>{;<过程说明部分>};
		 * <过程首部> ::= procedure<标识符>;
		 * 所以FIRST集中有{const,var,produre，NULL}。
		 */
		declFirSet = new BitSet(Symbol.symnum) ;
		declFirSet.set(Symbol._const);
		declFirSet.set(Symbol._var);
		declFirSet.set(Symbol._procedure);
		
		/*
		 * 初始化语句的FIRST集：
		 * <赋值语句> ::= <标识符>:=<表达式>
		 * <条件语句> ::= if<条件>then<语句>[else<语句>]
		 * <当型循环语句> ::= while<条件>do<语句>
		 * <过程调用语句> ::= call<标识符>
		 * <复合语句> ::= begin<语句>{;<语句>}end
		 * <重复语句> ::= repeat<语句>{;<语句>}until<条件>
		 * <读语句> ::= read'('<标识符>{,<标识符>}')‘
		 * <写语句> ::= write'('<标识符>{,<标识符>}')‘
		 * 所以FIRST集中有{NULL,if,while,call,begin,repeat,read,write,ident}。
		 */
		statFirSet = new BitSet(Symbol.symnum);
		statFirSet.set(Symbol._if);
		statFirSet.set(Symbol._while);
		statFirSet.set(Symbol._call);
		statFirSet.set(Symbol._begin);
		statFirSet.set(Symbol._repeat);
		statFirSet.set(Symbol._read);
		statFirSet.set(Symbol._write);
		statFirSet.set(Symbol._ident);
		/*
		 * 初始化因子的FIRST集
		 * <因子> ::= <标识符>|<无符号整数>|'('<表达式>')‘
		 * <标识符> ::= <字母>{<字母>|<数字>}
		 * 所以因子的FIRST集为{number,ident,(}
		 */
		facFirSet = new BitSet(Symbol.symnum);
		facFirSet.set(Symbol._number);
		facFirSet.set(Symbol._ident);
		facFirSet.set(Symbol._lparen);
	}
	
	private void nextSymbol(){
		sym = lexical.getSymbol();
	}
	
	/*
	 * 测试当前字符是否合法，如果不合法则跳过该字符，直到合法或结束为止。
	 */
	private void testSymbol(BitSet s1 , BitSet s2 ,int errorCode ){
		if(!s1.get(sym.getSymtype())){
			error.report(errorCode, lexical.lineCount);
			//当当前字符不符合语法，就跳过它，并判断它下一个字符是否属于当前FIRST集或FOLLOW集。
			s1.or(s2);
			while(!s1.get(sym.getSymtype())){
				nextSymbol();
			}
		}
	}
	//分析程序
	public void program(){
		/*
		 * 首先建立程序的FIRST集
		 * 
		 * <程序> ::= <分程序>.
		 *  所以程序的FOLLOW集为{.}
		 */
		BitSet followSet = new BitSet(Symbol.symnum);
		followSet.set(Symbol._peroid);
		
		
		nextSymbol();
				
		//进入分程序处理		
		subProgram(0,followSet);
		
		if(sym.getSymtype()!=Symbol._peroid){
			error.report(9, lexical.lineCount);
		}
		System.out.println("输出符号表");
		table.printTable();
		
		System.out.println("输出Pcode码 ， len is : " + arrayPtr );
		Pcode.printPcode(arrayPtr);
	}
	/*
	 * 分析分程序
	 * @param lev 当前分程序所在层
	 * @param fbs 此模块的FOLLOW集合
	 */
	private void subProgram(int lev ,BitSet fbs) {
		BitSet nextlev = new BitSet(Symbol.symnum);
		int dx0 = dx ; //记录上一层的数据量。
		int start = table.tableptr ; //本层的开始位置。
		dx = 3 ; //置初始值为3,每一层最开始的位置有三个空间用于存放静态链SL、动态链DL和返回地址RA。
		
		if(lev > SymbolTable.levMax){
			error.report(32, lexical.lineCount);
			return ;
		}
			//如果是常量。
			if(sym.getSymtype()==Symbol._const){
				nextSymbol();
				constdecldeal(lev);
			}
			
			//如果是变量
			if(sym.getSymtype()==Symbol._var){
				nextSymbol();
				vardecldeal(lev);
			}
			
			/*
			 * 如果是过程
			 * <过程说明部分> ::= <过程首部><分程序>{;<过程说明部分>};
			 * <过程首部> ::= procedure<标识符>;
			 */
			while(sym.getSymtype() == Symbol._procedure){
				print("this is proceduce");
				nextSymbol();
				procapitaldeal(lev);
				//处理完过程首部，又将遇到分程序，此时分程序的FOLLOW集为{;}
				nextlev.set(Symbol._semicolon);
				subProgram(lev+1,nextlev);
				//如果读到；
				if(sym.getSymtype()==Symbol._semicolon){
					nextSymbol();
				}else{
					error.report(5, lexical.lineCount);
				}
				//则接下来可能遇到<过程说明部分>或者<语句>。nextlev =<过程说明部分> U {_procedure}
				nextlev = (BitSet) statFirSet.clone();
				nextlev.set(Symbol._procedure);
				//跳过不合法字符。直到遇到过程或者{.}
				testSymbol(nextlev, fbs, 6);
			}
		//跳过非语句部分直到遇到语句或者{.}
		nextlev = (BitSet) statFirSet.clone();
		testSymbol(nextlev, fbs, 9);
		//下面进行语句的分析部分。
		if(statFirSet.get(sym.getSymtype())){
			statementdeal(fbs,lev);
		}
	}
	
	
	/*
	 *语句处理程序
	 *<语句> ::= <赋值语句>|<条件语句>|<当型循环语句>|<过程调用语句>|<读语句>|<写语句>|<复合语句>|<重复语句>|<空>
	 *@param fbs 此模块的FOLLOW集  对于语句应为{.}
	 *@param lev 层次
	 */
	private void statementdeal(BitSet fbs, int lev) {
		
		switch(sym.getSymtype()){
		//<赋值语句> ::= <标识符>:=<表达式>
		case Symbol._ident :   
			assignStatement(fbs, lev);
		}
		
	}
	/*
	 * 赋值语句处理
	 *@param fbs 此模块的FOLLOW集  对于赋值语句应为{.}
	 *@param lev 层次
	 */
	private void assignStatement(BitSet fbs, int lev) {
		int index = table.searchSymbol(sym.getId());
		
		//index>0表示搜索到了这个标识符。
		if(index >0 ){
			SymbolTable.Item item = table.getItem(index);
			if(item.type == SymbolTable.variable){  //判断是否为变量
				nextSymbol();
				if(sym.getSymtype() == Symbol._become){
					nextSymbol();
				}else{
					error.report(13, lexical.lineCount);
				}
				BitSet nextlev = (BitSet) fbs.clone();
				expression(nextlev,lev);
				
				genPcode(Pcode.STO, lev-item.lev, item.addr);
				
			}else{
				error.report(12, lexical.lineCount);
			}
		}else{
			error.report(11, lexical.lineCount);
		}
	}
	
	/*
	 * 表达式处理
	 *@param fbs 此模块的FOLLOW集  对于表达式语句应为{.}
	 *@param lev 层次
	 */
    private void expression(BitSet fbs, int lev) {
        if (sym.getSymtype() == Symbol._plus || sym.getSymtype() == Symbol._minus) {                                 //分析[+|-]<项>
            int addOperatorType = sym.getSymtype();
            nextSymbol();
            //项的FOLLOW集为加法运算符
            BitSet nxtlev = (BitSet) fbs.clone();
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
            
            if (addOperatorType == Symbol._minus) //OPR 0 1:：NEG取反
            {
                genPcode(Pcode.OPR, 0, 1);
            }
            // 如果不是负号就是正号，不需生成相应的指令
        } else {
            BitSet nxtlev = (BitSet) fbs.clone();
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
        }

        //分析{<加法运算符><项>}
        while (sym.getSymtype() == Symbol._plus || sym.getSymtype() == Symbol._minus) {
            int addOperatorType = sym.getSymtype();
            nextSymbol();
            BitSet nxtlev = (BitSet) fbs.clone();
            //FOLLOW(term)={ +,- }
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
          //opr 0 2:执行加法,opr 0 3:执行减法
            if(addOperatorType == Symbol._plus)
            	genPcode(Pcode.OPR, 0, 2); 
            else
            	genPcode(Pcode.OPR, 0, 3); 
        }
    }

    /*
     * <项> ::= <因子>{<乘法运算符><因子>}
     *@param fbs 此模块的FOLLOW集  对于项应为{ . , + , - }
	 *@param lev 层次
	 */
    private void term(BitSet fbs, int lev) {
        //分析<因子>
        //一个因子后应当遇到乘号或除号
        BitSet nxtlev = (BitSet) fbs.clone();
        nxtlev.set(Symbol.__mul);
        nxtlev.set(Symbol._div);

        factor(nxtlev, lev);

        //分析{<乘法运算符><因子>}
        while (sym.getSymtype() == Symbol.__mul || sym.getSymtype() == Symbol._div) {
            int mulOperatorType = sym.getSymtype();
            nextSymbol();
            factor(nxtlev, lev);
            //乘法:OPR 0 4 ,除法:OPR 0 5
            if(mulOperatorType == Symbol.__mul)
            	genPcode(Pcode.OPR, 0,4);
            else
            	genPcode(Pcode.OPR, 0,5);
        }
    }

    /*
     * <因子> ::= <标识符>|<无符号整数>|'('<表达式>')‘
     *@param fbs 此模块的FOLLOW集  对于项应为{ . , + , - , * , /}
	 *@param lev 层次
	 */
	private void factor(BitSet fbs, int lev) {
		//跳过不是因子的字符  直到遇到因子或者因子的FOLLOW集
		testSymbol(facFirSet, fbs, 24);
		
		if(facFirSet.get(sym.getSymtype())){
			if(sym.getSymtype() == Symbol._ident){
				int index = table.searchSymbol(sym.getId());
				if(index > 0 ){
					Item item = table.getItem(index);
					
					switch(item.type){
						case SymbolTable.constant :
							//生成lit指令，把这个数值字面常量放到栈顶
							genPcode(Pcode.LIT, 0, item.value);
							break ;
						case SymbolTable.procedure :
							//表达式内不可有过程标识符
							error.report(21,lexical.lineCount);
                            break;
						case SymbolTable.variable :
							//把位于距离当前层level的层的偏移地址为adr的变量放到栈顶
                            genPcode(Pcode.LOD, lev - item.lev, item.addr);
                            break;
                        default :
                        	error.report(37, lexical.lineCount);
					}
				}else{
					error.report(11, lexical.lineCount);
				}
				nextSymbol();
			}else if(sym.getSymtype() == Symbol._number){
				int num = sym.getNum();
				if(num > SymbolTable.addrMax){
					num = 0 ;
					error.report(31, lexical.lineCount);
				}
				genPcode(Pcode.LIT, 0, num);
				nextSymbol();
			}else if(sym.getSymtype() == Symbol._lparen ){
				nextSymbol();
				BitSet nextlev = (BitSet) fbs.clone();
				nextlev.set(Symbol._rparen);
				expression(nextlev, lev);
				if(sym.getSymtype() == Symbol._rparen){
					nextSymbol();
				}else{
					error.report(22, lexical.lineCount);
				}
			}else{
				error.report(37, lexical.lineCount);
			}
		}else{
			error.report(24, lexical.lineCount);
		}
		
	}

	//<过程说明部分> ::= <过程首部><分程序>{;<过程说明部分>};
	//<过程首部> ::= procedure<标识符>;
//	private void procedeal(int lev) {
//		procapitaldeal(lev);
//	}
	private void procapitaldeal(int lev){
		if(sym.getSymtype()==Symbol._ident){
			table.enter(sym, SymbolTable.procedure, lev, dx);
			dx++;
			nextSymbol();
		}else{
			error.report(4, lexical.lineCount);
		}
		
		if(sym.getSymtype() == Symbol._semicolon){
			nextSymbol();
		}else{
			error.report(5, lexical.lineCount);
		}
	}

	//<变量说明部分>::= var<标识符>{,<标识符>};
	private void vardecldeal(int lev) {
		identdeal(lev);
		while(sym.getSymtype()==Symbol._comma){
			nextSymbol();
			identdeal(lev);
		}
		if(sym.getSymtype()==Symbol._semicolon){
			nextSymbol();
		}else{
			error.report(5, lexical.lineCount);
		}
	}
	
	//<标识符> ::= <字母>{<字母>|<数字>}
	private void identdeal(int lev){
		if(sym.getSymtype() == Symbol._ident){
			table.enter(sym, SymbolTable.variable, lev, dx);
			dx++;
			nextSymbol();
		}else{
			error.report(4, lexical.lineCount);
		}
	}

	//<常量说明部分> ::= const<常量定义>{,<常量定义>};
	private void constdecldeal(int lev){
		constdefideal(lev);                            //<常量定义>
		print(" Symtype is :"+ sym.getSymtype()+"");
        while (sym.getSymtype() == Symbol._comma) {
            nextSymbol();
            constdefideal(lev);
        }

        if (sym.getSymtype() == Symbol._semicolon) //如果是分号，表示常量申明结束
        {
        	nextSymbol();
        } else {
            error.report(5,lexical.lineCount);                                     //漏了逗号或者分号
        }
	}
	
	
	//<常量定义> ::= <标识符>=<无符号整数>
	private void constdefideal(int lev) {
		if(sym.getSymtype()==Symbol._ident){
			String id = sym.getId();
			System.out.println("sym id : " + id);
			nextSymbol();
			if(sym.getSymtype()==Symbol._become || sym.getSymtype() == Symbol._eql){
				if(sym.getSymtype()==Symbol._become ){
					error.report(1, lexical.lineCount);
				}
				nextSymbol();
				if(sym.getSymtype() == Symbol._number){
					sym.setId(id);
					table.enter(sym, SymbolTable.constant, lev, dx);
					dx++;
					nextSymbol();
				}else{
					error.report(2, lexical.lineCount);
				}
			}else{
				error.report(3, lexical.lineCount);
			}
		}else{
			error.report(4, lexical.lineCount);
		}
	}
	
	
	private void genPcode(int f ,int l , int a ){
		if(arrayPtr > Pcode.arrayCount){
			error.report(36, lexical.lineCount);
		}else{
			Pcode.arrayPcode[arrayPtr] = new Pcode(f,l,a);
		}
	}
	
	
	
	public void testreadconst(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._const){
			nextSymbol();
			constdecldeal(1);
			System.out.println("输出符号表");
			table.printTable();
			return ;
		}
	}
	
	public void testreadvar(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._var){
			nextSymbol();
			vardecldeal(0);
			System.out.println("输出符号表");
			table.printTable();
			return ;
		}
	}
	
	
	public void testreadpro(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._var){
			nextSymbol();
			vardecldeal(0);
			System.out.println("输出符号表");
			table.printTable();
			return ;
		}
	}
	
	private void print(String s){
		System.out.println(s);
	}
	
}
