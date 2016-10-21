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

import android.app.Activity;
import android.content.ClipDescription;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Toast;

public class ShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null && ClipDescription.MIMETYPE_TEXT_PLAIN.equals(type)) {
            new SharingTask(this).execute();
        }
        else
            finish();
    }

    /**
     * @return Loadable Uri object that has all the attributes set
     */
    public Uri fetchLoadableUri() {
        String url = Utils.fetchUrl(getIntent());
        String title = Utils.fetchTitle(url);
        String clipboardData = Utils.fetchClipboardData(this);

        return new Uri.Builder()
                .scheme("http")
                .authority("community.source.institute")
                .appendPath("new-topic")
                .appendQueryParameter("title", title)
                .appendQueryParameter("body",
                        String.format("%s\n\nFrom [%s](%s):\n> %s", url, title, url, clipboardData))
                .appendQueryParameter("category", "Community")
                .build();
    }

    /**
     * Opens a browser and load the uri
     */
    public void loadInBrowser(Uri uri) {
        Utils.log(uri.toString());
        Intent viewLink = new Intent(Intent.ACTION_VIEW, uri);
        if (viewLink.resolveActivity(getPackageManager()) != null) {
            startActivity(viewLink);
        }
        else
            Toast.makeText(getApplicationContext(), R.string.toast_browser_not_found, Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * Task to execute the sharing process in background
     */
    public static class SharingTask extends AsyncTask<Void, Void, Uri> {
        private ShareActivity shareActivity;

        public SharingTask(ShareActivity shareActivity) {
            this.shareActivity = shareActivity;
        }

        @Override
        protected Uri doInBackground(Void... params) {
            Looper.prepare();
            return shareActivity.fetchLoadableUri();
        }

        @Override
        protected void onPostExecute(Uri uri) {
            shareActivity.loadInBrowser(uri);
        }
    }

    /**
     * Overriding without super call so that cannot be exit on back press
     */
    @Override
    public void onBackPressed() {}
}
