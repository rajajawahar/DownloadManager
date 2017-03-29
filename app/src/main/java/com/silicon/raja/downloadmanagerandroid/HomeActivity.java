package com.silicon.raja.downloadmanagerandroid;

import android.app.DownloadManager;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import java.io.FileNotFoundException;

public class HomeActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_home);
  }

  public void startDownload(View view) {
    DownloadManager mManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
    DownloadManager.Request request = new DownloadManager.Request(
        Uri.parse("https://sites.google.com/site/compiletimeerrorcom/android-programming/oct-2013/DownloadManagerAndroid1.zip"));
    request.setDescription("File Downloading");
    long enqueue = mManager.enqueue(request);
    try {
      mManager.openDownloadedFile(enqueue);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
