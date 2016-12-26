package lgcCompiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

//���Ǵʷ���������
public class Lexical {
	
    public int lineCount=0; //��ǰ�ļ���������
    private char curCh = ' '; // ��ǰ�ַ�
    private String line; //��ǰ�ַ�������
    public int lineLength = 0; //��ǰ�ַ������е��ַ���
    public int chCount = 0; //��ǰ�ַ�����λ��
    private int[] ssym;
    private BufferedReader in;
    
    
	
	public Lexical(String filePath) {
		try {
			in = new BufferedReader(new FileReader(filePath)) ;
		} catch (FileNotFoundException e) {
			System.out.println("���ļ����������ļ�·���Ƿ���ȷ");
			e.printStackTrace();
		}
	}
      // ��ȡһ���ַ�
	private void getCh(){
		if(chCount == lineLength ){
			try {
				String temp = " " ;
				while(temp == " "){
					temp = in.readLine().trim()+" " ;
					lineCount++ ;
				}
				line = temp ;
			} catch (Exception e) {
				System.out.println("��getCh()�ж�ȡ�ַ�����");
				e.printStackTrace();
			}
			lineLength = line.length() ;
			chCount = 0 ;
			System.out.println(line);
		}
		curCh = line.charAt(chCount++);
	}
	
	//��ȡһ������
	public Symbol getSymbol(){
		Symbol symbol ;
		while(curCh==' '){
			getCh();
		}
		if((curCh >='a' && curCh <= 'z')||(curCh >='A' && curCh <= 'Z') ){
			symbol = getKeywordOrIdentifier() ; 
		}
		else if(curCh>='0'&& curCh <= '9'){
			symbol = getNumber() ;
		}
		else{
			symbol = getOperator() ;
		}
		return symbol ;
	}

	private Symbol getKeywordOrIdentifier(){
		StringBuffer tem = new StringBuffer() ;
		while((curCh >='a' && curCh <= 'z')||(curCh >='A' && curCh <= 'Z')||(curCh>='0'&& curCh <= '9')){
			tem.append(curCh) ;
			getCh() ;
		}
		String token = tem.toString();
		int type = Arrays.binarySearch(Symbol.word, token) ;
		Symbol sym ;
		if(type < 0 ){ // ˵��Ϊ��ʶ��
			sym = new Symbol(Symbol._ident) ;
			sym.setId(token);
		}
		else{
			sym = new Symbol(type) ;
			sym.setId(token);
		}
		
		return sym;
	}
	private Symbol getNumber() {
		StringBuffer tem = new StringBuffer() ;
		while((curCh>='0'&& curCh <= '9')){
			tem.append(curCh) ;
			getCh() ;
		}
		String token = tem.toString();
		Symbol sym = new Symbol(Symbol._number) ;
		sym.setNum(Integer.parseInt(token));
		return sym;
	}
	private Symbol getOperator() {
		Symbol sym = null ;
		switch(curCh){
			case '+' :
				sym = new Symbol(Symbol._plus) ;
				getCh();
				break ;
			case '-' :
				sym = new Symbol(Symbol._minus) ;
				getCh();
				break ;
			case '*' :
				sym = new Symbol(Symbol.__mul) ;
				getCh();
				break ;
			case '/' :
				sym = new Symbol(Symbol._div) ;
				getCh();
				break ;
			case '=' :
					sym = new Symbol(Symbol._eql) ;
					getCh();
				break;
			case '!':
				getCh();
				if(curCh == '='){
					sym = new Symbol(Symbol._neq) ;
					getCh();
				}else{
					sym = new Symbol(Symbol._null) ;
					System.out.println("��⵽�Ƿ��ַ�");
				}
				break ;
			case '<':
				getCh() ;
				if(curCh == '='){
					sym = new Symbol(Symbol._leq) ;
					getCh();
				}else{
					sym = new Symbol(Symbol._less) ;
				}
				break ;
			case '>' :
				getCh();
				if(curCh == '='){
					sym = new Symbol(Symbol._geq);
					getCh();
				}
				else{
					sym = new Symbol(Symbol._gtr);
				}
				break;
			case '(' :
				sym = new Symbol(Symbol._rparen);
				getCh();
				break;
			case ')' :
				sym = new Symbol(Symbol._lparen);
				getCh();
				break ;
			case ':' :
				getCh();
				if(curCh == '='){
					sym = new Symbol(Symbol._become);
					getCh();
				}
				break;
			case ';' :
				sym = new Symbol(Symbol._semicolon);
				getCh();
				break ;
			case '.' :
				sym = new Symbol(Symbol._peroid);
				getCh() ;
				break ;
			case ',' :
				sym = new Symbol(Symbol._comma);
				getCh();
				break;
		}
		return sym ;
	}
	public static void main(String[] args) {

	}

}
