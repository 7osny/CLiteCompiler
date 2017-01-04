import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hefny on 12/28/2016.
 */
public class SemanticsAnalyzer {
    final String Not_Uniqe_EXC="Variable is already defined";
    final String Not_Declared_EXC="Cannot find symbol";
    final String Invalid_Type_EXC="Incompatible types";
    final String Invalid_Boolean_EXC="incompatible types: this type cannot be converted to boolean";
    enum LITERAL_TYPE{INT,FLOAT,CHAR,BOOL;};
    ParsingTree parsingTree;
    int whichLine=0;
    HashMap<String,LITERAL_TYPE> Symbol_Table=new HashMap<String,LITERAL_TYPE>();
    public SemanticsAnalyzer(ParsingTree parsingTree){
         this.parsingTree=parsingTree;

    }
    public void startAnalyzer(){

        ArrayList<ParsingTree.Node> decs=ParsingTree.getNodes(parsingTree.root,"deceleration");
        ArrayList<ParsingTree.Node> assigs=ParsingTree.getNodes(parsingTree.root,"assignment");
        ArrayList<ParsingTree.Node> ifs=ParsingTree.getNodes(parsingTree.root,"ifStatement");
        ArrayList<ParsingTree.Node> whiles=ParsingTree.getNodes(parsingTree.root,"whileStatement");

        for(ParsingTree.Node dec:decs)
               declaration(dec);
        for(ParsingTree.Node as:assigs)
            assignment(as);
        for(ParsingTree.Node ifstatement:ifs)
            ifStatment(ifstatement);
        for (ParsingTree.Node whileStat:whiles)
            whileStatement(whileStat);
        System.out.println("Semantics Analysis has finished successfully...");
    }
   //this function is responsible to add new variables to symbol table
    public void declaration(ParsingTree.Node declarationNode){
        ArrayList<ParsingTree.Node> ids=ParsingTree.getNodes(declarationNode,TokenType.Identifier);
        LITERAL_TYPE var_type=null;
        var_type = convertTypeSpecifier( ParsingTree.getNodes(declarationNode,TokenType.TypeSpecifier).get(0).getToken().getValue());
        for(int i=0;i<ids.size();i++){
           if(Symbol_Table.containsKey(ids.get(i).getToken().getValue()))
               showError(Not_Uniqe_EXC,ids.get(i).getToken());
            Symbol_Table.put(ids.get(i).getToken().getValue(),var_type);

       }
     //   System.out.print(Symbol_Table);
    }

    private LITERAL_TYPE convertTypeSpecifier(String str) {
         LITERAL_TYPE var_type = null;
        if(str.equalsIgnoreCase("int"))
           var_type= LITERAL_TYPE.INT;
        else if(str.equalsIgnoreCase("float"))
            var_type= LITERAL_TYPE.FLOAT;
        else if(str.equalsIgnoreCase("char"))
            var_type= LITERAL_TYPE.CHAR;
        else if(str.equalsIgnoreCase("bool"))
            var_type= LITERAL_TYPE.BOOL;
        return var_type;
    }

    public LITERAL_TYPE getExpressionLiteralType(ParsingTree.Node node){
        ArrayList<ParsingTree.Node>terminals=ParsingTree.getLeafNodes(node);
          if(ParsingTree.getNodes(node,"equOp").size()>0 ||ParsingTree.getNodes(node,"relOp").size()>0
                  ||terminals.contains("&&")||terminals.contains("||")) {
              checkBoolExpValidity(node);
              return LITERAL_TYPE.BOOL;
          }
          return getArithmeticLiteralType(node);
    }
    public LITERAL_TYPE getArithmeticLiteralType(ParsingTree.Node node){
     //   LITERAL_TYPE ex_type=null;
        ArrayList<ParsingTree.Node>prims=ParsingTree.getNodes(node,"primary");
        ArrayList<LITERAL_TYPE>primsLiteralType=new ArrayList<>();
        for(ParsingTree.Node unary:ParsingTree.getNodes(node,"unaryOP")){
            checkUnaryValidity(unary);
            primsLiteralType.add(LITERAL_TYPE.BOOL);
            ArrayList<ParsingTree.Node> toRemove = ParsingTree.getNodes(unary, "primary");
            for(int i=0;i<toRemove.size();i++){
                for(int j=0;j<prims.size();j++){
                    if(prims.get(j)==toRemove.get(i))
                        prims.remove(j);
                }
            }
        }
        for(ParsingTree.Node pr:prims){

            ArrayList<ParsingTree.Node>ids=ParsingTree.getNodes(pr,TokenType.Identifier);
            ArrayList<ParsingTree.Node>literals=ParsingTree.getNodes(pr,TokenType.Literal);
            if(ids.size()!=0){
                whichLine=ids.get(0).getToken().getLine();
                for(int i=0;i<ids.size();i++){
                    checkVariableExistence(ids.get(i).getName(), ids.get(i).getToken());
                    primsLiteralType.add(Symbol_Table.get(ids.get(i).getName()));
                }
            }
            if(literals.size()!=0){
                whichLine=literals.get(0).getToken().getLine();
                for(int i=0;i<literals.size();i++){
                    primsLiteralType.add(convertStrValueToLiteral(literals.get(i).getName()));
                }
            }
        }

        return dominatingLiteral(primsLiteralType);
    }

