/**
 * Created by BE on 11/23/2016.
 */
enum TokenType {Reserved, Operator, Literal, Identifier, Parenthese, SpecialSymbol, TypeSpecifier, Invalid}
public class Token {

    TokenType type;
    String value;

    public TokenType getType() {
        return type;
    }


    public String getValue() {
        return value;
    }

    public Token(TokenType type, String value) {
        this.type = type;
        this.value = value;
    }
    public String toString(){
        return value +" " +type ;

    }
}
