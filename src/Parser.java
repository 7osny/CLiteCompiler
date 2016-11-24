import com.sun.org.apache.xpath.internal.SourceTree;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.System.exit;

/**
 * Created by BE on 11/24/2016.
 */
public class Parser {
    Queue<Token> tokenes;
    Token currentToken;

    public void startParsing(ArrayList<Token> resultingTokens) {
        tokenes = new LinkedList<>(resultingTokens);
        currentToken = tokenes.poll();



        program();
        if(currentToken.getType()==TokenType.EOF){
            System.out.println("Parsing has finished successfully");
        }
    }
    // Program   int main () { Declarations Statements }
    private void program() {
        match("int");
        match("main");
        match("(");
        match(")");
        match("{");
        declerations();
        statements();
        match("}");

    }

    //Declarations  { Declaration }


    private void declerations() {
        while (currentToken.getType() == TokenType.TypeSpecifier) {
            decleration();
        }
    }
    //Declaration  Type Identifier [ [ Integer ] ] { , Identifier [ [ Integer ] ] }
    private void decleration() {
        match(TokenType.TypeSpecifier);
        match(TokenType.Identifier);
        while (currentToken.getValue().equals(",")) {
            match(",");
            match(TokenType.Identifier);
            if (currentToken.getType()==TokenType.Parenthese) {

                    match("[");
                    if (TokensScanner.checkInteger(currentToken.getValue()))
                        match(TokenType.Literal);
                    else
                        showError();
                    match("]");
            }
        }
        match(";");
    }
    //  Statements  {  Statement  }


    private void statements() {
       // match("{");
        while(currentToken.getValue().equals(";")
                ||currentToken.getValue().equals("{")
                ||currentToken.getType()==TokenType.Identifier
                ||currentToken.getValue().toLowerCase().equals("if")
                ||currentToken.getValue().toLowerCase().equals("while") )
        {
        statement();
        }
      //  match("}");

    }
    //  Statement  ; | Block; | Assignment;| IfStatement; | WhileStatement;

    private void statement() {
        switch (currentToken.getType()) {
            case SpecialSymbol:
                //match(";");
                break;
            case Parenthese://Block match
                block();
                break;
            case Identifier:  //assignment match
                assignment();
                break;
            case Reserved:
                if (currentToken.getValue().toLowerCase().equals("if"))
                    ifStatement();

                else if (currentToken.getValue().toLowerCase().equals("while"))
                    whileStatement();
                else
                    showError();
                break;

            default:
                showError();
        }
        match(";");
    }
    //  Block  { Statements }
    private void block() {
        while(currentToken.getValue()=="{")
            statements();
    }

    //  Assignment  Identifier [ [ Expression ] ] = Expression;
    private void assignment() {
        match(TokenType.Identifier);
        if (currentToken.getValue().equals("[")) {
            match("[");
            expression();
            match("]");

        }
        match("=");
        expression();
    }

    //   WhileStatement  while ( Expression ) Statement
    private void whileStatement() {
        match("while");
        match("(");
        expression();
        match(")");
        statement();
    }
    //   IfStatement  if ( Expression ) Statement [ else Statement ]
    private void ifStatement() {
        match("if");
        match("(");
        expression();
        match(")");
        statement();
        if(currentToken.getValue()=="else"){
            match("else");
            statement();
        }
    }
//Expression  Conjunction { || Conjunction }
    private void expression() {
        conjunction();
        while (currentToken.getValue().equals("||")) {
            match("||");
            conjunction();
        }
    }



//Conjunction  Equality { && Equality }
    private void conjunction() {
        equality();
        while (currentToken.getValue().equals("&&")) {
            match("&&");
            equality();
        }
    }
    //Equality  Relation [ EquOp Relation ]
    private void equality() {
        relation();

        if (equOp()) {
            match(TokenType.Operator);
            relation();
        }
    }
    //EquOp  == | !=
    private boolean equOp() {
        return currentToken.getValue().equals("==") || currentToken.getValue().equals("!=");
    }

    // Relation   Addition [ RelOp Addition]
    private void relation() {
        addition();
        if (relOp()) {
            match(TokenType.Operator);
            addition();

        }
    }
    // RelOp  < | <= | > | >=
    private boolean relOp() {
        return currentToken.getValue().equals("<=") || currentToken.getValue().equals("<") || currentToken.getValue().equals(">=");
    }
    //   Addition Term { AddOp Term }

    private void addition() {
        term();
        while (addOp()) {
            match(TokenType.Operator);
            term();
        }
    }
    //   AddOp   + | -

    private boolean addOp() {
        return currentToken.getValue().equals("+") || currentToken.getValue().equals("-");
    }
//   Term  Factor { MulOp Factor }


    private void term() {
        factor();
        while (mulOp()) {
            match(TokenType.Operator);
            factor();
        }
    }
    //   MulOp  * | / | %
    private boolean mulOp() {
        return currentToken.getValue().equals("*") || currentToken.getValue().equals("/") || currentToken.getValue().equals("%");
    }
    //  Factor  [ UnaryOp ] Primary


    private void factor() {
        if (unaryOp()) {
            match(TokenType.Operator);
        }
        primary();
    }

    //  Primary   Identifier [ [Expression] ] | Literal | ( Expression ) | Type ( Expression)
    private void primary() {
        switch (currentToken.getType()) {
            case Identifier:
                match(TokenType.Identifier);
                if (currentToken.getValue().equals("[")) {
                    match(TokenType.Parenthese);
                    expression();
                    match("]");

                }
                break;
            case Parenthese:
                if (currentToken.getType().equals("(")) {
                    match("(");
                    expression();
                    match(")");
                } else
                    showError();
                break;
            case TypeSpecifier:
                match(TokenType.TypeSpecifier);
                match("(");
                expression();
                match(")");
                break;
            case Literal:
                match(TokenType.Literal);
                break;
            default:
                showError();
        }
    }
    // UnaryOP  - | !
    private boolean unaryOp() {
        return currentToken.getValue().equals("-") || currentToken.getValue().equals("!");
    }

    private void match(String expected) {
        if (expected.equals(currentToken.getValue()))
            currentToken = tokenes.poll();
        else
            showError();

    }

    private void match(TokenType type) {
        if (type == currentToken.getType())
            currentToken = tokenes.poll();
        else
            showError();
    }

    private void showError() {
        System.out.println("Parser Couldn't finish error \n at line :: " + currentToken.getLine() + "\n in --> " + currentToken.getValue());
        exit(1);
    }
}