    private void checkUnaryValidity(ParsingTree.Node unary) {
        ArrayList<ParsingTree.Node>primes=ParsingTree.getNodes(unary,"primary");
        ParsingTree.Node node=new ParsingTree.Node("TestUnaryPrimes");
        node.setChilds(primes);

        if(getExpressionLiteralType(node)!=LITERAL_TYPE.BOOL){
            showError(Invalid_Boolean_EXC);
        }
    }

    private void checkBoolExpValidity(ParsingTree.Node node){
        ArrayList<ParsingTree.Node>conjunctions=ParsingTree.getNodes(node,"conjunction");
        for(ParsingTree.Node con:conjunctions) {

            ArrayList<ParsingTree.Node> equalities = ParsingTree.getNodes(con, "equality");
            for (ParsingTree.Node eq : equalities) {
                getArithmeticLiteralType(eq);
                if(!compatable(LITERAL_TYPE.BOOL,getExpressionLiteralType(eq)))
                    showError(Invalid_Boolean_EXC);

            }

        }


    }

    private LITERAL_TYPE dominatingLiteral(ArrayList<LITERAL_TYPE> primsLiteralType) {

        if(primsLiteralType.contains(LITERAL_TYPE.FLOAT)&&primsLiteralType.contains(LITERAL_TYPE.BOOL))
            showError(Invalid_Type_EXC);
        if(primsLiteralType.contains(LITERAL_TYPE.BOOL)
                &&(primsLiteralType.contains(LITERAL_TYPE.CHAR)
                ||primsLiteralType.contains(LITERAL_TYPE.INT))){
            showError(Invalid_Type_EXC);
        }
        if(primsLiteralType.contains(LITERAL_TYPE.FLOAT))
            return LITERAL_TYPE.FLOAT;
        if(primsLiteralType.contains(LITERAL_TYPE.BOOL))
            return LITERAL_TYPE.BOOL;
        if(primsLiteralType.contains(LITERAL_TYPE.INT))
            return LITERAL_TYPE.INT;
        if(primsLiteralType.contains(LITERAL_TYPE.CHAR))
            return LITERAL_TYPE.CHAR;
        showError("UnExpected Error");
        return  null;
    }
    private void ifStatment(ParsingTree.Node node){
        if(getExpressionLiteralType(ParsingTree.getNodes(node,"expression").get(0))!=LITERAL_TYPE.BOOL)
                 showError(Invalid_Boolean_EXC);
        for(ParsingTree.Node var:ParsingTree.getNodes(node,TokenType.Identifier))
            checkVariableExistence(var.getName(),var.getToken());
    }
    private void whileStatement(ParsingTree.Node node){
        if(getExpressionLiteralType(ParsingTree.getNodes(node,"expression").get(0))!=LITERAL_TYPE.BOOL)
            showError(Invalid_Boolean_EXC);
        for(ParsingTree.Node var:ParsingTree.getNodes(node,TokenType.Identifier))
           checkVariableExistence(var.getName(),var.getToken());
    }
    private void assignment(ParsingTree.Node node){
        ArrayList<ParsingTree.Node>ids=ParsingTree.getNodes(node,TokenType.Identifier);
        for(ParsingTree.Node var:ids)
            checkVariableExistence(var.getName(),var.getToken());
        String id=ParsingTree.getNodes(node,TokenType.Identifier).get(0).getName();
        LITERAL_TYPE type=Symbol_Table.get(id);
        ArrayList<ParsingTree.Node> exps=ParsingTree.getNodes(node,"expression");
        for(ParsingTree.Node ex : exps){
            if(!compatable((type),getExpressionLiteralType(ex)))
                showError(Invalid_Type_EXC);
        }


    }
    public boolean compatable(LITERAL_TYPE target,LITERAL_TYPE source ) {
        if(target==source)
            return true;
       if(target==LITERAL_TYPE.FLOAT&&(source==LITERAL_TYPE.INT
               ||source==LITERAL_TYPE.CHAR))
           return true;
       if(target==LITERAL_TYPE.INT&&source==LITERAL_TYPE.CHAR)
           return true;
       return false;
    }
    private void checkVariableExistence(String name, Token token) {
        if(Symbol_Table.containsKey(name))
            return;
        else
            showError(Not_Declared_EXC,token);
    }
    private LITERAL_TYPE convertStrValueToLiteral(String str){
        if(TokensScanner.checkInteger(str))
            return LITERAL_TYPE.INT;
        if(TokensScanner.checkFloat(str))
            return LITERAL_TYPE.FLOAT;
        if(TokensScanner.checkBoolean(str))
            return LITERAL_TYPE.BOOL;
        if(TokensScanner.checkChar(str))
            return LITERAL_TYPE.CHAR;
        showError("UnExpected Error");
        return null;

    }
    public void showError(String str,Token token){
        System.out.println("Semantics Error at line ( "+token.line+" ) ::: "+str);
        System.exit(1);
    }
    public void showError(String str){
        System.out.println("Semantics Error at line ( "+whichLine+" ) ::: "+str);
        System.exit(1);
    }

}
