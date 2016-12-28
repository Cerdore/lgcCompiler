package lgcCompiler;

import java.util.BitSet;

import lgcCompiler.SymbolTable.Item;

public class Grammatical {
	private Symbol sym ; // ��ǰ���š�
	private Lexical lexical ;//�ʷ���������
	private SymbolTable table ;//���ű�
	private Error error ; //���������
	
	//Pcodeָ���ָ��
	private int arrayPtr = 1;
	
	private BitSet declFirSet ; //������FIRST��
	
	private BitSet statFirSet ; //����FIRST��
	
	private BitSet facFirSet ; //���ӵ�FIRST��
	
	private int dx = 0 ;//��ǰ������������ڻ�ָ���ƫ������
	
	public Grammatical(Lexical lexical,SymbolTable table){
		this.lexical = lexical ;
		this.table = table ;
		this.error = new Error() ;
		
		/*
		 * ��ʼ��������FIRST����
		 * <����˵������> ::= const<��������>{,<��������>};
		 * var<��ʶ��>{,<��ʶ��>};
		 * <����˵������> ::= <�����ײ�><�ֳ���>{;<����˵������>};
		 * <�����ײ�> ::= procedure<��ʶ��>;
		 * ����FIRST������{const,var,produre��NULL}��
		 */
		declFirSet = new BitSet(Symbol.symnum) ;
		declFirSet.set(Symbol._const);
		declFirSet.set(Symbol._var);
		declFirSet.set(Symbol._procedure);
		
		/*
		 * ��ʼ������FIRST����
		 * <��ֵ���> ::= <��ʶ��>:=<���ʽ>
		 * <�������> ::= if<����>then<���>[else<���>]
		 * <����ѭ�����> ::= while<����>do<���>
		 * <���̵������> ::= call<��ʶ��>
		 * <�������> ::= begin<���>{;<���>}end
		 * <�ظ����> ::= repeat<���>{;<���>}until<����>
		 * <�����> ::= read'('<��ʶ��>{,<��ʶ��>}')��
		 * <д���> ::= write'('<��ʶ��>{,<��ʶ��>}')��
		 * ����FIRST������{NULL,if,while,call,begin,repeat,read,write,ident}��
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
		 * ��ʼ�����ӵ�FIRST��
		 * <����> ::= <��ʶ��>|<�޷�������>|'('<���ʽ>')��
		 * <��ʶ��> ::= <��ĸ>{<��ĸ>|<����>}
		 * �������ӵ�FIRST��Ϊ{number,ident,(}
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
	 * ���Ե�ǰ�ַ��Ƿ�Ϸ���������Ϸ����������ַ���ֱ���Ϸ������Ϊֹ��
	 */
	private void testSymbol(BitSet s1 , BitSet s2 ,int errorCode ){
		if(!s1.get(sym.getSymtype())){
			error.report(errorCode, lexical.lineCount);
			//����ǰ�ַ��������﷨���������������ж�����һ���ַ��Ƿ����ڵ�ǰFIRST����FOLLOW����
			s1.or(s2);
			while(!s1.get(sym.getSymtype())){
				nextSymbol();
			}
		}
	}
	//��������
	public void program(){
		/*
		 * ���Ƚ��������FIRST��
		 * 
		 * <����> ::= <�ֳ���>.
		 *  ���Գ����FOLLOW��Ϊ{.}
		 */
		BitSet followSet = new BitSet(Symbol.symnum);
		followSet.set(Symbol._peroid);
		
		
		nextSymbol();
				
		//����ֳ�����		
		subProgram(0,followSet);
		
		if(sym.getSymtype()!=Symbol._peroid){
			error.report(9, lexical.lineCount);
		}
		System.out.println("������ű�");
		table.printTable();
		
		
		
		System.out.println("���Pcode�� �� len is : " + arrayPtr );
		Pcode.printPcode(arrayPtr);
		
	}
	/*
	 * �����ֳ���
	 * @param lev ��ǰ�ֳ������ڲ�
	 * @param fbs ��ģ���FOLLOW����
	 */
	private void subProgram(int lev ,BitSet fbs) {
		BitSet nextlev = new BitSet(Symbol.symnum);
		int dx0 = dx ; //��¼��һ�����������
		int start = table.tableptr ; //����Ŀ�ʼλ�á�
		dx = 3 ; //�ó�ʼֵΪ3,ÿһ���ʼ��λ���������ռ����ڴ�ž�̬��SL����̬��DL�ͷ��ص�ַRA��
		
		if(lev > SymbolTable.levMax){
			error.report(32, lexical.lineCount);
			return ;
		}
		
		int flag = 0 ;
//		do{
			//����ǳ�����
			if(sym.getSymtype()==Symbol._const){
				nextSymbol();
				constdecldeal(lev);
			}
				
				//����Ǳ���
				if(sym.getSymtype()==Symbol._var){
					nextSymbol();
					vardecldeal(lev);
				}
				
				
				/*
				 * ����ǹ���
				 * <����˵������> ::= <�����ײ�><�ֳ���>{;<����˵������>};
				 * <�����ײ�> ::= procedure<��ʶ��>;
				 */
				while(sym.getSymtype() == Symbol._procedure){
					print("this is proceduce");
					nextSymbol();
					procapitaldeal(lev);
					//����������ײ����ֽ������ֳ��򣬴�ʱ�ֳ����FOLLOW��Ϊ{;}
					nextlev.set(Symbol._semicolon);
					
					print("����Ҫ����ֳ���");
					subProgram(lev+1,nextlev);
					//���������
					if(sym.getSymtype()==Symbol._semicolon){
						nextSymbol();
					}else{
						error.report(5, lexical.lineCount);
					}
				}
//		}while(declFirSet.get(sym.getSymtype()));
	
		//��������䲿��ֱ������������{.}
				
				
		//����������ķ������֡�
		if(statFirSet.get(sym.getSymtype())){
			statementdeal(fbs,lev);
		}
		
		dx =dx0;
	}
	
	
	/*
	 *��䴦�����
	 *<���> ::= <��ֵ���>|<�������>|<����ѭ�����>|<���̵������>|<�����>|<д���>|<�������>|<�ظ����>|<��>
	 *@param fbs ��ģ���FOLLOW��  �������ӦΪ{.}
	 *@param lev ���
	 */
	private void statementdeal(BitSet fbs, int lev) {
		
		switch(sym.getSymtype()){
		//<��ֵ���> ::= <��ʶ��>:=<���ʽ>
		case Symbol._ident :   
			assignStatement(fbs, lev);
			break;
		case Symbol._if :
			ifStatment(fbs,lev) ;
			break ;
		case Symbol._while :
			whileStatment(fbs,lev);
			break;
		case Symbol._call :
			callStatment(fbs,lev) ;
			break;
		case Symbol._read :
			readStatment(fbs,lev);
			break;
		case Symbol._write :
			writeStatment(fbs,lev);
			break;
		case Symbol._begin :
			beginStatment(fbs,lev);
			break;
		case Symbol._repeat :
			repeatStatment(fbs,lev);
			break;
		default:
            BitSet nextlev = new BitSet(Symbol.symnum);
            testSymbol(fbs, nextlev, 19);                                    //����ķ��Ų���ȷ
            break;
		}
		
	}
	//<�ظ����> ::= repeat<���>{;<���>}until<����>
	private void repeatStatment(BitSet fbs, int lev) {
		int cx1 =arrayPtr;
		nextSymbol();
		BitSet nextlev = (BitSet) fbs.clone();
		nextlev.set(Symbol._semicolon);
		nextlev.set(Symbol._until);
		statementdeal(nextlev, lev);
		while(statFirSet.get(sym.getSymtype())||sym.getSymtype() == Symbol._semicolon){
			if(sym.getSymtype()==Symbol._semicolon ){
				nextSymbol();
			}else{
				error.report(34, lexical.lineCount);
			}
			statementdeal(nextlev, lev);
		}
		nextlev = new BitSet();
		nextlev.set(Symbol._until);
		testSymbol(nextlev, fbs, 38);
		if(sym.getSymtype() == Symbol._until){
			nextSymbol();
			conditionStatment(fbs, lev);
			genPcode(Pcode.JPC, 0, cx1);
		}else{
			error.report(38, lexical.lineCount);
		}
		
	}

	//<�������> ::= begin<���>{;<���>}end
	private void beginStatment(BitSet fbs, int lev) {
		nextSymbol();
		BitSet nextlev = (BitSet) fbs.clone();
		nextlev.set(Symbol._semicolon);
		nextlev.set(Symbol._end);
		statementdeal(nextlev, lev);
		//����{;<���>}���Һ��Ե�����д�������
		while(statFirSet.get(sym.getSymtype())|| sym.getSymtype() == Symbol._semicolon){
			if(sym.getSymtype() == Symbol._semicolon){
				nextSymbol();
			}else{
				error.report(10, lexical.lineCount);
			}
			statementdeal(nextlev, lev);
		}
		if(sym.getSymtype() == Symbol._end){
			nextSymbol();
		}else{
			error.report(17, lexical.lineCount);
		}
	}

	//<д���> ::= write'('<��ʶ��>{,<��ʶ��>}')��
	private void writeStatment(BitSet fbs, int lev) {
		nextSymbol();
		if(sym.getSymtype() == Symbol._lparen){
			nextSymbol();
			int index = -1 ;
			do{
				if(sym.getSymtype() == Symbol._ident){
					index = table.searchSymbol(sym.getId(),lev);
				}else{
					error.report(14, lexical.lineCount);
				}
				
				if(index > 0 ){
					Item item = table.getItem(index);
					
					if(item.type == SymbolTable.variable){
						genPcode(Pcode.LOD, lev - item.lev, item.addr); // ����������ջ��
						genPcode(Pcode.OPR,0,14); // ���ջ����ֵ��
					}else if(item.type == SymbolTable.constant){
						genPcode(Pcode.LOD, 0 , item.value); // ����������ջ��
						genPcode(Pcode.OPR,0,14); // ���ջ����ֵ��
					}else{
						error.report(11, lexical.lineCount);
					}
					
				}else{
					error.report(11, lexical.lineCount);
				}
				nextSymbol();
			}while(sym.getSymtype() == Symbol._comma);
			
			if(sym.getSymtype() == Symbol._rparen){
				nextSymbol();
			}else{
				error.report(33,lexical.lineCount);
			}
			
		}else{
			error.report(34, lexical.lineCount);
		}
		genPcode(Pcode.OPR,0,15);
	}

	//<�����> ::= read'('<��ʶ��>{,<��ʶ��>}')��
	private void readStatment(BitSet fbs, int lev) {
		nextSymbol();
		if(sym.getSymtype() == Symbol._lparen){
			int index = -1 ;
			do{
				nextSymbol();
				if(sym.getSymtype() == Symbol._ident){
					index = table.searchSymbol(sym.getId(),lev);
				}else{
					error.report(14, lexical.lineCount);
				}
				if(index > 0 ){
					Item item = table.getItem(index);
					if(item.type == SymbolTable.variable){
						genPcode(Pcode.OPR, 0, 16);
						genPcode(Pcode.STO,lev - item.lev, item.addr);
					}else{
						error.report(38, lexical.lineCount);
					}
				}else{
					error.report(11, lexical.lineCount);
				}
				nextSymbol();
			}while(sym.getSymtype() == Symbol._comma) ;
		}else{
			error.report(34, lexical.lineCount);
		}
		if(sym.getSymtype() == Symbol._rparen){
			nextSymbol();
		}else{
			error.report(33, lexical.lineCount);
			BitSet nextlev = new BitSet();
			nextlev.set(Symbol._rparen);
			testSymbol(nextlev, fbs, 33);
		}
	}

	//<���̵������> ::= call<��ʶ��>
	private void callStatment(BitSet fbs, int lev) {
		nextSymbol();
		if(sym.getSymtype() == Symbol._ident){
			int index = table.searchSymbol(sym.getId(),lev);
			if(index > 0){
				Item item = table.getItem(index);
				if(item.type == SymbolTable.procedure){
					genPcode(Pcode.CAL, lev - item.lev, item.addr);
				}else{
					error.report(15, lexical.lineCount);
				}
			}else{
				error.report(11, lexical.lineCount);
			}
			nextSymbol();
		}else{
			error.report(14, lexical.lineCount);
		}
		
	}

	//<����ѭ�����> ::= while<����>do<���>
	private void whileStatment(BitSet fbs, int lev) {
		int cx1 = arrayPtr ;
		BitSet nextlev = (BitSet) fbs.clone();
		nextlev.set(Symbol._do);
		nextSymbol();
		conditionStatment(nextlev, lev);
		int cx2 = arrayPtr ;
		genPcode(Pcode.JPC,0,0);
		if(sym.getSymtype() == Symbol._do){
			nextSymbol();
		}else{
			error.report(18, lexical.lineCount);
		}
		statementdeal(fbs, lev);
		genPcode(Pcode.JMP, 0, cx1);
		Pcode.arrayPcode[cx2].a = arrayPtr;
		
	}

	/*
	 * <�������> ::= if<����>then<���>[else<���>]
	 */
	private void ifStatment(BitSet fbs, int lev) {
		nextSymbol();
		//<����>��FOLLOW��
		BitSet nextlev = (BitSet) fbs.clone();
		nextlev.set(Symbol._then);
		conditionStatment(nextlev,lev);
		if(sym.getSymtype() == Symbol._then){
			nextSymbol();
		}else{
			error.report(16, lexical.lineCount);
		}
		int cx1 = arrayPtr ;
		genPcode(Pcode.JPC, 0, 0); // ������תָ���ʱ��Ϊ0
		
		nextlev = (BitSet) fbs.clone();
		nextlev.set(Symbol._else);
		statementdeal(nextlev, lev);
		Pcode.arrayPcode[cx1].a = arrayPtr ;
		if(sym.getSymtype() == Symbol._else){
			Pcode.arrayPcode[cx1].a++;
			nextSymbol();
			int temptr = arrayPtr ;
			genPcode(Pcode.JMP, 0, 0);
			statementdeal(fbs, lev);
			Pcode.arrayPcode[cx1].a = temptr ;
		}
	}
	/*
	 * <����> ::= <���ʽ><��ϵ�����><���ʽ>|odd<���ʽ>
	 * <��ϵ�����> ::= =|<>|<|<=|>|>=
	 * <�������> ::= if<����>then<���>[else<���>]
	 */
	private void conditionStatment(BitSet fbs, int lev) {
		if(sym.getSymtype() == Symbol._odd){
			expression(fbs, lev);
			genPcode(Pcode.OPR, 0, 6);
		}else{
			BitSet nextlev = (BitSet) fbs.clone();
			nextlev.set(Symbol._eql);
			nextlev.set(Symbol._neq);
			nextlev.set(Symbol._less);
			nextlev.set(Symbol._leq);
			nextlev.set(Symbol._gtr);
			nextlev.set(Symbol._geq);
			expression(nextlev, lev);
			if(sym.getSymtype() == Symbol._eql||sym.getSymtype() == Symbol._neq||
					sym.getSymtype() == Symbol._less || sym.getSymtype() == Symbol._leq ||
					sym.getSymtype() == Symbol._gtr||sym.getSymtype() == Symbol._geq){
				int type = sym.getSymtype();
				nextSymbol();
				expression(fbs, lev);
				genPcode(Pcode.OPR, 0, type);
			}else{
				error.report(20, lexical.lineCount);
			}
		}
	}

	/*
	 * ��ֵ��䴦��
	 *@param fbs ��ģ���FOLLOW��  ���ڸ�ֵ���ӦΪ{.}
	 *@param lev ���
	 */
	private void assignStatement(BitSet fbs, int lev) {
		int index = table.searchSymbol(sym.getId(),lev);
		
		//index>0��ʾ�������������ʶ����
		if(index >0 ){
			SymbolTable.Item item = table.getItem(index);
			if(item.type == SymbolTable.variable){  //�ж��Ƿ�Ϊ����
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
	 * ���ʽ����
	 *@param fbs ��ģ���FOLLOW��  ���ڱ��ʽ���ӦΪ{.}
	 *@param lev ���
	 */
    private void expression(BitSet fbs, int lev) {
        if (sym.getSymtype() == Symbol._plus || sym.getSymtype() == Symbol._minus) {                                 //����[+|-]<��>
            int addOperatorType = sym.getSymtype();
            nextSymbol();
            //���FOLLOW��Ϊ�ӷ������
            BitSet nxtlev = (BitSet) fbs.clone();
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
            
            if (addOperatorType == Symbol._minus) //OPR 0 1:��NEGȡ��
            {
                genPcode(Pcode.OPR, 0, 1);
            }
            // ������Ǹ��ž������ţ�����������Ӧ��ָ��
        } else {
            BitSet nxtlev = (BitSet) fbs.clone();
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
        }

        //����{<�ӷ������><��>}
        while (sym.getSymtype() == Symbol._plus || sym.getSymtype() == Symbol._minus) {
            int addOperatorType = sym.getSymtype();
            nextSymbol();
            BitSet nxtlev = (BitSet) fbs.clone();
            //FOLLOW(term)={ +,- }
            nxtlev.set(Symbol._plus);
            nxtlev.set(Symbol._minus);
            term(nxtlev, lev);
          //opr 0 2:ִ�мӷ�,opr 0 3:ִ�м���
            if(addOperatorType == Symbol._plus)
            	genPcode(Pcode.OPR, 0, 2); 
            else
            	genPcode(Pcode.OPR, 0, 3); 
        }
    }

    /*
     * <��> ::= <����>{<�˷������><����>}
     *@param fbs ��ģ���FOLLOW��  ������ӦΪ{ . , + , - }
	 *@param lev ���
	 */
    private void term(BitSet fbs, int lev) {
        //����<����>
        //һ�����Ӻ�Ӧ�������˺Ż����
        BitSet nxtlev = (BitSet) fbs.clone();
        nxtlev.set(Symbol.__mul);
        nxtlev.set(Symbol._div);

        factor(nxtlev, lev);

        //����{<�˷������><����>}
        while (sym.getSymtype() == Symbol.__mul || sym.getSymtype() == Symbol._div) {
            int mulOperatorType = sym.getSymtype();
            nextSymbol();
            factor(nxtlev, lev);
            //�˷�:OPR 0 4 ,����:OPR 0 5
            if(mulOperatorType == Symbol.__mul)
            	genPcode(Pcode.OPR, 0,4);
            else
            	genPcode(Pcode.OPR, 0,5);
        }
    }

    /*
     * <����> ::= <��ʶ��>|<�޷�������>|'('<���ʽ>')��
     *@param fbs ��ģ���FOLLOW��  ������ӦΪ{ . , + , - , * , /}
	 *@param lev ���
	 */
	private void factor(BitSet fbs, int lev) {
		//�����������ӵ��ַ�  ֱ���������ӻ������ӵ�FOLLOW��
		testSymbol(facFirSet, fbs, 24);
		
		if(facFirSet.get(sym.getSymtype())){
			if(sym.getSymtype() == Symbol._ident){
				int index = table.searchSymbol(sym.getId(),lev);
				if(index > 0 ){
					Item item = table.getItem(index);
					
					switch(item.type){
						case SymbolTable.constant :
							//����litָ��������ֵ���泣���ŵ�ջ��
							genPcode(Pcode.LIT, 0, item.value);
							break ;
						case SymbolTable.procedure :
							//���ʽ�ڲ����й��̱�ʶ��
							error.report(21,lexical.lineCount);
                            break;
						case SymbolTable.variable :
							//��λ�ھ��뵱ǰ��level�Ĳ��ƫ�Ƶ�ַΪadr�ı����ŵ�ջ��
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

	//<����˵������> ::= <�����ײ�><�ֳ���>{;<����˵������>};
	//<�����ײ�> ::= procedure<��ʶ��>;
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

	//<����˵������>::= var<��ʶ��>{,<��ʶ��>};
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
	
	//<��ʶ��> ::= <��ĸ>{<��ĸ>|<����>}
	private void identdeal(int lev){
		if(sym.getSymtype() == Symbol._ident){
			table.enter(sym, SymbolTable.variable, lev, dx);
			dx++;
			nextSymbol();
		}else{
			error.report(4, lexical.lineCount);
		}
	}

	//<����˵������> ::= const<��������>{,<��������>};
	private void constdecldeal(int lev){
		constdefideal(lev);                            //<��������>
		print(" Symtype is :"+ sym.getSymtype()+"");
        while (sym.getSymtype() == Symbol._comma) {
            nextSymbol();
            constdefideal(lev);
        }

        if (sym.getSymtype() == Symbol._semicolon) //����Ƿֺţ���ʾ������������
        {
        	nextSymbol();
        } else {
            error.report(5,lexical.lineCount);                                     //©�˶��Ż��߷ֺ�
        }
	}
	
	
	//<��������> ::= <��ʶ��>=<�޷�������>
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
			Pcode.arrayPcode[arrayPtr++] = new Pcode(f,l,a);
		}
	}
	
	
	
	public void testreadconst(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._const){
			nextSymbol();
			constdecldeal(1);
			System.out.println("������ű�");
			table.printTable();
			return ;
		}
	}
	
	public void testreadvar(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._var){
			nextSymbol();
			vardecldeal(0);
			System.out.println("������ű�");
			table.printTable();
			return ;
		}
	}
	
	
	public void testreadpro(){
		nextSymbol();
		if(sym.getSymtype()==Symbol._var){
			nextSymbol();
			vardecldeal(0);
			System.out.println("������ű�");
			table.printTable();
			return ;
		}
	}
	
	private void print(String s){
		System.out.println(s);
	}
	
}
