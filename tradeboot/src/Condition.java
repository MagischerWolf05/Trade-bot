import org.json.simple.*;
public class Condition {
    Condition(String option1, String operator, String option2){
        this.option1 = option1;

        this.operator = operator;
        this.option2 = option2;
    }
    public String option1 = "0";
    public String option2 = "0";
    public String operator = "=";
    //im achieved die Daten reingeben
    public boolean achieved(Double option1Value,Double option2Value){
        Double option1val = option1Value;
        Double option2val = option2Value;
        if(this.operator == "="){
            return option1val == option2val;
        }
        else if(this.operator == "<"){
            return option1val < option2val;
        }
        else if(this.operator == ">"){
            return option1val < option2val;
        }
        //Sollte nicht hier rankommen
        return false;
    }

}