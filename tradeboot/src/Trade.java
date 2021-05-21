import java.time.LocalDateTime;

public class Trade {
    public Double buyPrice;
    public LocalDateTime buyDate;
    public boolean win;
    public Double soldPrice;
    public LocalDateTime soldDate;

    Trade(LocalDateTime buyDate, Double buyPrice){
        this.buyPrice = buyPrice;
        this.buyDate = buyDate;
    }
    public Double getProfit(boolean percent, double price){
        //wenn abgeschlossen
        if(this.soldDate != null && this.soldPrice != null){
            if(percent){
                return this.soldPrice / this.buyPrice * 100;
            }
            else {
                return this.soldPrice - this.buyPrice;
            }

        }
        if(percent){
            //vlt noch kürzen
            return (price / this.buyPrice) * 100;
        }
        else {
            return price - this.buyPrice;
        }
    }

    public void close(LocalDateTime soldDate, Double soldPrice){
        this.soldDate = soldDate;
        this.soldPrice = soldPrice;
        //0.0 bei Price wegen automatisch rechnen weil abgeschlossen
        if(this.getProfit(false,0.0) >= 0){
            this.win = true;
        }
        else {
            this.win = false;
        }
    }
}
