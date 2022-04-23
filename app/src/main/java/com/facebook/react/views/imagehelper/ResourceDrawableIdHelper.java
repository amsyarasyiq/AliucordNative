/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license
 */

package com.facebook.react.views.imagehelper;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;

import com.discord.BuildConfig;

import java.util.HashMap;
import java.util.Map;

public class ResourceDrawableIdHelper {
    private static final String LOCAL_RESOURCE_SCHEME = "res";
    private static volatile ResourceDrawableIdHelper sResourceDrawableIdHelper;
    private Map<String, Integer> mResourceDrawableIdMap = new HashMap();

    private ResourceDrawableIdHelper() {
    }

    public static ResourceDrawableIdHelper getInstance() {
        if (sResourceDrawableIdHelper == null) {
            synchronized (ResourceDrawableIdHelper.class) {
                if (sResourceDrawableIdHelper == null) {
                    sResourceDrawableIdHelper = new ResourceDrawableIdHelper();
                }
            }
        }
        return sResourceDrawableIdHelper;
    }

    private int getResourceDrawableId(Context context, String str) {
        if (str == null || str.isEmpty()) {
            return 0;
        }
        String replace = str.toLowerCase().replace("-", "_");
        try {
            return Integer.parseInt(replace);
        } catch (NumberFormatException unused) {
            synchronized (this) {
                if (this.mResourceDrawableIdMap.containsKey(replace)) {
                    return this.mResourceDrawableIdMap.get(replace);
                }
                // ALIUCORD CHANGED: dynamic package name lookup -> BuildConfig.APPLICATION_ID
                int identifier = context.getResources().getIdentifier(replace, "drawable", BuildConfig.APPLICATION_ID);
                this.mResourceDrawableIdMap.put(replace, identifier);
                return identifier;
            }
        }
    }

    public synchronized void clear() {
        this.mResourceDrawableIdMap.clear();
    }

    public Drawable getResourceDrawable(Context context, String str) {
        Drawable resourceDrawable = ImageOTAUtils.getResourceDrawable(context, str);
        if (resourceDrawable != null) {
            return resourceDrawable;
        }
        int resourceDrawableId = getResourceDrawableId(context, str);
        if (resourceDrawableId > 0) {
            return context.getResources().getDrawable(resourceDrawableId);
        }
        return null;
    }

    public Uri getResourceDrawableUri(Context context, String str) {
        Uri resourceUri = ImageOTAUtils.getResourceUri(context, str);
        if (resourceUri != null) {
            return resourceUri;
        }
        int resourceDrawableId = getResourceDrawableId(context, str);
        if (resourceDrawableId > 0) {
            return new Uri.Builder().scheme(LOCAL_RESOURCE_SCHEME).path(String.valueOf(resourceDrawableId)).build();
        }
        return Uri.EMPTY;
    }
}