import java.sql.Date;

public class Response {

    private Long replyId;
    private String replyFrom;

    public Long getReplyId() {
        return replyId;
    }

    public void setReplyId(Long replyId) {
        this.replyId = replyId;
    }

    public String getReplyFrom() {
        return replyFrom;
    }

    public void setReplyFrom(String replyFrom) {
        this.replyFrom = replyFrom;
    }

    public String getReplyTo() {
        return replyTo;
    }

    public void setReplyTo(String replyTo) {
        this.replyTo = replyTo;
    }

    public String getReplyMsg() {
        return replyMsg;
    }

    public void setReplyMsg(String replyMsg) {
        this.replyMsg = replyMsg;
    }

    public String getReplyStatus() {
        return replyStatus;
    }

    public void setReplyStatus(String replyStatus) {
        this.replyStatus = replyStatus;
    }

    public Date getReplyTimeReceived() {
        return replyTimeReceived;
    }

    public void setReplyTimeReceived(Date replyTimeReceived) {
        this.replyTimeReceived = replyTimeReceived;
    }

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    private String replyTo;
    private String replyMsg;

    @Override
    public String toString() {
        return "Response{" +
                "replyId=" + replyId +
                ", replyFrom='" + replyFrom + '\'' +
                ", replyTo='" + replyTo + '\'' +
                ", replyMsg='" + replyMsg + '\'' +
                ", replyStatus='" + replyStatus + '\'' +
                ", replyTimeReceived=" + replyTimeReceived +
                ", messageId=" + messageId +
                '}';
    }

    private String replyStatus;
    private Date replyTimeReceived;
    private Long messageId;
}
