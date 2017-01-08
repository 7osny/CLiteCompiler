package sample.src;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import static java.lang.System.err;
import static java.lang.System.exit;

/**
 * Created by BE on 11/24/2016.
 */
public class Parser {
    Queue<Token> tokenes;
    Token currentToken;
    String errorMsg="Parsing has finished successfully....";
    private boolean errorBol;

    public boolean getErrorBol(){
        return errorBol;
    }
    public ParsingTree startParsing(ArrayList<Token> resultingTokens) {
        tokenes = new LinkedList<>(resultingTokens);
        currentToken = tokenes.poll();


        ParsingTree.Node node = new ParsingTree.Node("Start");
        node.add(program());
        if (currentToken.getType() == TokenType.EOF) {
            System.out.println("Parsing has finished successfully....");
        }
        ParsingTree pt = new ParsingTree(node);
        return pt;
    }

    // Program   int main () { Declarations Statements }
    private ParsingTree.Node program() {
        ParsingTree.Node node = new ParsingTree.Node("Program");
        node.add(match("int"));
        node.add(match("main"));
        node.add(match("("));
        node.add(match(")"));
        node.add(match("{"));
        node.add(decelerations());
        node.add(statements());
        node.add(match("}"));
        return node;
    }

    //Declarations  { Declaration }


    private ParsingTree.Node decelerations() {
        ParsingTree.Node node = new ParsingTree.Node("decelerations");
        while (currentToken.getType() == TokenType.TypeSpecifier) {
            node.add(deceleration());
        }
        return node;
    }

    //Declaration  Type Identifier [ [ Integer ] ] { , Identifier [ [ Integer ] ] ; }

    private ParsingTree.Node deceleration() {
        ParsingTree.Node node = new ParsingTree.Node("deceleration");
        node.add(match(TokenType.TypeSpecifier));
        node.add(match(TokenType.Identifier));
        if (currentToken.getValue().equals("[")) {
            matchArray(node);
            while (currentToken.getValue().equals(",")){
                node.add(match(","));
                node.add(match(TokenType.Identifier));
                matchArray(node);
            }
        } else {
            while (currentToken.getValue().equals(",")) {
                node.add(match(","));
                node.add(match(TokenType.Identifier));
                if (currentToken.getType() == TokenType.Parenthese) {

                    node.add(match("["));
                    if (TokensScanner.checkInteger(currentToken.getValue()))
                        node.add(match(TokenType.Literal));
                    else
                        showError();
                    node.add(match("]"));
                }
            }
        }
        node.add(match(";"));
        return node;
    }

    private void matchArray(ParsingTree.Node node) {
        node.add(match("["));
        if (!TokensScanner.checkInteger(currentToken.getValue()))
            showError();
        node.add(match(TokenType.Literal));
        node.add(match("]"));
    }
    //  Statements  {  Statement  }


    private ParsingTree.Node statements() {
        ParsingTree.Node node = new ParsingTree.Node("statements");
        // match("{");
        while (currentToken.getValue().equals(";")
                || currentToken.getValue().equals("{")
                || currentToken.getType() == TokenType.Identifier
                || currentToken.getValue().toLowerCase().equals("if")
                || currentToken.getValue().toLowerCase().equals("while")) {
            node.add(statement());
        }
        //  match("}");
        return node;
    }
    //  Statement  ; | Block | Assignment;| IfStatement | WhileStatement

    private ParsingTree.Node statement() {
        ParsingTree.Node node = new ParsingTree.Node("statement");
        switch (currentToken.getType()) {
            case SpecialSymbol:
                node.add(match(";"));
                break;
            case Parenthese://Block match
                node.add(block());
                break;
            case Identifier:  //assignment match
                node.add(assignment());
                node.add(match(";"));
                break;
            case Reserved:
                if (currentToken.getValue().toLowerCase().equals("if"))
                    node.add(ifStatement());

                else if (currentToken.getValue().toLowerCase().equals("while"))
                    node.add(whileStatement());
                else
                    showError();
                break;

            default:
                showError();
        }
        // match(";");
        return node;
    }

    //  Block  { Statements }||;
    private ParsingTree.Node block() {
        ParsingTree.Node node = new ParsingTree.Node("block");
        if (currentToken.getValue().equals(";")) {
            node.add(match(";"));
        } else if (currentToken.getValue().equals("{")) {
            while (currentToken.getValue().equals("{")) {
                node.add(match("{"));
                node.add(statements());
                node.add(match("}"));
            }
        }
        return node;
    }

    //  Assignment  Identifier [ [ Expression ] ] = Expression;
    private ParsingTree.Node assignment() {
        ParsingTree.Node node = new ParsingTree.Node("assignment");
        node.add(match(TokenType.Identifier));
        if (currentToken.getValue().equals("[")) {
            node.add(match("["));
            node.add(expression());
            node.add(match("]"));

        }
        node.add(match("="));
        node.add(expression());
        return node;
    }

    //   WhileStatement  while ( Expression ) Statement
    private ParsingTree.Node whileStatement() {
        ParsingTree.Node node = new ParsingTree.Node("whileStatement");
        node.add(match("while"));
        node.add(match("("));
        node.add(expression());
        node.add(match(")"));
        node.add(statement());
        return node;
    }

    //   IfStatement  if ( Expression ) Statement [ else Statement ]
    private ParsingTree.Node ifStatement() {
        ParsingTree.Node node = new ParsingTree.Node("ifStatement");
        node.add(match("if"));
        node.add(match("("));
        node.add(expression());
        node.add(match(")"));
        node.add(statement());
        if (currentToken.getValue().equals("else")) {
            node.add(match("else"));
            node.add(statement());
        }
        return node;
    }

