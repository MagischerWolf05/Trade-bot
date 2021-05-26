import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class Auswertung implements Initializable {
    @FXML
    Label profit;
    @FXML
    Label winingtrade;
    @FXML
    Label lossingtrade;
    @FXML
    Label loss;
    @FXML
    Label gain;
    @FXML
    Label profittrade;
    @FXML
    Label moneymade;
    StrategyTester tester;
    public Auswertung(StrategyTester data){
        tester = data;

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        getProfit();
    }

    public void getProfit(){
        int y = 0;
        Double[] profitarr = new Double[tester.trades.size()];
        for (int i = 0; i < tester.trades.size(); i++) {
            if(tester.trades.get(i).win){
                y++;
            }
            profitarr[i] = (tester.trades.get(i).getProfit(true ,0));
        }
        System.out.println(bublesort(profitarr)[0]);
        profit.setText(String.valueOf(bublesort(profitarr)[0]));
        winingtrade.setText(String.valueOf(y));
        lossingtrade.setText(String.valueOf(tester.trades.size() - y));
        moneymade.setText(String.valueOf(tester.balance - this.tester.initialBalance));
        profittrade.setText(String.valueOf(y / tester.trades.size() - y));
        gain.setText(String.valueOf(bublesort(profitarr)[0]));
        loss.setText(String.valueOf(bublesort(profitarr)[tester.trades.size()-1]));
    }
    




    public Double[] bublesort(Double[] arr){
        int n = arr.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (arr[j] > arr[j+1])
                {
                    // swap arr[j+1] and arr[j]
                    Double temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
        return arr;
    }

}
