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
      if ((downloadStatus == DownloadManager.STATUS_SUCCESSFUL) && fileUri != null) {
        openDownloadedAttachment(context, fileUri);
      }
    }
    cursor.close();
  }

  private void openDownloadedAttachment(final Context context, Uri attachmentUri) {
    if (attachmentUri != null) {
      Intent openAttachmentIntent = new Intent(Intent.ACTION_VIEW);
      openAttachmentIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      openAttachmentIntent.setDataAndType(attachmentUri,
          context.getContentResolver().getType(attachmentUri));
      openAttachmentIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      try {
        context.startActivity(openAttachmentIntent);
      } catch (ActivityNotFoundException e) {
        Toast.makeText(context, "Unable to open the file", Toast.LENGTH_LONG).show();
      }
    }
  }
}
