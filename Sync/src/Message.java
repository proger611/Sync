import java.sql.Date;
import java.time.LocalDate;

public class Message {
    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getRetries() {
        return retries;
    }

    public void setRetries(int retries) {
        this.retries = retries;
    }

    public Date getTimeReceived() {
        return timeReceived;
    }

    public void setTimeReceived(Date timeReceived) {
        this.timeReceived = timeReceived;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSubUser() {
        return subUser;
    }

    public void setSubUser(String subUser) {
        this.subUser = subUser;
    }

    public String getCmt() {
        return cmt;
    }

    public void setCmt(String cmt) {
        this.cmt = cmt;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientSurname() {
        return patientSurname;
    }

    public void setPatientSurname(String patientSurname) {
        this.patientSurname = patientSurname;
    }

    public String getPatientFirstName() {
        return patientFirstName;
    }

    public void setPatientFirstName(String patientFirstName) {
        this.patientFirstName = patientFirstName;
    }

    public String getPatientTitle() {
        return patientTitle;
    }

    public void setPatientTitle(String patientTitle) {
        this.patientTitle = patientTitle;
    }

    public int getSmsId242() {
        return smsId242;
    }

    public void setSmsId242(int smsId242) {
        this.smsId242 = smsId242;
    }

    public int getSmsCount() {
        return smsCount;
    }

    public void setSmsCount(int smsCount) {
        this.smsCount = smsCount;
    }

    public String getPatientAppt() {
        return patientAppt;
    }

    public void setPatientAppt(String patientAppt) {
        this.patientAppt = patientAppt;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public String toString() {
        return "Message{" +
                "messageId=" + messageId +
                ", phone=" + phone +
                ", msg='" + msg + '\'' +
                ", status='" + status + '\'' +
                ", retries=" + retries +
                ", timeReceived=" + timeReceived +
                ", userId='" + userId + '\'' +
                ", subUser='" + subUser + '\'' +
                ", cmt='" + cmt + '\'' +
                ", patientId=" + patientId +
                ", patientSurname='" + patientSurname + '\'' +
                ", patientFirstName='" + patientFirstName + '\'' +
                ", patientTitle='" + patientTitle + '\'' +
                ", smsId242=" + smsId242 +
                ", smsCount=" + smsCount +
                ", patientAppt='" + patientAppt + '\'' +
                ", sourceId=" + sourceId +
                '}';
    }

    private Long messageId;  //1
    private String phone; //3
    private String msg; //4
    private String status; //5
    private int retries; //6
    private Date timeReceived; //7
    private String userId; //8
    private String subUser; //9
    private String cmt; //10
    private String patientId; //11
    private String patientSurname; //12
    private String patientFirstName; //13
    private String patientTitle; //14
    private int smsId242; //17
    private int smsCount; //19
    private String patientAppt; //20
    private int sourceId; //21

}
