package sample.src;

/**
 * Created by BE on 11/23/2016.
 */
enum TokenType {Reserved, Operator, Literal, Identifier, Parenthese, SpecialSymbol, TypeSpecifier, Invalid,EOF}

public class Token {

    TokenType type;
    String value;
    int line;

    public TokenType getType() {
        return type;
    }


    public String getValue() {
        return value;
    }

    public Token(TokenType type, String value, int line) {
        this.type = type;
        this.value = value;
        this.line=line;
    }
    public String toString(){
        return value ;

    }
    public int getLine(){
        return line;
    }
}
