package network;

import model.Customer;
import network.api_request_model.ApiResponse;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @POST("login")
    @FormUrlEncoded
    Call<AuthResponse> authenticate(@Field("login") String login, @Field("password") String password);


    @POST("managerWorkInPlan")
    @FormUrlEncoded
    Call<ApiResponse> getWorkPlanList(@Field("userId") String userId,
                                                  @Field("apiKey") String apiKey,
                                                  @Field("getInfoType") String getInfoType,
                                                  @Field("language") String language,
                                                  @Field("action") String action,
                                                  @Field("where") String where,
                                                  @Field("limit") String limit,
                                                  @Field("orderBy") String orderBy);

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
    Call<Customer> getCustomer(@Field("userId") String userId,
                               @Field("apiKey") String apiKey,
                               @Field("getInfoType") String getInfoType,
                               @Field("language") String language,
                               @Field("action") String action,
                               @Field("where") String where,
                               @Field("limit") String limit,
                               @Field("orderBy") String orderBy);

}
