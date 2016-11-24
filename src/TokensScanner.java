
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import static java.lang.System.exit;

public class TokensScanner {


    static ArrayList<String> reservedWords = new ArrayList<>();
    static ArrayList<String> parenthes = new ArrayList<>();
    static ArrayList<String> specialSymbols = new ArrayList<>();
    static ArrayList<String> operators = new ArrayList<>();
    static ArrayList<String> typeSpecifiers = new ArrayList<>();
    static ArrayList<String> resultingfloats = new ArrayList<>();
    static ArrayList<String> resultingints = new ArrayList<>();
    static ArrayList<String> resultingids = new ArrayList<>();
    static ArrayList<String> resultingops = new ArrayList<>();
    static ArrayList<String> resultingreserved = new ArrayList<>();
    static ArrayList<String> resultingParenthese = new ArrayList<>();
    static ArrayList<String> resultingspecialChars = new ArrayList<>();
    static ArrayList<String> resultingInvadlidTokens = new ArrayList<>();
    static ArrayList<String> resultingTypeSpecifiers = new ArrayList<>();
    static ArrayList<String> resultingChars = new ArrayList<>();
    static ArrayList<String> resultingBools = new ArrayList<>();
    static ArrayList<String> resultingLiterals = new ArrayList<>();
    static ArrayList<Token> tokenes = new ArrayList<>();


    public static void main(String[] args) throws FileNotFoundException {
        initializeThings();
        Scanner sysin = new Scanner(System.in);

        System.out.print("Enter Src file path :: ");
        String path = sysin.nextLine();
        //if(new File(path))
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Wrong File Path");
            exit(0);
        }

        Scanner input = new Scanner(file);

