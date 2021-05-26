public class Strategy {
    //Vielleicht später noch mehr conditions
    public Condition buyCondition;
    public Condition sellCondition;

    public int stopLoss;
    //true = % false = Dollar
    public boolean stopLossType;

    public Double positionSizing;
    public boolean positionSizingDollar;

    Strategy(Condition buyCondition, Condition sellCondition, String StopLoss,String Positionsizing){
        this.buyCondition = buyCondition;
        this.sellCondition = sellCondition;

        if(StopLoss.endsWith("$")){
            this.stopLossType = false;
        }
        else if(StopLoss.endsWith("%")){
            this.stopLossType = true;
        }
        this.stopLoss =Integer.parseInt( StopLoss.substring(0,StopLoss.length()-1) );

        if(Positionsizing.endsWith("$")){
            this.positionSizingDollar = true;
        }
        else if(Positionsizing.endsWith("%")){
            this.positionSizingDollar = false;
        }
        //Testen
        this.positionSizing =Double.parseDouble( Positionsizing.substring(0,StopLoss.length()-1) );

    }
}