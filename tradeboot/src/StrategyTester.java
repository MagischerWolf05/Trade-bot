import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.FileWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class StrategyTester {
    //public boolean tester = false;
    public String StockDataFolder = "C:\\Users\\Remo Wälchli\\OneDrive - bbw.ch\\ZLI\\Wochen\\Trade-bot\\tradeboot\\src\\StockData\\";
    public String interval = "15min";
    public ArrayList<String> apiKeys = new ArrayList<String>(Arrays.asList("R7JDRYCEZE5HRLMO","CSJTLGI49VWMSGST")) ;
    public HashMap<String,JSONObject> data = new HashMap<String,JSONObject>();
    public HashMap<LocalDateTime,Double> balanceHistory = new HashMap<LocalDateTime,Double>();

    public Double accessibleBalance;
    public Double balance;
    public Double initialBalance;
    public Strategy strategy;

    public ArrayList<Trade> trades = new ArrayList<Trade>();
    public ArrayList<Trade> outstandingTrades = new ArrayList<Trade>();

    StrategyTester(Strategy strat, Double balance){
        this.balance = balance;
        this.accessibleBalance = balance;
        this.initialBalance = balance;
        this.strategy = strat;

        this.loadData();

        LocalDateTime start = sortData(true);
        LocalDateTime end = sortData(false);

        LocalDateTime timeIndex = start;
        boolean finished = false;
        while (!finished){
            //interval
            timeIndex = timeIndex.plusMinutes(15);
            balanceHistory.put(timeIndex,this.balance);
            //formatTimeIndex gibt
            JSONObject buyOption1Node = getNodeByTime(timeIndex,this.strategy.buyCondition.option1);
            JSONObject buyOption2Node = getNodeByTime(timeIndex,this.strategy.buyCondition.option2);
            JSONObject sellOption1Node = getNodeByTime(timeIndex,this.strategy.sellCondition.option1);
            JSONObject sellOption2Node = getNodeByTime(timeIndex,this.strategy.sellCondition.option2);
            JSONObject priceNode = getNodeByTime(timeIndex,"Preis");

            if(buyOption1Node != null && buyOption2Node != null && sellOption1Node != null && sellOption2Node != null && priceNode != null){

                String buyOption1NodeContentIndex =  getNodeContentIndex(this.strategy.buyCondition.option1);
                String buyOption2NodeContentIndex =  getNodeContentIndex(this.strategy.buyCondition.option2);
                String sellOption1NodeContentIndex =  getNodeContentIndex(this.strategy.sellCondition.option1);
                String sellOption2NodeContentIndex =  getNodeContentIndex(this.strategy.sellCondition.option2);
                String priceNodeContentIndex = getNodeContentIndex("Preis");

                Double price =Double.parseDouble((String) priceNode.get(priceNodeContentIndex)) ;



                boolean sell = this.strategy.sellCondition.achieved(Double.parseDouble((String)sellOption1Node.get(sellOption1NodeContentIndex)) , Double.parseDouble((String)sellOption2Node.get(sellOption2NodeContentIndex)) );
                if(sell && this.outstandingTrades.size() != 0){
                    for (int i = 0;i < this.outstandingTrades.size(); i++) {
                        Trade trade = this.outstandingTrades.get(i);
                        trade.close(timeIndex,price);
                        this.accessibleBalance += trade.getProfit(false,0.0) + trade.size;

                        this.trades.add(trade);
                        this.outstandingTrades.remove(i);
                        i--;
                    }
                }

                //stop loss
                for (int i = 0; i<this.outstandingTrades.size();i++) {
                    Trade trade = this.outstandingTrades.get(i);
                    //profit in percentage oder Dollar je nachdem
                    Double profit = trade.getProfit(this.strategy.stopLossType,price);
                    if (profit <= (this.strategy.stopLoss * -1) ){
                        trade.close(timeIndex,price);
                        //gets dollar profit amount at closed price
                        this.accessibleBalance += trade.getProfit(false,0.0) + trade.size;

                        this.trades.add(trade);
                        this.outstandingTrades.remove(i);
                        i--;
                    }
                }

                //this.balance ist die Balance total also balance in Trades und freie balance also totaler Wert
                this.updateFluidBalance(price);


                boolean buy = this.strategy.buyCondition.achieved(Double.parseDouble((String)buyOption1Node.get(buyOption1NodeContentIndex)) ,Double.parseDouble((String)buyOption2Node.get(buyOption2NodeContentIndex)) );
                if(buy){
                    //hier kaufen
                    Double positionSize;
                    if(this.strategy.positionSizingDollar){
                        positionSize = this.strategy.positionSizing;
                    }
                    else {
                        positionSize = (this.balance / 100) * this.strategy.positionSizing;
                    }

                    if(this.accessibleBalance - positionSize >= 0){
                        //accessible balance ist das geld nicht in Trades
                        this.accessibleBalance -= positionSize;
                        this.outstandingTrades.add(new Trade(timeIndex,price,positionSize));
                    }
                }



                if(timeIndex.equals(end)){
                    finished = true;
                    for (int i = 0;i < this.outstandingTrades.size(); i++) {
                        Trade trade = this.outstandingTrades.get(i);
                        trade.close(timeIndex,price);

                        //gets dollar profit amount at closed price
                        this.accessibleBalance += trade.getProfit(false,0.0) + trade.size;

                        this.trades.add(trade);
                        this.outstandingTrades.remove(i);
                        i--;
                    }
                }

            }

        }
    }

    public void loadData(){
        HashMap<String,String> functions = new HashMap<String,String>();
        //ArrayList<String>functions = new ArrayList<String>();
        functions.put(this.strategy.sellCondition.option1,getAPIFunction(this.strategy.sellCondition.option1));
        functions.put(this.strategy.sellCondition.option2,getAPIFunction(this.strategy.sellCondition.option2));
        functions.put(this.strategy.buyCondition.option1,getAPIFunction(this.strategy.sellCondition.option1));
        functions.put(this.strategy.buyCondition.option2,getAPIFunction(this.strategy.buyCondition.option2));
        functions.put("Preis",getAPIFunction("Preis"));
        //removes same ones
        for (int i = 0;i < functions.size();i++) {
            String condition = functions.get(i);
            try{
                Double.parseDouble(condition);
                functions.remove(i);
                i--;
                if(condition.equals("Profit")){

                }
                //if profit dann einfach vom trade unterschied nehmen bei tr4ade getProfit und dann true oder false für % oder $
                //sowiso noch profit extra mitnehmen in data
            }
            catch (Exception e){
                //Wenn Option ist oder kein Double geparsed werden konnte
                for (int j = 0;j<functions.size();j++) {
                    String condition2 = functions.get(j);
                    if (condition == condition2);{
                        functions.remove(j);
                    }
                }
            }
        }
        //Doubles are now removed
        String start = "https://www.alphavantage.co/query?";
        String apiKeyPrefix = "&apikey=";
        int count = 0;
        for (Map.Entry<String,String> call: functions.entrySet()) {

            String url =start +  call.getValue() + "&interval=" + this.interval + apiKeyPrefix + this.apiKeys.get(count % this.apiKeys.size());
            count ++;
            //&symbol=IBM
            JSONObject data = getData(url,call.getKey(),"IBM");
            this.data.put(call.getKey(),data);
        }
        int i = 0;

        //vo functions key der index / key vom JSON object zB Technical Analysis: RSI um die Daten zu bekommen oder einfach als .get(1)??
        //if two are the same then nah delete because the key in the hashmap is the option so it would anyways be the sam ebut two api calls
    }

    public String getAPIFunction(String option){
        //hier kommen die ausgewählten Values
        if(option.equals("14 Day RSI")){
            return "function=RSI&time_period=14&interval=daily&series_type=open";
        }
        if(option.equals("Avg Preis Stunde")){
            return "function=SMA&time_period=4&series_type=open" + this.interval;
        }
        if(option.equals("Avg Preis 5 Stunden")){
            return "function=SMA&time_period=20&series_type=open" + this.interval;
        }
        if(option.equals("Avg Preis 10 Tage")){
            return "function=SMA&time_period=10&interval=daily&series_type=open";
        }
        if(option.equals("Avg Preis 24H")){
            return "function=SMA&time_period=96&series_type=open" + this.interval;
        }
        if(option.equals("Avg Preis 7 Tage")){
            return "function=SMA&time_period=7&interval=daily&series_type=open";
        }
        if(option.equals("Preis")){
            return "function=TIME_SERIES_INTRADAY" + this.interval + "&outputsize=full";
        }
        //fails
        return null;
    }

    public LocalDateTime sortData(boolean fromBottom){
        //for (Map.Entry<String,JSONObject> e:this.data.entrySet()) {
          //  if (e.getValue().size() == 1){
            //    return;
            //}
        //}
        //in der metadata kann herausgelesen werden welcher intervals also einfach dann erste
        LocalDateTime timeIndex;
        if(fromBottom){
            timeIndex = LocalDateTime.now().minusMonths(2);
        }
        else {
            timeIndex = LocalDateTime.now();
        }
        //Interval
        timeIndex = timeIndex.minusMinutes(timeIndex.getMinute());
        timeIndex = timeIndex.plusMinutes(15);
        timeIndex = timeIndex.minusSeconds(timeIndex.getSecond());
        timeIndex = timeIndex.minusNanos(timeIndex.getNano());
        boolean matched = false;
        String lastInterval = "";

        while (!matched){
            if (lastInterval.equals("daily")){
                if (fromBottom){
                    timeIndex = timeIndex.plusDays(1);
                    timeIndex = timeIndex.minusHours(timeIndex.getHour());
                    timeIndex = timeIndex.minusMinutes(timeIndex.getMinute());
                }
                else {
                    timeIndex = timeIndex.minusDays(1);
                    timeIndex = timeIndex.minusHours(timeIndex.getHour());
                    timeIndex = timeIndex.minusMinutes(timeIndex.getMinute());
                    timeIndex = timeIndex.plusHours(23);
                    timeIndex = timeIndex.plusMinutes(45);

                }

            }
            else {
                if (fromBottom){
                    //Inerval
                    timeIndex = timeIndex.plusMinutes(15);
                }
                else {
                    //Interval
                    timeIndex = timeIndex.minusMinutes(15);
                }

            }
            try {
                for (Map.Entry<String,JSONObject> data: this.data.entrySet()) {
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    String formattedTimeIndex = timeIndex.format(myFormatObj);
                    String index = formattedTimeIndex;


                    JSONObject meta =(JSONObject) data.getValue().get("Meta Data");
                    String timelineIndex = this.getDataIndexFromIndicator(data.getKey());
                    JSONObject timeline =(JSONObject) data.getValue().get(timelineIndex);
                    //Wenn es Tag ist dann wird das Datum umgeändert in Tag siehe getNodeByTime
                    //wieder entweder mit . oder mit :
                    if(meta.get("4: Interval") != null){
                        lastInterval =(String) meta.get("4: Interval");
                    }
                    else if(meta.get("4. Interval") != null){
                        lastInterval =(String) meta.get("4. Interval");
                    }




                    JSONObject dataNode = this.getNodeByTime(timeIndex,data.getKey());
                    if (dataNode == null){
                        throw new Exception("Not matched");
                    }
                    else {
                        int a = 2;
                    }

                    //indicator holen und dann die Datenwerte / Timeline holen

                    //dataObject.get("Meta Data").get("2: Indicator");
                    // das zwischen den Klammen holen und dann "Technical Analysis: " + dasZwischenDenKlammen

                    //oder halt Time Series (dasWasInMetaDataStehtInterval) wenn es kein indicator gibt in Meta Data

                    //timeline mit timeindex Tag holen wenn in Metadata Interval daily ist
                    //Timeline mit timeIndex holen.
                }
                matched = true;
                return timeIndex;
                //hier ist timeline gefunden von allen
                //cutoff vor dieser zeit um durch zu cyclen dass alle gleich beginnen
            }
            catch (Exception e){
                e.toString();
            }
        }
        return null;
    }

    public void updateFluidBalance(Double price){
        for (Trade trade:this.outstandingTrades) {
            this.balance += trade.getProfit(false,price);
        }
    }

    public void updateAccessibleBalance(){
        Double balance = 0.0;
        for (Trade trade: this.outstandingTrades){
            balance += trade.size;
        }
        this.accessibleBalance = balance;
    }

    public JSONObject APIcall(String URLString){
        try{
            URL url = new URL(URLString);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responsecode = conn.getResponseCode();
            //Getting the response code
            if (responsecode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responsecode);
            } else {

                String inline = "";
                Scanner scanner = new Scanner(url.openStream());

                //Write all the JSON data into a string using a scanner
                while (scanner.hasNext()) {
                    inline += scanner.nextLine();
                }

                //Close the scanner
                scanner.close();

                //Using the JSON simple library parse the string into a json object
                JSONParser parse = new JSONParser();
                JSONObject data =  (JSONObject) parse.parse(inline);
                return data;
            }
        }
        catch(Exception e){

        }
        return null;

    }

    public String getDataIndexFromIndicator(String indicator){
        if(indicator.equals("14 Day RSI")){
            return "Technical Analysis: RSI";
        }
        if(indicator.equals("Avg Preis 24H") || indicator.equals("Avg Preis Stunde") || indicator.equals("Avg Preis 5 Stunden") || indicator.equals("Avg Preis 10 Tage") || indicator.equals("Avg Preis 7 Tage")){
            return "Technical Analysis: SMA";
        }
        if(indicator.equals("Preis")){
            return "Time Series (" + this.interval + ")";
        }
        return "";
    }

    public JSONObject getData(String url, String option, String symbol){
        //takes from json if exists and if not it renews
        //if last date from json is more than a week away it refreshes
        String inline = "";
        File jsonFile = new File( this.StockDataFolder+ symbol + " " + option + ".json");
        try {
            Scanner scanner = new Scanner(jsonFile);
            while (scanner.hasNext()) {
                inline += scanner.nextLine();
            }
            scanner.close();
            JSONParser parse = new JSONParser();
            try{
                return (JSONObject) parse.parse(inline);
            }
            catch (Exception e){
                System.out.println(e.getMessage());
            }
        }
        catch (Exception e){
            //Kein File gefunden
            APIcall(url + "&symbol=" + symbol);
        }
        return null;
    }

    public String getNodeContentIndex(String option){
        //hier kommen die ausgewählten Values
        if(option.equals("14 Day RSI")){
                return "RSI";
        }
        if(option.equals("Avg Preis Stunde") || option.equals("Avg Preis 5 Stunden") || option.equals("Avg Preis 10 Tage") || option.equals("Avg Preis 24H") || option.equals("Avg Preis 7 Tage")){
            return "SMA";
        }

        if(option.equals("Preis")){
            return "4. close";
        }
        //fails
        return null;
    }

    public JSONObject getNodeByTime(LocalDateTime unformatted, String option){
        String timeLineIndex = this.getDataIndexFromIndicator(option);
        JSONObject timeline =(JSONObject) this.data.get(option).get(timeLineIndex);
        JSONObject meta =(JSONObject) this.data.get(option).get("Meta Data");
        String nodeIndex = unformatted.toString();
        DateTimeFormatter myFormatObj;

        if ( (meta.get("4: Interval") != null && meta.get("4: Interval").equals("daily")) || (meta.get("4. Interval") != null && meta.get("4. Interval").equals("daily"))){
            //2021-05-19 17:55:00 Format normal, 2021-05-19 Format daily
            myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            nodeIndex = unformatted.format(myFormatObj);
            //index = formattedTimeIndex.substring(0,timeIndex.toString().length()-9);
        }
        //bei Intraday data beginnen sachen mit 1. also mit punkt nicht mit :
        if(meta.get("1. Information") != null && ((String)meta.get("1. Information")).startsWith("Intraday") ){
            myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            nodeIndex= unformatted.format(myFormatObj);
        }

        return (JSONObject) timeline.get(nodeIndex);
    }

}
