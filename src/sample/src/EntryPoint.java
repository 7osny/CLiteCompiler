package sample.src;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import sample.src.Parser;
import sample.src.ParsingTree;
import sample.src.SemanticsAnalyzer;
import sample.src.TokensScanner;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.lang.System.exit;

public class EntryPoint extends Application {
    static HBox h;
    static Label label;
    static HBox top;
    static Button fillBtn;
    static TextField pathField;
    static TextArea codeTA;
    static TextArea errorTA;
    static Button compileBtn;
    static VBox vb;
    static String source = "";

    @Override
    public void start(Stage primaryStage) throws Exception {
        h = new HBox(8);
        label = new Label("Source Path :: ");
        top = new HBox();
        Button fillBtn = new Button("Add");
        pathField = new TextField();
        pathField.setMinWidth(1000);
        top.getChildren().addAll(label, pathField, fillBtn);
        top.setSpacing(10);


        codeTA = new TextArea();
        errorTA = new TextArea();
        codeTA.setMinWidth(700);
        errorTA.setMinWidth(700);
        codeTA.setMinHeight(500);
        errorTA.setMinHeight(500);
        //pathField.set(new Insets(10,0,0,0));
        compileBtn = new Button("Compile");
        vb = new VBox(8);
        vb.getChildren().add(top);
        h.getChildren().add(codeTA);
        h.getChildren().add(errorTA);
        h.setAlignment(Pos.CENTER);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(vb);
        vb.getChildren().addAll(h, compileBtn);
        // Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        vb.setAlignment(Pos.CENTER);
        compileBtn.setOnMouseClicked(event -> onCompileClicked());
        fillBtn.setOnMouseClicked(event -> onAddClicked());
        //   borderPane.setTop(pathField);
        primaryStage.setTitle("Compiler");
        primaryStage.setScene(new Scene(borderPane, 1400, 730));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public static void showError(String msg) {

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("");
        alert.setContentText(msg);
        alert.show();

    }

    public static void showSuccess(String msg) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("");
        alert.setContentText(msg);
        alert.show();

    }

    public static void onCompileClicked() {
        errorTA.clear();
       // showSuccess("onCompileClicked");
        fillTA();
        TokensScanner tokensScanner = new TokensScanner();
        Parser parser = new Parser();
        errorTA.setText(errorTA.getText()+"\n"+tokensScanner.startScanning(source)); //start scanning
        if (tokensScanner.errorExist())
            return;
        ParsingTree parsingTree = parser.startParsing(tokensScanner.getTokenes());
        errorTA.setText(errorTA.getText()+"\n"+parser.getErrorMsg());
        if (parser.getErrorBol())
            return;
        SemanticsAnalyzer semanticsAnalyzer = new SemanticsAnalyzer(parsingTree);
        semanticsAnalyzer.startAnalyzer();
        errorTA.setText(errorTA.getText()+"\n"+semanticsAnalyzer.getErrorMsg());
        if (semanticsAnalyzer.getErrorBol())
            return;


    }

    public static void onAddClicked() {
        String path = pathField.getText();
        StringBuilder sb=new StringBuilder();
        //if(new File(path))
        File file = new File(path);
        try {
            Scanner input = new Scanner(file);
            while (input.hasNext()) {
                sb.append (input.nextLine());
                sb.append('\n');
            }
            codeTA.setText(sb.toString());
            fillTA();
        } catch (Exception ex) {
            showError("File not exist");

        }


    }

    public static void fillTA() {
        source=codeTA.getText().toString();

    }
}
