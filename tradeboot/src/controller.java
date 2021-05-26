import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.*;

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
    // ComboBox stock;

    ObservableList<String> timezonearray = FXCollections.observableArrayList();
    ObservableList<String> optionarray = FXCollections.observableArrayList();
    ObservableList<String> buyOperatorarray = FXCollections.observableArrayList();

    String buyoptionstring = "";
    String selloptionstring = "";

    public void initialize() {
    compareOption();
    AddTimeZone();
    Optiondropdown();
    buyoption2options();
    selloption2options();
    buyOperator.getItems().addAll(this.buyOperatorarray);
    selloperator.getItems().addAll(this.buyOperatorarray);
    Timezonemenu.getItems().addAll(this.timezonearray);
    buyOption.getItems().addAll(this.optionarray);
    buyoption2.getItems().addAll(this.optionarray);
    selloption.getItems().addAll(this.optionarray);
    selloption2.getItems().addAll(this.optionarray);

        calculate.setOnAction((ActionEvent event) -> {
            this.getAlldata();
            //alles erstellen
            //Condition buyCondition = new Condition(this.valuebuyoption1,this.valuebuyoperator,this.valuebuyoption2);
            //Condition sellCondition = new Condition(this.valueselloption1,this.valueselloperator,this.valueselloption2);
            Condition buyCondition = new Condition("Preis","<","Avg Preis 24H");
            Condition sellCondition = new Condition("Preis", ">","Avg Preis 24H");
            Strategy strat = new Strategy(buyCondition,sellCondition,"10%","10%");
            StrategyTester tester = new StrategyTester(strat,100.0);

        });
    }

    public void AddTimeZone(){
        for (int i = 0; i < TimeZone.getAvailableIDs().length; i++) {
            this.timezonearray.add(TimeZone.getAvailableIDs()[i]);
        }
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
     this.balancevalue = balance.getText();
     this.positionssizingvalue = positionssizing.getText();
     this.stoplossvalue = stoploss.getText();



    }
}