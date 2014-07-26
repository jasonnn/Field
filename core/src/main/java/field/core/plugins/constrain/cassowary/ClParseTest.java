package field.core.plugins.constrain.cassowary;

// $Id: ClParseTest.java,v 1.1 1999/12/16 01:23:07 gjb Exp $
// ClParseTest.java
// Needs JavaCUP 10k or newer, from:
// http://www.cs.princeton.edu/~appel/modern/java/CUP/
// And JLex from:
// http://www.cs.princeton.edu/~appel/modern/java/JLex/index.html

import com.sun.java_cup.internal.runtime.Symbol;

import java.io.StringReader;
import java.util.Hashtable;

public class ClParseTest {
	public static final
    void main(String[] args) {
		try {
            //System.out.println("Parsing: " + args[0]);
            StringReader string_reader = new StringReader(args[0]);
			Yylex yylex = new Yylex(string_reader);
			Hashtable<String, ClVariable> varmap = new Hashtable<String, ClVariable>();
			yylex.setVariableNameObjectHash(varmap);
			parser constraint_parser = new parser(yylex);
			constraint_parser.setVariableNameObjectHash(varmap);
			Symbol symbol = constraint_parser.parse();
            //System.out.println("Parsed as: " + symbol.value.toString());
        } catch (Exception exception) {
            //System.out.println("Exception: " + exception.getMessage());
            exception.printStackTrace();
		}
	}
}
