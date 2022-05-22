package yilmazturk.alper.myroadfriend_bag_742.Model;

public class Chat {
    private String senderID, receiverID, message,date,time;
    private boolean seen;

    public Chat() {
    }

    public Chat(String senderID, String receiverID, String message, String date, String time, boolean seen) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.date = date;
        this.time = time;
        this.seen = seen;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }
}
