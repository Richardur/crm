package network;

import network.api_request_model.ApiResponseGetCustomer;
import network.api_request_model.ApiResponseReactionPlan;
import network.api_request_model.ApiResponseWorkPlan;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface ApiService {
    @POST("login")
    @FormUrlEncoded
    Call<AuthResponse> authenticate(@Field("login") String login, @Field("password") String password);


  /*  @POST("managerWorkInPlan")
    @FormUrlEncoded
    Call<ApiResponseWorkPlan> getWorkPlanList(@Field("userId") String userId,
                                              @Field("apiKey") String apiKey,
                                              @Field("getInfoType") String getInfoType,
                                              @Field("language") String language,
                                              @Field("action") String action,
                                              @Field("where") String where,
                                              @Field("limit") String limit,
                                              @Field("orderBy") String orderBy);
*/
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

  @POST("managerReactionInPlanEdit")
  @FormUrlEncoded
  Call<ApiResponseReactionPlan> updateTask(
          @Field("userId") String userId,
          @Field("apiKey") String apiKey,
          @Field("putType") String putType,
          @Field("language") String language,
          @Field("action") String action,
          @Field("where") String where, // JSON string with the update details
          @Field("limit") String limit,
          @Field("orderBy") String orderBy // If needed
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


    @PUT("customerReg")
    @FormUrlEncoded
    Call<ApiResponseWorkPlan> editCustomer(
            @Field("apiKey") String apiKey,
            @Field("userId") String userId,
            @Field("language") String language,
            @Field("putType") String putType,
            @Field("limit") String limit,
            @Field("id") String id,
            @Field("customerName") String customerName, // Add other fields as needed
            @Field("customerCode") String customerCode);


}
