package test;

import lgcCompiler.Lexical;
import lgcCompiler.Symbol;

public class LexicalTest {
	
	public static void main(String[] args) {
		String filePath = "C:/Users/liguochao/Documents/GitHub/lgcCompiler/lgcCompiler/lexical.txt" ;
		Lexical lexical = new Lexical(filePath) ;
		int i = 10 ;
		while(i>0){
			
			Symbol sym = lexical.getSymbol();
			if(sym!=null)
			System.out.println(sym.getSymtype());
			i--;
		}
	}
}
