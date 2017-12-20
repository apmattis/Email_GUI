package HomePage;

public class Message {
    private String name;
    private StringBuilder contents;

    public Message(){
        setName(null);
        setContents(new StringBuilder());
    }

    public Message(String name){
        this.setName(name);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public StringBuilder getContents() {
        return contents;
    }

    public void setContents(StringBuilder contents) {
        this.contents = contents;
    }
}
