package com.isolate.egovdhn.in.Database;

import com.isolate.egovdhn.in.Models.AddAlertRequestModel;
import com.isolate.egovdhn.in.Models.BlockResponse;
import com.isolate.egovdhn.in.Models.ChangePasswordRequestModel;
import com.isolate.egovdhn.in.Models.DistrictModel;
import com.isolate.egovdhn.in.Models.DistrictResponse;
import com.isolate.egovdhn.in.Models.FCMTokenRequest;
import com.isolate.egovdhn.in.Models.FormDataResponse;
import com.isolate.egovdhn.in.Models.GetPrescriptionRequestModel;
import com.isolate.egovdhn.in.Models.GetRecordsRequestModel;
import com.isolate.egovdhn.in.Models.GetSchedulesRequestModel;
import com.isolate.egovdhn.in.Models.LocationRequestModel;
import com.isolate.egovdhn.in.Models.LoginRequestModel;
import com.isolate.egovdhn.in.Models.NotifResponse;
import com.isolate.egovdhn.in.Models.ResetPasswordRequestModel;
import com.isolate.egovdhn.in.Models.ResponseAddAlert;
import com.isolate.egovdhn.in.Models.ResponseAddRecord;
import com.isolate.egovdhn.in.Models.ResponseChangePassword;
import com.isolate.egovdhn.in.Models.ResponseGetContacts;
import com.isolate.egovdhn.in.Models.ResponseGetPhoto;
import com.isolate.egovdhn.in.Models.ResponseGetPrescription;
import com.isolate.egovdhn.in.Models.ResponseGetRecords;
import com.isolate.egovdhn.in.Models.ResponseGetSchedules;
import com.isolate.egovdhn.in.Models.ResponseResetPassword;
import com.isolate.egovdhn.in.Models.ResponseUpload;
import com.isolate.egovdhn.in.Models.StateModel;
import com.isolate.egovdhn.in.Models.StateResponse;
import com.isolate.egovdhn.in.Models.UploadResponse;

import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitNetworkApi {

    @Multipart
    @POST("api2/user_sign_up.php")
    Call<ResponseUpload> sendIsolationRequest(
            @Part("info") RequestBody info,
            @Part MultipartBody.Part aadhaarFront,
            @Part MultipartBody.Part aadharaBack,
            @Part MultipartBody.Part washroom1,
            @Part MultipartBody.Part washroom2,
            @Part MultipartBody.Part home1,
            @Part MultipartBody.Part home2,
            @Part MultipartBody.Part home3,
            @Part MultipartBody.Part home4,
            @Part MultipartBody.Part home5
    );


    @Multipart
    @POST("api2/user_update.php")
    Call<ResponseUpload> updateIsolationRequest(
            @Part("info") RequestBody info,
            @Part MultipartBody.Part aadhaarFront,
            @Part MultipartBody.Part aadhaarBack,
            @Part MultipartBody.Part washroom1,
            @Part MultipartBody.Part washroom2,
            @Part MultipartBody.Part home1,
            @Part MultipartBody.Part home2,
            @Part MultipartBody.Part home3,
            @Part MultipartBody.Part home4,
            @Part MultipartBody.Part home5
    );


    @Multipart
    @POST("api2/add_photo.php")
    Call<ResponseUpload> uploadPhoto(
            @Part("info") RequestBody info,
            @Part MultipartBody.Part image
    );

    @POST("api2/user_exists.php")
    Call<FormDataResponse> getUser(@Body HashMap<String, String> body);


    @POST("api2/check_photo.php")
    Call< ResponseGetPhoto > getPhotoEnable(@Body GetSchedulesRequestModel request);

    @POST("api2/user_log_in.php")
    Call<ResponseUpload> getLogin(@Body LoginRequestModel loginRequestModel);

    @POST("api/get_states.php")
    Call<StateResponse> getStates();

    @POST("api/get_districts.php")
    Call<DistrictResponse> getDistricts(@Body StateModel stateModel);

    @POST("api/get_blocks.php")
    Call<BlockResponse> getBlocks(@Body DistrictModel districtModel);

    @POST("api/get_schedule.php")
    Call<ResponseGetSchedules> getSchedules(@Body GetSchedulesRequestModel getSchedulesRequestModel);

    @POST("api2/add_alert.php")
    Call<ResponseAddAlert> addAlert(@Body AddAlertRequestModel addAlertRequestModel);

    @POST("api2/user_add_remark.php")
    Call<UploadResponse> updateLocation(@Body LocationRequestModel locationRequestModel);

    @POST("api2/get_records.php")
    Call<ResponseGetRecords> getRecords(@Body GetRecordsRequestModel getRecordsRequestModel);

    @Multipart
    @POST("api2/add_record.php")
    Call<ResponseAddRecord> updateData(
            @Part("info") RequestBody info,
            @Part MultipartBody.Part temperatureProof,
            @Part MultipartBody.Part bpProof,
            @Part MultipartBody.Part spo2Proof
    );

    @POST("api2/reset_password.php")
    Call<ResponseResetPassword> resetPassword(@Body ResetPasswordRequestModel resetPasswordRequestModel);

    @POST("api2/change_password.php")
    Call<ResponseChangePassword> changePassword(@Body ChangePasswordRequestModel changePasswordRequestModel);

    @POST("api2/get_prescription.php")
    Call<ResponseGetPrescription> getPrescription(@Body GetPrescriptionRequestModel getPrescriptionRequestModel);

    @POST("api2/get_upload.php")
    Call< UploadResponse > getUpload(@Body GetPrescriptionRequestModel getPrescriptionRequestModel);

    //TODO: change endpoints
    @POST("api2/get_notifications.php")
    Call< NotifResponse > getNotifications(@Body GetPrescriptionRequestModel getPrescriptionRequestModel);

    @POST("api2/user_update_token.php")
    Call<ResponseUpload> saveTokenToServer(@Body FCMTokenRequest fcmTokenRequest);

    @POST("api2/user_active.php")
    Call<ResponseResetPassword> getUserActive(@Body GetSchedulesRequestModel request);

    @POST("api2/set_telemedicine.php")
    Call<ResponseResetPassword> setStatus(@Body HashMap<String, String> map);

    @GET("api2/get_contacts.php")
    Call<ResponseGetContacts> getEmergencyContacts();
}
