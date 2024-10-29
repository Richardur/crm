package model;

import android.os.Parcel;
import android.os.Parcelable;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Task implements Parcelable {
    private String reactionHeaderID;
    private String reactionHeaderManagerID;
    private String workInPlanForCustomerID;
    private String workInPlanForCustomerOrder;
    private int workInPlanID;
    private String reactionWorkManagerID;
    private String managerName;
    private String reactionWorkDoneByID;
    private String reactionWorkDoneByName;
    private String reactionWorkActionID;
    private String workInPlanName;
    private String workInPlanNote;
    private String workInPlanForCustomerName;
    private Timestamp workInPlanTerm;
    private Timestamp workInPlanDoneDate;
    private String workInPlanDone;
    private boolean isDateOnlyAction;

    // Representative details
    private String repName;
    private String repSurname;
    private String repPhone;
    private String repEmail;


    public boolean isDateOnlyAction() {
        return isDateOnlyAction;
    }

    public void setDateOnlyAction(boolean dateOnlyAction) {
        isDateOnlyAction = dateOnlyAction;
    }

    public Task(String reactionHeaderID, String reactionHeaderManagerID, String workInPlanForCustomerID,
                String workInPlanForCustomerOrder, int workInPlanID, String reactionWorkManagerID,
                String managerName, String reactionWorkDoneByID, String reactionWorkDoneByName, String reactionWorkActionID, String workInPlanName, String workInPlanNote,
                String workInPlanForCustomerName, Timestamp workInPlanTerm, Timestamp workInPlanDoneDate, String workInPlanDone, boolean isDateOnlyAction) {
        this.reactionHeaderID = reactionHeaderID;
        this.reactionHeaderManagerID = reactionHeaderManagerID;
        this.workInPlanForCustomerID = workInPlanForCustomerID;
        this.workInPlanForCustomerOrder = workInPlanForCustomerOrder;
        this.workInPlanID = workInPlanID;
        this.reactionWorkManagerID = reactionWorkManagerID;
        this.managerName = managerName;
        this.reactionWorkDoneByID = reactionWorkDoneByID;
        this.reactionWorkDoneByName = reactionWorkDoneByName;
        this.reactionWorkActionID = reactionWorkActionID;
        this.workInPlanName = workInPlanName;
        this.workInPlanNote = workInPlanNote;
        this.workInPlanForCustomerName = workInPlanForCustomerName;
        this.workInPlanTerm = workInPlanTerm;
        this.workInPlanDoneDate = workInPlanDoneDate;
        this.workInPlanDone = workInPlanDone;
        this.isDateOnlyAction = isDateOnlyAction;

    }

    protected Task(Parcel in) {
        reactionHeaderID = in.readString();
        reactionHeaderManagerID = in.readString();
        workInPlanForCustomerID = in.readString();
        workInPlanForCustomerOrder = in.readString();
        workInPlanID = in.readInt();
        reactionWorkManagerID = in.readString();
        managerName = in.readString();
        reactionWorkDoneByID = in.readString();
        reactionWorkDoneByName = in.readString();
        reactionWorkActionID = in.readString();
        workInPlanName = in.readString();
        workInPlanNote = in.readString();
        workInPlanForCustomerName = in.readString();
        workInPlanTerm = in.readByte() == 0 ? null : new Timestamp(in.readLong());
        workInPlanDoneDate = in.readByte() == 0 ? null : new Timestamp(in.readLong());
        workInPlanDone = in.readString();
        repName = in.readString();
        repSurname = in.readString();
        repPhone = in.readString();
        repEmail = in.readString();

    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };



    public void setWorkInPlanDone(String workInPlanDone) {
        this.workInPlanDone = workInPlanDone;
    }

    public void setWorkInPlanDoneDate(String workInPlanDoneDateStr) {
        if (workInPlanDoneDateStr == null) {
            this.workInPlanDoneDate = null;
        } else {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                Date date = sdf.parse(workInPlanDoneDateStr);
                this.workInPlanDoneDate = new Timestamp(date.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
                this.workInPlanDoneDate = null;
            }
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(reactionHeaderID);
        dest.writeString(reactionHeaderManagerID);
        dest.writeString(workInPlanForCustomerID);
        dest.writeString(workInPlanForCustomerOrder);
        dest.writeInt(workInPlanID);
        dest.writeString(reactionWorkManagerID);
        dest.writeString(managerName);
        dest.writeString(reactionWorkDoneByID);
        dest.writeString(reactionWorkDoneByName);
        dest.writeString(reactionWorkActionID);
        dest.writeString(workInPlanName);
        dest.writeString(workInPlanNote);
        dest.writeString(workInPlanForCustomerName);

        if (workInPlanTerm == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(workInPlanTerm.getTime());
        }

        if (workInPlanDoneDate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(workInPlanDoneDate.getTime());
        }

        dest.writeString(workInPlanDone);
        dest.writeString(repName);
        dest.writeString(repSurname);
        dest.writeString(repPhone);
        dest.writeString(repEmail);

    }

    // Getter methods for all fields...
    public String getReactionWorkDoneByID() {
        return reactionWorkDoneByID;
    }

    public void setReactionWorkDoneByID(String reactionWorkDoneByID) {
        this.reactionWorkDoneByID = reactionWorkDoneByID;
    }


    public String getReactionWorkDoneByName() {
        return reactionWorkDoneByName;
    }

    public void setReactionWorkDoneByName(String reactionWorkDoneByName) {
        this.reactionWorkDoneByName = reactionWorkDoneByName;
    }

    public String getReactionHeaderID() {
        return reactionHeaderID;
    }


    public String getReactionHeaderManagerID() {
        return reactionHeaderManagerID;
    }

    public String getWorkInPlanForCustomerID() {
        return workInPlanForCustomerID;
    }

    public String getWorkInPlanForCustomerOrder() {
        return workInPlanForCustomerOrder;
    }

    public int getWorkInPlanID() {
        return workInPlanID;
    }

    public String getReactionWorkManagerID() {
        return reactionWorkManagerID;
    }

    public String getManagerName() {
        return managerName;
    }

    public String getReactionWorkActionID() {
        return reactionWorkActionID;
    }

    public String getWorkInPlanName() {
        return workInPlanName;
    }

    public String getWorkInPlanNote() {
        return workInPlanNote;
    }

    public String getWorkInPlanForCustomerName() {
        return workInPlanForCustomerName;
    }

    public Timestamp getWorkInPlanTerm() {
        return workInPlanTerm;
    }

    public Timestamp getWorkInPlanDoneDate() {
        return workInPlanDoneDate;
    }

    public String getWorkInPlanDone() {
        return workInPlanDone;
    }

    public String getRepName() {
        return repName;
    }

    public String getRepSurname() {
        return repSurname;
    }

    public String getRepPhone() {
        return repPhone;
    }

    public String getRepEmail() {
        return repEmail;
    }

    // Setter methods for representative details...

    public void setRepName(String repName) {
        this.repName = repName;
    }

    public void setRepSurname(String repSurname) {
        this.repSurname = repSurname;
    }

    public void setRepPhone(String repPhone) {
        this.repPhone = repPhone;
    }

    public void setRepEmail(String repEmail) {
        this.repEmail = repEmail;
    }
}
