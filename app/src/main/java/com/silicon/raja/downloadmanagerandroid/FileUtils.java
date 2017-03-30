package com.silicon.raja.downloadmanagerandroid;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.OpenableColumns;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by rajamohamed on 30/03/17.
 */

public class FileUtils {

  public static File createAttachmentCacheFile(Context context, String fileName)
      throws IOException {
    File parent = new File(context.getCacheDir(), "downloads");
    parent.mkdirs();
    File dstFile = new File(parent, fileName);

    if (!dstFile.exists()) {
      dstFile.createNewFile();
    }
    return dstFile;
  }

  @TargetApi(Build.VERSION_CODES.JELLY_BEAN) public static String getFileName(Context context, Uri uri) {
    String fileName = null;
    if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
      Cursor cursor = context.getContentResolver()
          .query(uri, null, null, null, null, null);

      if (cursor != null) {
        if (cursor.moveToFirst()) {
          int index = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
          fileName = cursor.getString(index);
        }
        cursor.close();
      }
    } else {
      fileName = uri.getLastPathSegment();
    }
    return fileName;
  }
  public static boolean copyFile(Context context, Uri src, File dst) {
    InputStream inputStream = null;
    FileOutputStream fileOutputStream = null;
    try {
      inputStream = context.getContentResolver().openInputStream(src);
      fileOutputStream = new FileOutputStream(dst);

      if (!dst.exists()) {
        dst.createNewFile();
      }


      byte[] buffer = new byte[1000];
      while (inputStream.read(buffer, 0, buffer.length) >= 0) {
        fileOutputStream.write(buffer, 0, buffer.length);
      }
      return true;
    } catch (IOException e) {
      return false;
    } finally {
      if (inputStream != null) {
        try {
          inputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }

  }
}
