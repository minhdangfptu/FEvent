package com.fptu.fevent.util;

import android.content.Context;
import com.cloudinary.android.MediaManager;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryUtil {
    private static boolean isInitialized = false;

    public static void initCloudinary(Context context) {
        if (isInitialized) return;

        // ✅ KHÔNG được gọi MediaManager.get() trước khi init()

        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", "dn9txxtvm");
        config.put("api_key", "771384254166158");
        config.put("api_secret", "CmEtRT9nLdm5IkzZZSGJAHQXosE");
        config.put("secure", true);

        MediaManager.init(context.getApplicationContext(), config);

        isInitialized = true;
    }
}
