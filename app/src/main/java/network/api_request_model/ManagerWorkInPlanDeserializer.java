package network.api_request_model;

import android.util.Log;

import com.google.gson.*;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;

import java.lang.reflect.Type;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class ManagerWorkInPlanDeserializer implements JsonDeserializer<ManagerWorkInPlan> {
    @Override
    public ManagerWorkInPlan deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ManagerWorkInPlan managerWorkInPlan = new ManagerWorkInPlan();

        // Check if "workInPlanID" field exists and is a valid integer
        Integer workInPlanID = getIntegerValue(jsonObject, "workInPlanID");
        if (workInPlanID != null) {
            managerWorkInPlan.setWorkInPlanID(workInPlanID);
        } else {
            // Handle the case where "workInPlanID" is missing or not a valid integer
           // Log.e("ManagerWorkInPlanDeserializer", "Field 'workInPlanID' is missing, null, or not a valid integer.");
        }
        Integer workInPlanDelayed = getIntegerValue(jsonObject, "workInPlanDelayed");
        if (workInPlanDelayed != null) {
            managerWorkInPlan.setWorkInPlanDelayed(workInPlanDelayed);
        } else {
            // Handle the case where "workInPlanDelayed" is missing or not a valid integer
            //Log.e("ManagerWorkInPlanDeserializer", "Field 'workInPlanDelayed' is missing, null, or not a valid integer.");
        }
        Integer workInPlanManagerID = getIntegerValue(jsonObject, "workInPlanManagerID");
        if (workInPlanManagerID != null) {
            managerWorkInPlan.setWorkInPlanManagerID(workInPlanManagerID);
        } else {
            // Handle the case where "workInPlanManagerID" is missing or not a valid integer
           // Log.e("ManagerWorkInPlanDeserializer", "Field 'workInPlanManagerID' is missing, null, or not a valid integer.");
        }
        Integer workInPlanByOrderID = getIntegerValue(jsonObject, "workInPlanByOrderID");
        if (workInPlanByOrderID != null) {
            managerWorkInPlan.setWorkInPlanByOrderID(workInPlanByOrderID);
        } else {
            // Handle the case where "workInPlanByOrderID" is missing or not a valid integer
           // Log.e("ManagerWorkInPlanDeserializer", "Field 'workInPlanByOrderID' is missing, null, or not a valid integer.");
        }

        managerWorkInPlan.setWorkInPlanTerm(parseTimestamp(jsonObject.get("workInPlanTerm")));
        // Handle other fields...
        managerWorkInPlan.setWorkInPlanStart(parseTimestamp(jsonObject.get("workInPlanStart")));
        managerWorkInPlan.setWorkInPlanDone(parseTimestamp(jsonObject.get("workInPlanDone")));
        managerWorkInPlan.setWorkInPlanStarted(parseTimestamp(jsonObject.get("workInPlanStarted")));
        managerWorkInPlan.setWorkInPlanReviewed(parseTimestamp(jsonObject.get("workInPlanReviewed")));
        managerWorkInPlan.setWorkInPlanPriority(getStringValue(jsonObject, "workInPlanPriority"));
        managerWorkInPlan.setWorkInPlanForCutomerName(getStringValue(jsonObject, "workInPlanForCutomerName"));
        managerWorkInPlan.setWorkInPlanForCustomerCode(getStringValue(jsonObject, "workInPlanForCustomerCode"));
        managerWorkInPlan.setWorkInPlanByOrderNo(getStringValue(jsonObject, "workInPlanByOrderNo"));
        managerWorkInPlan.setWorkInPlanName(getStringValue(jsonObject, "workInPlanName"));
        managerWorkInPlan.setWorkInPlanNote(getStringValue(jsonObject, "workInPlanNote"));

        return managerWorkInPlan;
    }

    private Integer getIntegerValue(JsonObject jsonObject, String fieldName) {
        JsonElement element = jsonObject.get(fieldName);
        if (element != null && !element.isJsonNull() && element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
            return element.getAsInt();
        } else {
            //Log.e("ManagerWorkInPlanDeserializer", "Field '" + fieldName + "' is missing, null, or not a valid integer.");
            return null; // Return null for missing, null, or invalid integer values
        }
    }


    private String getStringValue(JsonObject jsonObject, String fieldName) {
        JsonElement element = jsonObject.get(fieldName);
        if (element != null && !element.isJsonNull()) {
            return element.getAsString();
        } else {
            return null; // Return null for missing or null values
        }
    }

    private Timestamp parseTimestamp(JsonElement element) {
        if (element != null && !element.isJsonNull()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                java.util.Date parsedDate = dateFormat.parse(element.getAsString());
                return new Timestamp(parsedDate.getTime());
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null; // Return null if element is missing or null
    }
}