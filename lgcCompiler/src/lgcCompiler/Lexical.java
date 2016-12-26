package lgcCompiler;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

//这是词法分析程序
public class Lexical {
	
    public int lineCount=0; //当前文件的总行数
    private char curCh = ' '; // 当前字符
    private String line; //当前字符所在行
    public int lineLength = 0; //当前字符所在行的字符数
    public int chCount = 0; //当前字符所在位置
    private int[] ssym;
    private BufferedReader in;
    
    
	
	public Lexical(String filePath) {
		try {
			in = new BufferedReader(new FileReader(filePath)) ;
		} catch (FileNotFoundException e) {
			System.out.println("打开文件出错，请检查文件路径是否正确");
			e.printStackTrace();
		}
	}
      // 获取一个字符
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
				System.out.println("在getCh()中读取字符出错");
				e.printStackTrace();
			}
			lineLength = line.length() ;
			chCount = 0 ;
			System.out.println(line);
		}
		curCh = line.charAt(chCount++);
	}
	
	//获取一个单词
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
		if(type < 0 ){ // 说明为标识符
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
					System.out.println("检测到非法字符");
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
