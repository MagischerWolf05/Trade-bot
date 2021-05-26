import org.json.simple.*;
public class Condition {
    Condition(String option1, String operator, String option2){
        this.option1 = option1;

        this.operator = operator;
        this.option2 = option2;
        //mabe parsen und dann einen boolean als property der "numeric" heisst umsetzen also auf true
    }
    public String option1 = "0";
    public String option2 = "0";
    public boolean isOption1Numeric = false;
    public boolean isOption2Numeric = false;
    public String operator = "=";
    //im achieved die Daten reingeben
    public boolean achieved(Double option1Value,Double option2Value){
        //Wenn numeric ist dann den value selber nehmen
        //optionValue argument ist ja dann eh null wenn es das nicht findet in den Daten.
        Double option1val = option1Value;
        Double option2val = option2Value;
        //Wenn es eine Nummer ist muss es mit .0 geschrieben werden unbedingt
        if (isOption1Numeric){
            option1val =Double.parseDouble(this.option1);
        }
        if(isOption2Numeric){
            option2val = Double.parseDouble(this.option2);
        }

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