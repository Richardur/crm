package network;

import model.Employe;
import network.api_request_model.ApiResponseGetCustomer;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ApiResponseWorkPlan;
import network.api_response.CRMWorkResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @POST("login")
    @FormUrlEncoded
    Call<AuthResponse> authenticate(@Field("login") String login, @Field("password") String password);

    @POST("CRMWorkList")
    @FormUrlEncoded
    Call<CRMWorkResponse> getActionDetails(@Field("userId") String userId,
                                           @Field("apiKey") String apiKey,
                                           @Field("getInfoType") String getInfoType,
                                           @Field("language") String language,
                                           @Field("action") String action,
                                           @Field("where") String where,
                                           @Field("limit") String limit,
                                           @Field("orderBy") String orderBy);

    @POST("zinynai_employe")
    @FormUrlEncoded
    Call<EmployeResponse> getEmployeeDetails(@Field("userId") String userId,
                                     @Field("apiKey") String apiKey,
                                     @Field("getInfoType") String getInfoType,
                                     @Field("language") String language,
                                     @Field("action") String action,
                                     @Field("where") String where,
                                     @Field("limit") String limit,
                                     @Field("orderBy") String orderBy);



    @POST("managerReactionInPlan")
    @FormUrlEncoded
    Call<ApiResponseReactionPlan> getReactionPlanList(@Field("userId") String userId,
                                                  @Field("apiKey") String apiKey,
                                                  @Field("getInfoType") String getInfoType,
                                                  @Field("language") String language,
                                                  @Field("action") String action,
                                                  @Field("where") String where,
                                                  @Field("limit") String limit,
                                                  @Field("orderBy") String orderBy);

  @POST("managerReactionInPlan")
  @FormUrlEncoded
  Call<ApiResponseReactionPlan> getTasksForDate(@Field("userId") String userId,
                                                    @Field("apiKey") String apiKey,
                                                    @Field("getInfoType") String getInfoType,
                                                    @Field("language") String language,
                                                    @Field("action") String action,
                                                    @Field("where") String where,
                                                    @Field("limit") String limit,
                                                    @Field("orderBy") String orderBy);

  @POST("managerReactionInPlan")
  @FormUrlEncoded
  Call<ApiResponseReactionPlan> updateTask(
          @Field("userId") String userId,
          @Field("apiKey") String apiKey,
          @Field("putType") String putType,
          @Field("language") String language,
          @Field("action") String action,
          @Field("where") String where, // JSON string with the update details
          @Field("limit") String limit
          //@Field("orderBy") String orderBy // If needed
  );

    @POST("managerReactionInPlan")
    @FormUrlEncoded
    Call<ApiResponseUpdate> updateManagerReaction(
            @Field("userId") String userId,
            @Field("apiKey") String apiKey,
            @Field("action") String action,
            @Field("languageCode") String languageCode,
            @Field("ManagerReactionInPlanHeaderReg") String managerReactionInPlanHeaderReg,
            @Field("ManagerReactionWorkReg") String managerReactionWorkReg
    );


    @POST("customer")
    @FormUrlEncoded
    Call<CustomerListResponse> getCustomerList(@Field("userId") String userId,
                                             @Field("apiKey") String apiKey,
                                             @Field("getInfoType") String getInfoType,
                                             @Field("language") String language,
                                             @Field("action") String action,
                                             @Field("where") String where,
                                             @Field("limit") String limit,
                                             @Field("orderBy") String orderBy);
    @POST("customer")
    @FormUrlEncoded
    Call<ApiResponseGetCustomer> getCustomer(@Field("userId") String userId,
                               @Field("apiKey") String apiKey,
                               @Field("getInfoType") String getInfoType,
                               @Field("language") String language,
                               @Field("action") String action,
                               @Field("where") String where,
                               @Field("limit") String limit,
                               @Field("orderBy") String orderBy);


    @POST("customer")
    @FormUrlEncoded
    Call<ApiResponseUpdate> updateCustomer(
            @Field("userId") String userId,
            @Field("apiKey") String apiKey,
            @Field("action") String action,
            @Field("languageCode") String languageCode,
            @Field("customerID") String customerId,
            @Field("customerName") String customerName,
            @Field("customerCode") String customerCode,
            @Field("customerVAT") String customerVAT,
            @Field("customerType") String customerType,
            @Field("customerActive") String customerActive,
            @Field("customerAdressCity") String customerAdressCity,
            @Field("customerAdressStreet") String customerAdressStreet,
            @Field("customerAdressHouse") String customerAdressHouse,
            @Field("customerAdressPostIndex") String customerAdressPostIndex,
            @Field("customerWWW") String customerWWW,
            @Field("CustomerContactPersons") String customerContactPersons // JSON string of contact persons
    );


    Call<ApiResponseUpdate> updateCustomer(String userId, String apiKey, String action, String languageCode, Object customer);
}
