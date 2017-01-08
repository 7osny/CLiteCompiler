package sample.src;

import java.util.ArrayList;

/**
 * Created by Hefny on 12/28/2016.
 */
public class ParsingTree {

    public ParsingTree(Node node){
        root=node;
    }
    public static ArrayList<Node> getLeafNodes(Node node){
        ArrayList<Node> leafs=new ArrayList<>();
        if(node.getChilds().size()==0){
            leafs.add(node);
            return leafs;
        }
        else{
            for(Node ch : node.getChilds())
                leafs.addAll(getLeafNodes(ch));

        }
        return leafs;
    }
    public static ArrayList<Node> getNodes(Node node, String nodeName){
        ArrayList<Node> nodes=new ArrayList<>();
        if(node.getName().equalsIgnoreCase(nodeName)){
            nodes.add(node);
            return nodes;
        }
        else{
            for(Node ch : node.getChilds())
                nodes.addAll(getNodes(ch,nodeName));

        }
        return nodes;

    }
    public static ArrayList<Node> getNodes(Node node, TokenType type){
        ArrayList<Node> nodes=new ArrayList<>();
        if(node.getToken()!=null&&node.getToken().getType()==type){
            nodes.add(node);
            return nodes;
        }
        else{
            for(Node ch : node.getChilds())
                nodes.addAll(getNodes(ch,type));

        }
        return nodes;

    }



    public static class Node{
        String name;
        Token token;
        ArrayList<Node> childs=new ArrayList<>();
        public ArrayList<Node> getChilds() {
            return childs;
        }

        public void setChilds(ArrayList<Node> childs) {
            this.childs = childs;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Token getToken() {
            return token;
        }

        public void setToken(Token token) {
            this.token = token;
        }


        public Node(String name){
            this.name=name;

        }
        public Node(String name, Token type){
            this.name=name;
            this.token=type;

        }
        @Override
        public String toString() {
            return  name;
        }
        public void display(){
            String str=name+" -> ";
            str+=childs;
            System.out.println(str);
            for(Node ch : childs)
                ch.display();


        }
        public void add(Node node){
            childs.add(node);

        }

        @Override
        public boolean equals(Object obj) {
            return name.equalsIgnoreCase(((Node)obj).getName());
        }
    }
    Node root;
}
