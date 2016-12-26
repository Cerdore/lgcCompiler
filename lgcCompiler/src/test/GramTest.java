package test;

import lgcCompiler.Grammatical;
import lgcCompiler.Lexical;
import lgcCompiler.SymbolTable;

public class GramTest {
	public static void main(String[] args) {
		String filePath = "C:/Users/liguochao/Documents/GitHub/lgcCompiler/lgcCompiler/Grammer.txt" ;
		Lexical lexical = new Lexical(filePath) ;
		SymbolTable table = new SymbolTable();
		Grammatical gram = new Grammatical(lexical, table);
		gram.program();
	}
}
