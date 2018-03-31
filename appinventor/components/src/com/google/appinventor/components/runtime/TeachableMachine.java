// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the Apache License, Version 2.0
// http://www.apache.org/licenses/LICENSE-2.0

package com.google.appinventor.components.runtime;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.*;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.common.YaVersion;
import com.google.appinventor.components.runtime.util.JsonUtil;
import com.google.appinventor.components.runtime.util.MediaUtil;
import com.google.appinventor.components.runtime.util.YailList;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Component for classifying images.
 */

@DesignerComponent(version = YaVersion.TEACHABLEMACHINE_COMPONENT_VERSION,
        category = ComponentCategory.EXPERIMENTAL, nonVisible = false,
        description = "Component for classifying images.")
@SimpleObject
@UsesAssets(fileNames = "computervision.html")
@UsesPermissions(permissionNames = "android.permission.INTERNET")
public final class TeachableMachine extends AndroidViewComponent implements Component {
    private static final String LOG_TAG = TeachableMachine.class.getSimpleName();

    private final WebView webview;
    private final Form form;

    /**
     * Creates a new WebViewer component.
     *
     * @param form the container that this component will be placed in
     */
    public TeachableMachine(Form form) {
        super(form);
        webview = new WebView(form);
        this.form = form;
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setMediaPlaybackRequiresUserGesture(false);
        // adds a way to send strings to the javascript
        webview.addJavascriptInterface(new JsObject(), "TeachableMachine");
        webview.setWebViewClient(new WebViewClient());
        webview.setWebChromeClient(new WebChromeClient() {
          @Override
          public void onPermissionRequest(PermissionRequest request) {
            String[] requestedResources = request.getResources();
            for (String r : requestedResources) {
              if (r.equals(PermissionRequest.RESOURCE_VIDEO_CAPTURE)) {
                request.grant(request.getResources());
              }
            }
            //super.onPermissionRequest(request);
            Log.d(LOG_TAG, "onPermissionRequest called");
          }
        });
        //webview.loadUrl("https://kelseyc18.github.io/appinventor-computervision/");
        //webview.loadUrl("https://kelseyc18.github.io/appinventor-computervision/image/");
        webview.loadUrl("https://kevin-vr.github.io/teachable-machine/");
        //webview.loadUrl("file:///android_assets/deeplearnjs.html");
//        webview.loadUrl("file:///android_asset/component/deeplearnjs.html");
        Log.d(LOG_TAG, "Created TeachableMachine component");
        form.$add(this);
    }

    /**
     * Classifies the image at the given path.
     */
    @SimpleFunction
    public void Classify(final String image) {
        Log.d(LOG_TAG, "Entered Classify");
        Log.d(LOG_TAG, image);

        String imagePath = (image == null) ? "" : image;
        BitmapDrawable imageDrawable;
        Bitmap scaledImageBitmap = null;

        try {
            imageDrawable = MediaUtil.getBitmapDrawable(form.$form(), imagePath);
            //scaledImageBitmap = Bitmap.createScaledBitmap(imageDrawable.getBitmap(), 227, 227, false);
            scaledImageBitmap = Bitmap.createScaledBitmap(imageDrawable.getBitmap(), 500, (int) (imageDrawable.getBitmap().getHeight() * 500.0 / imageDrawable.getBitmap().getWidth()), false);
        } catch (IOException ioe) {
            Log.e(LOG_TAG, "Unable to load " + imagePath);
        }

        // compression format of PNG -> not lossy
        Bitmap immagex = scaledImageBitmap;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();

        String imageEncodedbase64String = Base64.encodeToString(b, 0).replace("\n", "");
        Log.d(LOG_TAG, "imageEncodedbase64String: " + imageEncodedbase64String);

    }

    @SimpleFunction
    public void StartVideo() {
      webview.evaluateJavascript("start();", null);
    }

    @SimpleFunction
    public void StopVideo() {
      webview.evaluateJavascript("stop();", null);
    }

    @SimpleFunction
    public void ToggleCameraFacingMode() {
      webview.evaluateJavascript("toggleCameraFacingMode();", null);
    }

    @SimpleFunction
    public void ClassifyVideoData() {
      webview.evaluateJavascript("classifyVideoData();", null);
    }

    @SimpleFunction
    public void ShowImage() {
      webview.evaluateJavascript("showImage();", null);
    }

    @SimpleFunction
    public void HideImage() {
      webview.evaluateJavascript("hideImage();", null);
    }

    @SimpleFunction
    public void SetInputMode(final String inputMode) {
      webview.evaluateJavascript("setInputMode(\"" + inputMode + "\");", null);
    }

    @SimpleFunction
    public void SetInputWidth(final int width) {
      webview.evaluateJavascript("setInputWidth(" + width + ");", null);
    }

    @SimpleFunction
    public void Train(final YailList data) {

    }

    @SimpleFunction
    public void TrainSample(final YailList sample) {

    }

    @SimpleFunction
    public void Clear() {

    }

    @SimpleFunction
    public void Save(final String file) {

    }

    @SimpleFunction
    public void Load(final String file) {

    }

    @SimpleFunction
    public void StartTraining(final String label) {

    }

    @SimpleFunction
    public void StopTraining() {

    }

    /*
    @SimpleFunction
    public int GetSampleCount(final String label) {
        return 0;
    }

    @SimpleFunction
    public float GetConfidence(final String label) {
        return 0;
    }
    */

    @SimpleEvent
    public void ClassifierReady() {
        EventDispatcher.dispatchEvent(this, "ClassifierReady");
    }

    @SimpleEvent
    public void AfterTraining(int responseCode, String message) {
        EventDispatcher.dispatchEvent(this, "AfterTraining", responseCode, message);
    }

    @SimpleEvent
    public void GotClassification(YailList result) {
        EventDispatcher.dispatchEvent(this, "GotClassification", result);
    }

    @SimpleEvent
    public void ClassificationFailed(int errorCode, String message) {
        EventDispatcher.dispatchEvent(this, "ClassificationFailed", errorCode, message);
    }

    @Override
    public View getView() {
        return webview;
    }

    private class JsObject {
        @JavascriptInterface
        public void ready() {
            Log.d(LOG_TAG, "Entered ready");
            form.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ClassifierReady();
                }
            });
        }

        @JavascriptInterface
        public void reportResult(final String result) {
            Log.d(LOG_TAG, "Entered reportResult: " + result);
            try {
                Log.d(LOG_TAG, "Entered try of reportResult");
                JSONArray list = new JSONArray(result);
                YailList intermediateList = YailList.makeList(JsonUtil.getListFromJsonArray(list));
                final List resultList = new ArrayList();
                for (int i = 0; i < intermediateList.size(); i++) {
                    resultList.add(YailList.makeList((List) intermediateList.getObject(i)));
                }
                form.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        GotClassification(YailList.makeList(resultList));
                    }
                });
            } catch (JSONException e) {
                Log.d(LOG_TAG, "Entered catch of reportResult");
                e.printStackTrace();
                reportError(1, e.getMessage());
            }
        }

        @JavascriptInterface
        public void reportError(final int code, final String message) {
            Log.d(LOG_TAG, "Entered reportError: " + message);
            form.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ClassificationFailed(code, message);
                }
            });
        }
    }
}

