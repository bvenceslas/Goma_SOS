package com.android.brain.sosfind.Common;

import com.android.brain.sosfind.Remote.FCMClient;
import com.android.brain.sosfind.Remote.IFCMService;
import com.android.brain.sosfind.Remote.IGoogleAPI;
import com.android.brain.sosfind.Remote.RetrofitClient;

/**
 * Created by Brain on 13/04/2019.
 */

public class Common {

    public static String currentToken = "";
    public static final String baseURL = "https://maps.googleapis.com/";
    public static final String fcmURL = "https://fcm.googleapis.com/";

    //Pour passer l'API
    public static IGoogleAPI getGoogleAPI()
    {
        return RetrofitClient.getClient(baseURL).create(IGoogleAPI.class);
    }

    //ALert//Pour utiliser le service appel sur reseau Passager et chauffeur
    public static IFCMService getFCMService()
    {
        return FCMClient.getClient(fcmURL).create(IFCMService.class);
    }
}
