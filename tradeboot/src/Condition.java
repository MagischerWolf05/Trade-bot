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
    public boolean achieved(JSONObject data){
        Double option1val = (Double) data.get(this.option1);
        Double option2val = (Double) data.get(this.option2);
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