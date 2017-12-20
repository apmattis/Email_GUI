package HomePage;

import java.util.ArrayList;
import java.util.HashMap;

public class Messages {

    private ArrayList<String> messageNames;
    private StringBuilder messageContents;
    private HashMap<String, StringBuilder> messageMap;

    public Messages(){
        setMessageContents(new StringBuilder());
        setMessageNames(new ArrayList<>());
        setMessageMap(new HashMap<>());
    }

    public boolean containsMessageName(String name){
        return messageNames.contains(name);
    }

    public void addMessageToMap(String msg, StringBuilder msgCont){
        getMessageMap().put(msg, msgCont);
    }

    public void addMessage(String msg){
        getMessageNames().add(msg);
    }

    public void removeMessageIdx(int i){
        getMessageNames().remove(i);
    }

    public void removeMessage(String msg){
        getMessageNames().remove(msg);
    }

    public StringBuilder getMessageContents() {
        return messageContents;
    }

    public void setMessageContents(StringBuilder messageContents) {
        this.messageContents = messageContents;
    }

    public String getMessage(String name){
        return messageNames.get(messageNames.indexOf(name));
    }

    public ArrayList<String> getMessageNames() {
        return messageNames;
    }

    public void setMessageNames(ArrayList<String> messageNames) {
        this.messageNames = messageNames;
    }

    public HashMap<String, StringBuilder> getMessageMap() {
        return messageMap;
    }

    public void setMessageMap(HashMap<String, StringBuilder> messageMap) {
        this.messageMap = messageMap;
    }
}
