public class Strategy {
    //Vielleicht später noch mehr conditions
    public Condition buyCondition;
    public Condition sellCondition;

    public int stopLoss;
    //true = Dollar false = %
    public boolean stopLossType;

    public int positionSizing;
    public boolean positionSizingType;

    Strategy(Condition buyCondition, Condition sellCondition, String StopLoss,String Positionsizing){
        this.buyCondition = buyCondition;
        this.sellCondition = sellCondition;

        if(StopLoss.endsWith("$")){
            this.stopLossType = true;
        }
        else if(StopLoss.endsWith("%")){
            this.stopLossType = false;
        }
        this.stopLoss =Integer.parseInt( StopLoss.substring(0,StopLoss.length()-2) );

        if(Positionsizing.endsWith("$")){
            this.positionSizingType = true;
        }
        else if(Positionsizing.endsWith("%")){
            this.positionSizingType = false;
        }
        this.positionSizing =Integer.parseInt( Positionsizing.substring(0,StopLoss.length()-2) );

    }
}