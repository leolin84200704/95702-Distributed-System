package ds;

import java.util.HashMap;

/*
// Name: Leo Lin
// AndrewID: hungfanl
 */
public class AnswerClickerModel {
    // Create a map for data storage
    HashMap<String, Integer> answer = new HashMap<>();
    AnswerClickerModel(){
        answer.put("A",0);
        answer.put("B",0);
        answer.put("C",0);
        answer.put("D",0);
    }
    public void addNAnswer(String c){
        answer.put(c, answer.get(c)+1);
    }
}