    //Expression  Conjunction { || Conjunction }
    private ParsingTree.Node expression() {
        ParsingTree.Node node = new ParsingTree.Node("expression");
        node.add(conjunction());
        while (currentToken.getValue().equals("||")) {
            node.add(match("||"));
            node.add(conjunction());
        }
        return node;
    }


    //Conjunction  Equality { && Equality }
    private ParsingTree.Node conjunction() {
        ParsingTree.Node node = new ParsingTree.Node("conjunction");
        node.add(equality());
        while (currentToken.getValue().equals("&&")) {
            node.add(match("&&"));
            node.add(equality());
        }
        return node;
    }

    //Equality  Relation [ EquOp Relation ]
    private ParsingTree.Node equality() {
        ParsingTree.Node node = new ParsingTree.Node("equality");
        node.add(relation());

        if (equOp()) {
            ParsingTree.Node eqNode = new ParsingTree.Node(("equOp"));
            eqNode.add(match(TokenType.Operator));
            node.add(eqNode);
            node.add(relation());

        }
        return node;
    }

    //EquOp  == | !=
    private boolean equOp() {
        return currentToken.getValue().equals("==") || currentToken.getValue().equals("!=");
    }

    // Relation   Addition [ RelOp Addition]
    private ParsingTree.Node relation() {
        ParsingTree.Node node = new ParsingTree.Node("relation");
        node.add(addition());
        if (relOp()) {
            ParsingTree.Node relNode = new ParsingTree.Node(("relOp"));
            relNode.add(match(TokenType.Operator));
            node.add(relNode);
            node.add(addition());

        }
        return node;

    }

    // RelOp  < | <= | > | >=
    private boolean relOp() {
        return currentToken.getValue().equals("<=") || currentToken.getValue().equals("<") || currentToken.getValue().equals(">") || currentToken.getValue().equals(">=");
    }
    //   Addition Term { AddOp Term }

    private ParsingTree.Node addition() {
        ParsingTree.Node node = new ParsingTree.Node("addition");
        node.add(term());
        while (addOp()) {
            node.add(match(TokenType.Operator));
            node.add(term());
        }
        return node;
    }
    //   AddOp   + | -

    private boolean addOp() {
        return currentToken.getValue().equals("+") || currentToken.getValue().equals("-");
    }
//   Term  Factor { MulOp Factor }


    private ParsingTree.Node term() {
        ParsingTree.Node node = new ParsingTree.Node("term");
        node.add(factor());
        while (mulOp()) {
            node.add(match(TokenType.Operator));
            node.add(factor());
        }
        return node;
    }

    //   MulOp  * | / | %
    private boolean mulOp() {
        return currentToken.getValue().equals("*") || currentToken.getValue().equals("/") || currentToken.getValue().equals("%");
    }
    //  Factor  [ UnaryOp ] Primary


    private ParsingTree.Node factor() {
        ParsingTree.Node node = new ParsingTree.Node("factor");
        ParsingTree.Node unaryNode = null;
        if (unaryOp()) {
            unaryNode = new ParsingTree.Node("unaryOP");
            unaryNode.add(match(TokenType.Operator));

        }
        if (unaryNode != null) {
            unaryNode.add(primary());
            node.add(unaryNode);
        } else
            node.add(primary());
        return node;
    }

    //  Primary   Identifier [ [Expression] ] | Literal | ( Expression ) | Type ( Expression)
    private ParsingTree.Node primary() {
        ParsingTree.Node node = new ParsingTree.Node("primary");
        switch (currentToken.getType()) {
            case Identifier:
                node.add(match(TokenType.Identifier));
                if (currentToken.getValue().equals("[")) {
                    node.add(match(TokenType.Parenthese));
                    node.add(expression());
                    node.add(match("]"));

                }
                break;
            case Parenthese:
                if (currentToken.getValue().equals("(")) {
                    node.add(match("("));
                    node.add(expression());
                    node.add(match(")"));
                } else
                    showError();
                break;
            case TypeSpecifier:
                node.add(match(TokenType.TypeSpecifier));
                node.add(match("("));
                node.add(expression());
                node.add(match(")"));
                break;
            case Literal:
                node.add(match(TokenType.Literal));
                break;
            default:
                showError();
        }
        return node;
    }

    // UnaryOP  - | !
    private boolean unaryOp() {
        return currentToken.getValue().equals("-") || currentToken.getValue().equals("!");
    }

    private ParsingTree.Node match(String expected) {
        ParsingTree.Node node = null;
        if (expected.equals(currentToken.getValue())) {
            node = new ParsingTree.Node(currentToken.toString(), currentToken);
            currentToken = tokenes.poll();

        } else
            showError();
        return node;

    }

    private ParsingTree.Node match(TokenType type) {
        ParsingTree.Node node = null;
        if (type == currentToken.getType()) {
            node = new ParsingTree.Node(currentToken.toString(), currentToken);
            currentToken = tokenes.poll();
        } else
            showError();
        return node;
    }

    private void showError() {
        System.out.println("Parser Couldn't finish error \n at line :: " + currentToken.getLine() + "\n in --> " + currentToken.getValue());
        if(!errorBol){
            errorBol=true;
            errorMsg="Parser Couldn't finish error \n at line :: " + currentToken.getLine() + "\n in --> " + currentToken.getValue();
        }
       // exit(1);
    }
    public String getErrorMsg(){
        return errorMsg;
    }


}
