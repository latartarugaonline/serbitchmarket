package it.ltm.scp.module.android.api.scanner;

import it.ltm.scp.module.android.model.devices.scanner.ScannerInfo;
import it.ltm.scp.module.android.model.devices.scanner.ScannerPatchConfig;
import it.ltm.scp.module.android.model.devices.scanner.ScannerSnapshotREST;
import it.ltm.scp.module.android.model.devices.scanner.VideoRequest;
import it.ltm.scp.module.android.model.devices.scanner.VideoResponse;
import it.ltm.scp.module.android.model.devices.scanner.ZebraConfig;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ScannerAPIService {

    @POST("api/devices/scanner/video")
    Call<VideoResponse> startVideoMode(@Body VideoRequest videoRequest);

    @DELETE("api/devices/scanner/video")
    Call<VideoResponse> stopVideoMode();

    @GET("api/devices/scanner/video")
    Call<ScannerSnapshotREST> getSnapshot();

    @GET("api/devices/scanners")
    Call<ScannerInfo> getScannerInfo();

    @POST("api/devices/scanner/idc")
    Call<Void> startIdcMode(@Body ZebraConfig zebraConfig);

    @DELETE("api/devices/scanner/idc")
    Call<Void> stopIdcMode();

    @PATCH("api/devices/scanner/config/{scannerID}")
    Call<Void> patchScannerConfig(@Path("scannerID")String scannerID, @Body ScannerPatchConfig scannerPatchConfig);

    @PUT("api/devices/scanner/config/{scannerID}")
    Call<Void> putScannerConfig(@Path("scannerID")String scannerID, @Body ZebraConfig zebraConfig);

}
