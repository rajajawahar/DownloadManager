package com.silicon.raja.downloadmanagerandroid;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;

/**
 * Created by rajamohamed on 29/03/17.
 */

public class DownloadCompleteReceived extends BroadcastReceiver {

  private static final String TAG = DownloadCompleteReceived.class.getSimpleName();

  @Override public void onReceive(Context context, Intent intent) {
    if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
      long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
      openDownloadedAttachment(context, downloadId);
    }
  }

  private void openDownloadedAttachment(final Context context, final long downloadId) {
    DownloadManager downloadManager =
        (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    DownloadManager.Query query = new DownloadManager.Query();
    query.setFilterById(downloadId);
    Cursor cursor = downloadManager.query(query);
    if (cursor.moveToFirst()) {
      int downloadStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
      Uri fileUri =
          Uri.parse(cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)));
      String fileUrl = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI));

      try {
        File dstFile =
            FileUtils.createAttachmentCacheFile(context, FileUtils.getFileName(context, fileUri));
        FileUtils.copyFile(context, fileUri, dstFile);
        final Uri uri = FileProvider.getUriForFile(context, BuildConfig.FILE_PROVIDER_AUTHORITY,
            new File(context.getCacheDir(),
                BuildConfig.ATTACHMENT_DOWNLOAD_CACHE_FOLDER + "/" + dstFile.getName()));
        context.getSharedPreferences("AttachmentPreferences", Context.MODE_PRIVATE)
            .edit()
            .putString(fileUrl + "_file_path", dstFile.getPath())
            .commit();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    cursor.close();
  }
}
