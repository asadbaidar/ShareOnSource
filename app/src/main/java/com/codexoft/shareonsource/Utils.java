/*
 * Copyright (C) 2016 Codexoft Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.codexoft.shareonsource;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.net.URL;
import java.util.regex.Pattern;

import static org.jsoup.Connection.*;

public final class Utils {

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0";
    public static final int REQUEST_TIMEOUT = 8000;

    /**
     * @param url to which the title needed to be fetched
     * @return title from the url
     */
    public static String fetchTitle(String url){
        if (URLUtil.isValidUrl(url)) {
            try {
                return Jsoup.connect(url)
                        .ignoreContentType(true)
                        .userAgent(USER_AGENT)
                        .timeout(REQUEST_TIMEOUT)
                        .followRedirects(true)
                        .execute()
                        .parse()
                        .title();
            } catch (Exception e) {
                log("Title Parse Error", e);
                return fetchHostName(url);
            }
        }
        return  "";
    }

    /**
     * @param url to which the host name needed to be fetched
     * @return host name from the url
     */
    public static String fetchHostName(String url) {
        if (URLUtil.isValidUrl(url)) {
            try {
                String hostName = Uri.parse(url).getHost().replace("www.", "").split(Pattern.quote("."))[0];
                String firstChar = hostName.substring(0, 1);
                return hostName.replaceFirst(firstChar, firstChar.toUpperCase());
            } catch (Exception e) {
                log("Host Parse Error", e);
                return "";
            }
        }
        return "";
    }

    /**
     * @param intent used to get EXTRA_TEXT which contains the text data
     * @return the text data
     */
    public static String fetchUrl(Intent intent) {
        try {
            String bodyText = intent.getStringExtra(Intent.EXTRA_TEXT);
            return bodyText != null ? bodyText : "";
        } catch (Exception e) {
            log("Url Parse Error", e);
            return "";
        }
    }

    /**
     * @return previously copied clipboard text data
     */
    public static String fetchClipboardData(Context context) {
        try {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard.hasPrimaryClip() &&
                    clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)) {
                ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);
                CharSequence data = item.getText();
                return data != null ? data.toString() : "";
            }
        }
        catch (Exception e) {
            log("ClipboardData Parse Error", e);
            return "";
        }
        return  "";
    }

    public static void log(String msg) {
        Log.d("ShareOnSource", msg);
    }

    public static void log(String msg, Exception e) {
        Log.d("ShareOnSource", msg, e);
    }

    /*
    //for image sharing

        Uri dataUri = getIntent().getParcelableExtra(Intent.EXTRA_STREAM);
        ArrayList<Uri> imageUris = getIntent().getParcelableArrayListExtra(Intent.EXTRA_STREAM);

        if (imageUris != null) {
            for (Uri uri : imageUris)
                tv.setText(tv.getText() + "\n arraystream " + uri.toString());
        }
        if (dataUri != null) {
            tv.setText(tv.getText() + "\n stream " + dataUri.toString());
            Cursor returnCursor =
                    getContentResolver().query(dataUri, null, null, null, null);
            if (returnCursor != null) {
                int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                returnCursor.moveToFirst();
                tv.setText(tv.getText() + "\n file name " + returnCursor.getString(nameIndex));
            }
        }
     */
}
