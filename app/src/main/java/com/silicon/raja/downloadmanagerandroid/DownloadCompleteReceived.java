package com.silicon.raja.downloadmanagerandroid;

import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Random;

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
        context.getSharedPreferences("AttachmeztPreferences", Context.MODE_PRIVATE)
            .edit()
            .putString(fileUrl + "_file_path", dstFile.getPath())
            .commit();
        openHeadsUpNotification(context, uri, dstFile.getName());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    cursor.close();
  }

  // Add Notification once the download completed
  private void openHeadsUpNotification(Context context, Uri uri, String fileName) {
    Random random = new Random();
    int id = random.nextInt(1000);

    Intent openViewIntent = new Intent(Intent.ACTION_VIEW);
    openViewIntent.setDataAndType(uri, context.getContentResolver().getType(uri));
    openViewIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    openViewIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
    PendingIntent piView =
        PendingIntent.getActivity(context, id, openViewIntent, PendingIntent.FLAG_ONE_SHOT);

    NotificationCompat.Builder builder =
        new NotificationCompat.Builder(context).setContentTitle(fileName)
            .setContentIntent(piView)
            .setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);

    NotificationManager notificationManager =
        (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(id, builder.build());
  }
}
