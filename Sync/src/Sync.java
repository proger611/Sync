import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Sync {
    private static final String url241 = "jdbc:mysql://192.168.0.241/smss";
    private static final String url242 = "jdbc:mysql://192.168.0.242/smss";
    private static final String user = "adm";
    private static final String pwd = "adm";
    private static final String dirLocation = "C:\\SMSC\\logs\\log";
    private static final String ENTER = System.getProperty("line.separator");

    private static final Logger log = LoggerFactory.getLogger(Sync.class);

    private static MOD mod = MOD.TEST;

    public static void main(String argv[]) {
        log.info("Start sync 242 -> 241...");

        setMod(MOD.TEST);
        log.info("MOD: {}", mod);

        moveMessages();
        //moveResponse();
        if (mod == MOD.WAR) {
            monitoringServiceRun();
        }

        //testMessages241();
        log.info("Done sync 242 -> 241... {}{} ", ENTER, ENTER);

        if (mod == MOD.TEST) {
            log.info("TEST ENTER");
        }
    }

    private static void setMod(MOD mod) {
        mod = mod;
    }

    public static void moveMessages() {
        log.info(" I. Start message sync");
        try {
            Connection connection242 = DriverManager.getConnection(url242, user, pwd);
            Connection connection241 = DriverManager.getConnection(url242, user, pwd);
            connection241.setAutoCommit(false);
            connection242.setAutoCommit(false);

            PreparedStatement stmt = connection242.prepareStatement("SELECT * from message where message_Time_Received >= ? and Message_Status = ? and message_text not like ? order by message_id");
            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now().toString()));
            stmt.setString(2, "S");
            stmt.setString(3, "%[%]");
            stmt.setMaxRows(300);

            ResultSet rs = stmt.executeQuery();

            List<Message> list = toMsg(rs);

            log.info("    + message count to sync: {}", list.size());

            stmt.close();
            rs.close();

            if (list.size() > 0) {

                if (mod == MOD.WAR) {
                    PreparedStatement stmtIsert = connection241.prepareStatement("INSERT into message " +
                            "(message_id, Provider_Id, phone_number, message_text, message_status, message_retries, message_time_received, " +
                            "user_id, sub_user_id, message_comment, patient_id, patient_surname, patient_first_name, patient_title, " +
                            "message_time_sent, message_IMSI_sent, sms_id, sms_delivery_status_id, sms_delivery_time, sms_count, " +
                            "patient_appt) " +
                            "values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, ?, null, null, ?, ?)");

                    for (Message x : list) {

                        stmtIsert.setString(1, null);
                        stmtIsert.setString(2, x.getPhone());
                        stmtIsert.setString(3, prepareMsgText(x));
                        stmtIsert.setString(4, x.getStatus());
                        stmtIsert.setInt(5, x.getRetries());
                        stmtIsert.setDate(6, x.getTimeReceived());
                        stmtIsert.setString(7, x.getUserId());
                        stmtIsert.setString(8, x.getSubUser());
                        stmtIsert.setString(9, x.getCmt());
                        stmtIsert.setString(10, x.getPatientId());
                        stmtIsert.setString(11, x.getPatientSurname());
                        stmtIsert.setString(12, x.getPatientFirstName());
                        stmtIsert.setString(13, x.getPatientTitle());
                        stmtIsert.setInt(14, x.getSmsId242());
                        stmtIsert.setInt(15, x.getSmsCount());
                        stmtIsert.setString(16, x.getPatientAppt());
                        stmtIsert.addBatch();
                    }

                    int[] msgIds = stmtIsert.executeBatch();

                    log.info("    + message insert to 241: {}", msgIds.length);

                    connection241.commit();

                    stmtIsert.close();

                    log.info("    + message insert to 241, COMMIT");

                } else {
                    for (Message x : list) {
                        log.info("    + TEST INSERT 241: {}", x);
                    }
                }

                //----------------------------------------------------

                if (mod == MOD.WAR) {

                     PreparedStatement stmtUpdate = connection242.prepareStatement("UPDATE message set message_status = ? where message_id = ?");

                    for (Message msg : list) {
                        stmtUpdate.setString(1, "S");
                        stmtUpdate.setLong(2, msg.getMessageId());
                        stmtUpdate.addBatch();
                    }

                    stmtUpdate.executeBatch();

                    log.info("    + message update to 242: {}", list.size());

                    connection242.commit();
                    stmtUpdate.close();

                    log.info("    + message UPDATE status 242, COMMIT");
                } else {
                    for (Message msg : list) {
                        log.info("    + TEST UPDATE: {}", msg);
                    }
                }
            }

            connection241.close();
            connection242.close();

        } catch (Exception e) {
            log.error("Message sync error! ", e);
        }

        log.info(" I. DONE message sync");
    }

    private static void moveResponse() {
        log.info(" II. Start response sync");
        try {
            Connection connection242 = DriverManager.getConnection(url242, user, pwd);
            Connection connection241 = DriverManager.getConnection(url242, user, pwd);
            connection241.setAutoCommit(false);
            connection242.setAutoCommit(false);

            PreparedStatement stmt = connection241.prepareStatement("SELECT * from message_reply where message_reply_Time_Received >= ? " +
                    "and message_reply_status = ? and message_text not like ? order by message_reply_id");

            stmt.setDate(1, java.sql.Date.valueOf(LocalDate.now().toString()));
            stmt.setString(2, "H");
            stmt.setMaxRows(100);

            ResultSet rs = stmt.executeQuery();

            List<Response> list = toResponse(rs);

            log.info("    + response count to sync: {}", list.size());

            stmt.close();
            rs.close();

            if (mod == MOD.WAR) {
                PreparedStatement stmtIsert = connection242.prepareStatement("INSERT into message_reply " +
                        "(message_reply_id, message_reply_to, message_reply_from, message_reply_text, message_status," +
                        " message_reply_time_received, message_reply_message_id) " +
                        "values (null, ?, ?, ?, ?, ?, ?)");

                for (Response x : list) {

                    stmtIsert.setString(1, "serv_241");
                    stmtIsert.setString(2, x.getReplyFrom());
                    stmtIsert.setString(2, x.getReplyMsg());
                    stmtIsert.setString(2, "P");
                    stmtIsert.setDate(2, x.getReplyTimeReceived());
                    stmtIsert.setLong(2, x.getMessageId());
                    stmtIsert.addBatch();
                }

                int[] updateResp = stmtIsert.executeBatch();
                log.info("    + RESPONSE INSERT to 242: {}", updateResp.length );

                connection242.commit();
                stmtIsert.close();

                log.info("    + RESPONSE INSERT to 242, COMMIT");

            } else {
                for (Response msg : list) {
                    log.info("    + TEST RESPONSE INSERT to 242", msg);
                }
            }


            //---

            if (mod == MOD.WAR) {
                PreparedStatement stmtUpdate = connection241.prepareStatement("UPDATE message_reply set message_status = ? where message_reply_id = ?");

                for (Response msg : list) {
                    stmtUpdate.setString(1, "I");
                    stmtUpdate.setLong(2, msg.getMessageId());
                    stmtUpdate.addBatch();
                }

                int[] updateResp = stmtUpdate.executeBatch();
                log.info("    + RESPONSE UPDATE status 241: {}", updateResp.length);

                connection241.commit();
                stmtUpdate.close();

                log.info("    + RESPONSE UPDATE status 241, COMMIT");
            } else {
                for (Response msg : list) {
                    log.info("    + RESPONSE UPDATE status 241", msg);
                }
            }

            connection241.close();
            connection242.close();

        }catch (Exception e) {
            log.error("Response sync error! ", e);
        }

        log.info(" II. DONE response sync");
    }

    public static void testMessages241() {
        log.info("I. TEST: Start message sync");

        try (Connection connection = DriverManager.getConnection(url241, user, pwd)) {
            //connection.setAutoCommit(false);

            Message msg = new Message();
            msg.setMessageId(100l);
            msg.setPhone("79037794311");
            msg.setMsg("Привет link opros " + LocalDateTime.now().getSecond());
            msg.setStatus("N");
            msg.setRetries(0);
            msg.setTimeReceived(java.sql.Date.valueOf(LocalDate.now().toString()));
            msg.setUserId("slw");
            msg.setSubUser("slw");
            msg.setPatientId("patient_id");
            msg.setPatientSurname("patient_surname");
            msg.setPatientFirstName("patient_first_name");
            msg.setPatientTitle("patient_title");
            msg.setSmsCount(0);
            msg.setPatientAppt("patient_appt");
            msg.setSourceId(0);

            List<Message> list = Arrays.asList(msg);

            log.info("    + message count to sync: {}", list.size());

            PreparedStatement stmtIsert = connection.prepareStatement("INSERT into message " +
                    "(message_id, Provider_Id, phone_number, message_text, message_status, message_retries, message_time_received, " +
                    "user_id, sub_user_id, message_comment, patient_id, patient_surname, patient_first_name, patient_title, " +
                    "message_time_sent, message_IMSI_sent, sms_id, sms_delivery_status_id, sms_delivery_time, sms_count, " +
                    "patient_appt) " +
                    "values (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, null, null, ?, null, null, ?, ?)");

            stmtIsert.setString(1, null);
            stmtIsert.setString(2, msg.getPhone());
            stmtIsert.setString(3, prepareMsgText(msg));
            stmtIsert.setString(4, msg.getStatus());
            stmtIsert.setInt(5, msg.getRetries());
            stmtIsert.setDate(6, msg.getTimeReceived());
            stmtIsert.setString(7, msg.getUserId());
            stmtIsert.setString(8, msg.getSubUser());
            stmtIsert.setString(9, msg.getCmt());
            stmtIsert.setString(10, msg.getPatientId());
            stmtIsert.setString(11, msg.getPatientSurname());
            stmtIsert.setString(12, msg.getPatientFirstName());
            stmtIsert.setString(13, msg.getPatientTitle());
            stmtIsert.setLong(14, msg.getSmsId242());
            stmtIsert.setInt(15, msg.getSmsCount());
            stmtIsert.setString(16, msg.getPatientAppt());

            stmtIsert.execute();

            log.info("  + insert OK messageId: {}", msg.getSmsId242());

            stmtIsert.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("   TEST: DONE message sync");
    }

    private static void monitoringServiceRun() {
        try {
            List<File> files = Files.list(Paths.get(dirLocation))
                    .map(Path::toFile)
                    .collect(Collectors.toList());

            if (files.size() < 18) {
                log.info("send sms");

                URL yahoo = new URL("http://smsc.ru/sys/send.php?login=centaur&psw=centaurapi&phones=79037794311&mes=WARN: Service sleep!");
                URLConnection yc = yahoo.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
                String inputLine = null;

                while ((inputLine = in.readLine()) != null)
                    log.info(inputLine);
                in.close();
            } else {
                log.info(" + file list: {}", files.size());
            }
        } catch (Exception e) {
            log.error("Monitoring service error: {}", e);
        }
    }

    private static List<Response> toResponse(ResultSet rs) throws SQLException {
        List<Response> list = new ArrayList<>();
        while (rs.next()) {
            Response msg = new Response();
            msg.setReplyId(rs.getLong("message_reply_id"));
            msg.setReplyTo(rs.getString("message_reply_to"));
            msg.setReplyFrom(rs.getString("message_reply_from"));
            msg.setReplyMsg(rs.getString("message_reply_text"));
            msg.setReplyStatus(rs.getString("message_reply_status"));
            msg.setReplyTimeReceived(rs.getDate("message_reply_time_received"));
            msg.setMessageId(rs.getLong("message_reply_message_id"));

            list.add(msg);
        }

        return list;
    }

    private static List<Message> toMsg(ResultSet rs) throws SQLException {
        List<Message> list = new ArrayList<>();
        while (rs.next()) {
            Message msg = new Message();
            msg.setMessageId(rs.getLong("message_id"));
            msg.setSmsId242(rs.getInt("sms_id"));
            msg.setPhone(rs.getString("phone_number"));
            msg.setMsg(rs.getString("message_text"));
            msg.setStatus(rs.getString("message_status"));
            msg.setRetries(rs.getInt("message_retries"));
            msg.setTimeReceived(rs.getDate("message_time_received"));
            msg.setUserId(rs.getString("user_id"));
            msg.setSubUser(rs.getString("sub_user_id"));
            msg.setPatientId(rs.getString("patient_id"));
            msg.setPatientSurname(rs.getString("patient_surname"));
            msg.setPatientFirstName(rs.getString("patient_first_name"));
            msg.setPatientTitle(rs.getString("patient_title"));
            msg.setSmsCount(rs.getInt("sms_count"));
            msg.setPatientAppt(rs.getString("patient_appt"));
            msg.setSourceId(rs.getInt("source_id"));

            list.add(msg);
        }

        return list;
    }

    private static String prepareMsgText(Message msg) {
        return msg.getMsg().replaceAll("link", "http://d4w.su/" + Long.toHexString(msg.getMessageId()))
                .replaceAll("opros", "http://d4w.su/o/" + Long.toHexString(msg.getMessageId()));
    }

    public static enum MOD {
        TEST, WAR;
    }
}
