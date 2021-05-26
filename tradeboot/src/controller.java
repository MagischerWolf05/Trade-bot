import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.util.TimeZone;


public class controller{



    @FXML
    ComboBox Timezonemenu;
    @FXML
    ComboBox buyOption;
    @FXML
    ComboBox buyOperator;
    @FXML
    ComboBox buyoption2;
    @FXML
    ComboBox selloption;
    @FXML
    ComboBox selloperator;
    @FXML
    ComboBox selloption2;
    @FXML
    ComboBox stock;
    @FXML
    TextField balance;
    @FXML
    TextField stoploss;
    @FXML
    TextField positionssizing;
    @FXML
    Button calculate;

    String valuetimezone = "";
    String valuebuyoption1 = "";
    String valuebuyoperator = "";
    String valuebuyoption2 = "";
    String valueselloption1 = "";
    String valueselloperator = "";
    String valueselloption2 = "";
    String balancevalue = "";
    String positionssizingvalue = "";
    String stoplossvalue = "";
    String stockValue = "";
    // ComboBox stock;

    ObservableList<String> timezonearray = FXCollections.observableArrayList();
    ObservableList<String> optionarray = FXCollections.observableArrayList();
    ObservableList<String> buyOperatorarray = FXCollections.observableArrayList();
    ObservableList<String> stocksarray = FXCollections.observableArrayList();

    String buyoptionstring = "";
    String selloptionstring = "";
    StrategyTester tester = null;
    public void initialize() {
    compareOption();
    AddTimeZone();
    Optiondropdown();
    stocksoption();
    buyoption2options();
    selloption2options();
    buyOperator.getItems().addAll(this.buyOperatorarray);
    selloperator.getItems().addAll(this.buyOperatorarray);
    Timezonemenu.getItems().addAll(this.timezonearray);
    buyOption.getItems().addAll(this.optionarray);
    buyoption2.getItems().addAll(this.optionarray);
    selloption.getItems().addAll(this.optionarray);
    selloption2.getItems().addAll(this.optionarray);
    stock.getItems().addAll(this.stocksarray);
        calculate.setOnAction((ActionEvent event) -> {
            this.getAlldata();
            //alles erstellen
            //Condition buyCondition = new Condition(this.valuebuyoption1,this.valuebuyoperator,this.valuebuyoption2);
            //Condition sellCondition = new Condition(this.valueselloption1,this.valueselloperator,this.valueselloption2);
            Condition buyCondition = new Condition( valuebuyoption1,valuebuyoperator,valuebuyoption1);
            Condition sellCondition = new Condition(valueselloption1,valueselloperator,valueselloption2);
            Strategy strat = new Strategy(buyCondition,sellCondition,stoplossvalue,positionssizingvalue);
            tester = new StrategyTester(strat,100.0,this.stockValue);
            try {
                openWindow();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void AddTimeZone(){
        for (int i = 0; i < TimeZone.getAvailableIDs().length; i++) {
            this.timezonearray.add(TimeZone.getAvailableIDs()[i]);
        }
    }
    public void stocksoption(){
        stock.setEditable(true);
        stocksarray.add("AAPL");
        stocksarray.add("AMD");
        stocksarray.add("MSFT");
        stocksarray.add("GME");
        stocksarray.add("GOOGL");
        stocksarray.add("IBM");

    }
    public void Optiondropdown()
    {
        this.optionarray.add("Preis");
        this.optionarray.add("14 Day RSI");
        this.optionarray.add("Avg Preis Stunde");
        this.optionarray.add("Avg Preis 5 Stunden");
        this.optionarray.add("Avg Preis 10 Tage");
        this.optionarray.add("Avg Preis 24h");
        this.optionarray.add("Avg Preis 7 Tage");
    }
    public void buyoption2options(){
        buyoption2.setEditable(true);
        buyoption2.setOnAction((Event ev) -> {
           this.buyoptionstring = buyoption2.getSelectionModel().getSelectedItem().toString();
        });
    }
    public void selloption2options(){
        selloption2.setEditable(true);
        selloption2.setOnAction((Event ev) -> {
            this.selloptionstring = selloption2.getSelectionModel().getSelectedItem().toString();
        });
    }
    public void compareOption()
    {
        this.buyOperatorarray.add("<");
        this.buyOperatorarray.add("=");
        this.buyOperatorarray.add(">");
    }
    public void getAlldata(){
     this.valuetimezone = (String) this.Timezonemenu.getValue();
     this.valuebuyoption1 = (String)this.buyOption.getValue();
     this.valuebuyoperator = (String)this.buyOperator.getValue();
     this.valuebuyoption2 = (String)this.buyoption2.getValue();
     this.valueselloption1 = (String) this.selloption.getValue();
     this.valueselloperator = (String)this.selloperator.getValue();
     this.valueselloption2 = (String)this.selloption2.getValue();
     this.stockValue = (String)this.stock.getValue();
     this.balancevalue = balance.getText();
     this.positionssizingvalue = positionssizing.getText();
     this.stoplossvalue = stoploss.getText();



    }
    public void openWindow() throws IOException {





     try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("auswertung.fxml"));
            Auswertung controller = new Auswertung(this.tester);
             fxmlLoader.setController(controller);
            Parent root1 = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setTitle("ABC");
            stage.setScene(new Scene(root1));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}