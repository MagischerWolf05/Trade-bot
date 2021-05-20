import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.util.SimpleTimeZone;
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
    Label Balance;
    @FXML
    Label PositionSizing;
    @FXML
    Label stoploos;

    ObservableList<String> timezonearray = FXCollections.observableArrayList();
    public void initialize() {
    AddTimeZone();
    Timezonemenu.getItems().addAll(timezonearray);

    }

    public void AddTimeZone(){
        for (int i = 0; i < TimeZone.getAvailableIDs().length; i++) {
            timezonearray.add(TimeZone.getAvailableIDs()[i]);
        }
    }



    }