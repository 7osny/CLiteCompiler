import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static java.lang.System.exit;

/**
 * Created by BE on 11/24/2016.
 */
public class EnrtyPoint {
    static TokensScanner tokensScanner=new TokensScanner();
    static Parser parser=new Parser();
    public static void main(String[] args) throws FileNotFoundException {
        Scanner sysin = new Scanner(System.in);

       // System.out.print("Enter Src file path :: ");
       // String path = sysin.nextLine();
        String path="f:\\src.txt";
        //if(new File(path))
        File file = new File(path);
        if (!file.exists()) {
            System.out.println("Wrong File Path");
            exit(0);
        }

        tokensScanner.startScanning(file); //start scanning
        if(tokensScanner.errorExist())
            System.exit(1);
        ParsingTree parsingTree=parser.startParsing(tokensScanner.getTokenes());
        SemanticsAnalyzer semanticsAnalyzer=new SemanticsAnalyzer(parsingTree);
        semanticsAnalyzer.startAnalyzer();

    }

}