        while (input.hasNext()) {
            ArrayList<String> arr = new ArrayList<>(Arrays.asList(input.nextLine().split(" ")));
            for (int i = 0; i < arr.size(); i++) {
                if (checkFloat(arr.get(i))) {
                    //  resultingfloats.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Literal);
                } else if (checkInteger(arr.get(i))) {
                    //resultingints.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Literal);
                } else if (specialSymbols.indexOf(arr.get(i)) >= 0) {
                    // resultingspecialChars.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.SpecialSymbol);

                } else if (checkChar(arr.get(i))) {
                    // resultingChars.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Literal);

                } else if (checkBoolean(arr.get(i))) {
                    // resultingBools.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Literal);

                } else if (reservedWords.indexOf(arr.get(i)) >= 0) {
                    // resultingreserved.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Reserved);

                } else if (typeSpecifiers.indexOf(arr.get(i)) >= 0) {
                    addInProberList(arr.get(i), TokenType.TypeSpecifier);

                } else if (operators.indexOf(arr.get(i)) >= 0) {
                    // resultingops.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Operator);

                } else if (parenthes.indexOf(arr.get(i)) >= 0) {
                    // resultingParenthese.add(arr.get(i));
                    addInProberList(arr.get(i), TokenType.Parenthese);

                } else {
                    String word = arr.get(i);
                    longestMatch(word);
                }
            }
        }
  /*      for (int i = 0; i < resultingInvadlidTokens.size(); i++) {
            String str = resultingInvadlidTokens.get(i);
            if (checkFloat(str)) {
                resultingfloats.add(str);
                resultingInvadlidTokens.remove(str);
                i--;
            } else if (checkInteger(str)) {
                resultingints.add(str);
                resultingInvadlidTokens.remove(str);
                i--;

            }
        }*/
        PrintLists();


    }

    private static void longestMatch(String word) {
        int id = 0;
        String invalidChars = "";
        //int reserved=-1;
        for (int k = 1; k <= word.length(); k++) {
            boolean invalid = true;
            if (invalidChars.length() == 0 && checkID(word.substring(0, k))) {
                id++;
                invalid = false;
            }
            if ((invalid && id > 0) || (k == word.length() && id > 0)) {
                ReservedOrId(word.substring(0, id));

                word = word.substring(id);
                k = 0;
                invalid = false;
                id = 0;
            }
            if (invalid) {
                if (parenthes.contains(word.substring(0, k))) {
                    //resultingParenthese.add(word.substring(0, k));
                    addInProberList(word.substring(0, k), TokenType.Parenthese);
                    invalid = false;
                    word = word.substring(k);
                    k = 0;
                } else if (specialSymbols.contains(word.substring(0, k))) {
                    // resultingspecialChars.add(word.substring(0, k));
                    addInProberList(word.substring(0, k), TokenType.SpecialSymbol);
                    invalid = false;
                    word = word.substring(k);
                    k = 0;

                } else {
                    if (k + 1 <= word.length() && operators.contains(word.substring(0, k + 1))) {
                        //   resultingops.add(word.substring(0, k + 1));
                        addInProberList(word.substring(0, k + 1), TokenType.Operator);
                        invalid = false;
                        word = word.substring(k + 1);
                        k = 0;
                    } else if (operators.contains(word.substring(0, k))) {
                        // resultingops.add(word.substring(0, k));
                        addInProberList(word.substring(0, k), TokenType.Operator);
                        invalid = false;
                        word = word.substring(k);
                        k = 0;
                    }

                }


            }
            if (!invalid && invalidChars.length() > 0) {
                swapAndAdd(invalidChars);
                invalidChars = "";
            }
            if (invalid) {
                invalidChars += word.substring(0, k);
                word = word.substring(k);
                k = 0;
                if (word.length() == 0)
                    addInProberList(invalidChars, TokenType.Invalid);
            }


        }
    }

    private static void swapAndAdd(String invalidChars) {
        Token token = tokenes.get(tokenes.size() - 1);
        tokenes.remove(tokenes.size() - 1);
        addInProberList(invalidChars, TokenType.Invalid);
        tokenes.add(token);
    }


    private static void addInProberList(String word, TokenType tokenType) {
        //Reserved, Operator, Literal, Identifier, Parenthese, SpecialSymbol
        switch (tokenType) {
            case Invalid:
               /* if (checkFloat(word))
                    resultingfloats.add(word);
                else if (checkInteger(word))
                    resultingints.add(word);
                else if (checkChar(word))
                    resultingChars.add(word);
                else if (checkBoolean(word))
                    resultingBools.add(word); */
                if (checkFloat(word) || checkChar(word) || checkBoolean(word) || checkInteger(word)) {
                    resultingLiterals.add(word);
                    tokenes.add(new Token(TokenType.Literal, word));
                    break;
                } else {
                    resultingInvadlidTokens.add(word);
                    tokenes.add(new Token(tokenType, word));
                    break;
                }

            case Reserved:
                tokenes.add(new Token(tokenType, word));
                resultingreserved.add(word);
                break;
            case Operator:
                tokenes.add(new Token(tokenType, word));
                resultingops.add(word);
                break;
            case Literal:
                tokenes.add(new Token(tokenType, word));
                resultingLiterals.add(word);
                break;
            case Identifier:
                tokenes.add(new Token(tokenType, word));
                resultingids.add(word);
                break;
            case Parenthese:
                tokenes.add(new Token(tokenType, word));
                resultingParenthese.add(word);
                break;
            case SpecialSymbol:
                tokenes.add(new Token(tokenType, word));
                resultingspecialChars.add(word);
                break;
            case TypeSpecifier:
                tokenes.add(new Token(tokenType, word));
                resultingTypeSpecifiers.add(word);

        }
    }

    private static void ReservedOrId(String word) {
        if (reservedWords.contains(word)) {
            //  resultingreserved.add(word);
            addInProberList(word, TokenType.Reserved);
        } else if (typeSpecifiers.contains(word)) {
            addInProberList(word, TokenType.TypeSpecifier);
        } else {
            //  resultingids.add(word);
            addInProberList(word, TokenType.Identifier);
        }
    }

    private static void PrintLists() {
        // System.out.println("resulting float :: " + resultingfloats);
        // System.out.println("resulting ints :: " + resultingints);
        //  System.out.println("resulting chars :: " + resultingChars);
        //  System.out.println("resulting bools :: " + resultingBools);
        System.out.println("resulting literals :: " + resultingLiterals);
        System.out.println("resulting ids :: " + resultingids);
        System.out.println("resulting ops :: " + resultingops);
        System.out.println("resulting Reserved Words :: " + resultingreserved);
        System.out.println("resulting typespecifiers :: " + resultingTypeSpecifiers);
        System.out.println("resulting Parenthese :: " + resultingParenthese);
        System.out.println("resulting specialChars :: " + resultingspecialChars);
        System.out.println("resulting InvadlidTokens :: " + resultingInvadlidTokens);
        System.out.println("resulting tokens :: " + tokenes);

    }


    private static void initializeThings() {

        reservedWords.add("if");
        reservedWords.add("else");
        reservedWords.add("for");
        reservedWords.add("while");
        typeSpecifiers.add("int");
        typeSpecifiers.add("float");
        typeSpecifiers.add("char");
        typeSpecifiers.add("bool");
        //   reservedWords.add("true");
        //   reservedWords.add("false");
        operators.add("=");
        operators.add("==");
        operators.add("!=");
        operators.add(">=");
        operators.add("<=");
        operators.add(">");
        operators.add("<");
        operators.add("+");
        operators.add("-");
        operators.add("*");
        operators.add("/");
        operators.add("&&");
        operators.add("||");
        operators.add("=");
        parenthes.add("(");
        parenthes.add(")");
        parenthes.add("{");
        parenthes.add("}");
        parenthes.add("[");
        parenthes.add("]");
        specialSymbols.add(",");
        specialSymbols.add(";");


    }

    static boolean checkInteger(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) < '0' || str.charAt(i) > '9')
                return false;
        }
        if (str.length() == 0)
            return false;
        return true;

    }

    static boolean checkFloat(String str) {


        if (!str.contains("."))
            return false;
        ArrayList<String> ints = new ArrayList<>(Arrays.asList(str.split("\\.")));
        if (ints.size() != 2)
            return false;

        if (!(checkInteger(ints.get(0)) && checkInteger(ints.get(0))))
            return false;
        return true;

    }

    static boolean checkChar(String str) {
        if (str.length() != 3)
            return false;
        char[] arr = str.toCharArray();
        return arr[0] == '\'' && arr[2] == '\'';

    }

    static boolean checkBoolean(String str) {
        return str.equals("true") || str.equals("false");


    }

    static boolean checkID(String str) {
        if (!isLetter(str.charAt(0))) {
            return false;
        }
        for (int i = 1; i < str.length(); i++) {
            if (!((isLetter(str.charAt(i))) || (checkInteger(String.valueOf(str.charAt(i))))))
                return false;
        }
        return true;

    }

    static boolean isLetter(char ch) {
        if ((ch <= 'Z' && ch >= 'A') || ((ch <= 'z' && ch >= 'a')))
            return true;
        return false;
    }
}