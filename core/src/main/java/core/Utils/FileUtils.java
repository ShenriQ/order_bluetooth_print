package core.Utils;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.format.Formatter;
import android.webkit.MimeTypeMap;


import com.app.fooddeliverysystem.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;

import core.Core;


public class FileUtils {
    public static File getFileFromUri(Context context, Uri uri) throws URISyntaxException, IOException {
        if (getPathFromUri(context, uri) != null) {
            String path = getPathFromUri(context, uri);
            return path != null ? new File(path) : FileUtil.from(Core.getContext(), uri);
        } else return null;
    }

    public static String getPathFromUri(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 21 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                try {
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                } catch (NumberFormatException e) {
                    return null;
                }

            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }


    public static void downloadFile(String Url, String fileName) {
        File direct = new File(Environment.getExternalStorageDirectory()
                + "/" + Core.getContext().getString(R.string.app_name));

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) Core.getContext().getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(Url);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle(fileName)
                .setDescription(Core.getContext().getString(R.string.app_name))
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/" + Core.getContext().getString(R.string.app_name) + "/" +fileName);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("*/*");//Allow all types of apps to show for opening this file
        mgr.enqueue(request);

    }

    //return docx from Uri of url
    public static String getMimeType(Context context, Uri uri) {
        String extension;

        //Check uri format to avoid null
        if (uri.getScheme().equals(ContentResolver.SCHEME_CONTENT)) {
            //If scheme is a content
            final MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(context.getContentResolver().getType(uri));
        } else {
            //If scheme is a File
            //This will replace white spaces with %20 and also other special characters. This will avoid returning null values on file name with spaces and special characters.
            extension = MimeTypeMap.getFileExtensionFromUrl(Uri.fromFile(new File(uri.getPath())).toString());

        }

        return extension;
    }

    //return docx from File
    public static String getExtension(File file) {
        String filenameArray[] = file.getName().split("\\.");
        return filenameArray[filenameArray.length - 1].toLowerCase();
    }

    //return 71 KB
    public static String getFileSize(File file) {
        String formattedSize = Formatter.formatFileSize(Core.getContext(), file.length());
        String[] splitArray = formattedSize.split("(?x)                  # verbose regex mode on                    \n" +
                "(?<=                  # Assert that the previous character is... \n" +
                " \\p{L}               # a letter                                 \n" +
                ")                     # and                                      \n" +
                "(?=                   # that the next character is...            \n" +
                " \\p{N}               # a digit.                                 \n" +
                ")                     #                                          \n" +
                "|                     # Or                                       \n" +
                "(?<=\\p{N})(?=\\p{L}) # vice versa");
        if (splitArray.length == 1)
            splitArray = formattedSize.split(" ");
        //Detect Type: KB/MB
        //length-2 is size, length-1 is type
        double size = Double.valueOf(splitArray[splitArray.length - 2]);
        String measurement = splitArray[splitArray.length - 1].toUpperCase();

        return size + " " + measurement;
    }

//    public static int getFileIcon(String fileName) {
//        String filenameArray[] = fileName.split("\\.");
//        String extension = filenameArray[filenameArray.length - 1].toLowerCase();
//        ArrayList<String> imageExtension = new ArrayList<>(Arrays.asList(new String[]{"jpg", "jpeg", "tiff", "png", "tif"}));
//        if (imageExtension.contains(extension))
//            return R.mipmap.image_icon;
//        ArrayList<String> audioExtension = new ArrayList<>(Arrays.asList(new String[]{"mp3", "wav", "amr"}));
//        if (audioExtension.contains(extension))
//            return R.mipmap.ic_launcher;
//        ArrayList<String> videoExtension = new ArrayList<>(Arrays.asList(new String[]{"mp4", "mpeg", "mpg", "avi", "flv", "mov"}));
//        if (videoExtension.contains(extension))
//            return R.mipmap.video_icon;
//        ArrayList<String> wordExtension = new ArrayList<>(Arrays.asList(new String[]{"doc", "docx"}));
//        if (wordExtension.contains(extension))
//            return R.mipmap.docx_icon;
//        ArrayList<String> archiveExtension = new ArrayList<>(Arrays.asList(new String[]{"zip", "rar"}));
//        if (archiveExtension.contains(extension))
//            return R.mipmap.zip_icon;
//        if (extension.matches("pptx"))
//            return R.mipmap.ppt_icon;
//        if (extension.matches("xlsx"))
//            return R.mipmap.xlsx_icon;
//        if (extension.matches("pdf"))
//            return R.mipmap.pdf_icon;
//        if (extension.matches("txt"))
//            return R.mipmap.ic_launcher;
//        return R.mipmap.ic_launcher;
//    }

//    public static void previewFile(BaseActivity context, Attachment attachment) {
//        String filenameArray[] = attachment.fileName.split("\\.");
//        String extension = filenameArray[filenameArray.length - 1].toLowerCase();
//        ArrayList<String> imageExtension = new ArrayList<>(Arrays.asList(new String[]{"jpg", "jpeg", "tiff", "png", "tif"}));
//        if (imageExtension.contains(extension)) {
//            Intent photoViewer = new Intent(context, PhotoViewerActivity.class);
//            photoViewer.putExtra("url", attachment.fileUrl);
//            context.startActivity(photoViewer);
//            return;
//        }
//        ArrayList<String> audioExtension = new ArrayList<>(Arrays.asList(new String[]{"mp3", "wav", "amr"}));
//        if (audioExtension.contains(extension)) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        ArrayList<String> videoExtension = new ArrayList<>(Arrays.asList(new String[]{"mp4", "mpeg", "mpg", "avi", "flv", "mov"}));
//        if (videoExtension.contains(extension)) {
//            Intent videoViewer = new Intent(context, VideoPlayerActivity.class);
//            videoViewer.putExtra("url", attachment.fileUrl);
//            videoViewer.putExtra("name", attachment.fileName);
//            context.startActivity(videoViewer);
//            return;
//        }
//        ArrayList<String> wordExtension = new ArrayList<>(Arrays.asList(new String[]{"doc", "docx"}));
//        if (wordExtension.contains(extension)) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        ArrayList<String> archiveExtension = new ArrayList<>(Arrays.asList(new String[]{"zip", "rar"}));
//        if (archiveExtension.contains(extension)) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        if (extension.matches("pptx")) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        if (extension.matches("xlsx")) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        if (extension.matches("pdf")) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//        if (extension.matches("txt")) {
//            context.flashbar("Downloading will start shortly..");
//            FileUtils.downloadFile(attachment.fileUrl, attachment.fileName);
//            return;
//        }
//    }

    private static boolean isValidSize(double size, String fileType) {
        switch (fileType) {
            case "Image": {
                if (size <= 10)
                    return true;
                else {
                    AppUtils.Toast("Image size is exceeding 10 MB");
                    return false;
                }
            }
            case "Audio": {
                if (size <= 200)
                    return true;
                else {
                    AppUtils.Toast("Audio size is exceeding 200 MB");
                    return false;
                }
            }
            case "Video": {
                if (size <= 300)
                    return true;
                else {
                    AppUtils.Toast("Video size is exceeding 300 MB");
                    return false;
                }
            }
            case "Archive": {
                if (size <= 200)
                    return true;
                else {
                    AppUtils.Toast("Archive size is exceeding 200 MB");
                    return false;
                }
            }
            default:
                if (size <= 200)
                    return true;
                else {
                    AppUtils.Toast("File size is exceeding 200 MB");
                    return false;
                }
        }

    }

    private static String validateFileExtension(String extension) {
//        "jpg", "jpeg", "tiff", "png", "tif", "mp3", "wav", "amr", "mp4", "mpeg", "mpg", "avi", "flv", "mov", "txt", "pdf", "doc", "docx", "xlsx", "pptx", "zip", "rar"}));
        ArrayList<String> imageExtension = new ArrayList<>(Arrays.asList("jpg", "jpeg", "tiff", "png", "tif"));
        if (imageExtension.contains(extension))
            return "Image";
        ArrayList<String> audioExtension = new ArrayList<>(Arrays.asList("mp3", "wav", "amr"));
        if (audioExtension.contains(extension))
            return "Audio";
        ArrayList<String> videoExtension = new ArrayList<>(Arrays.asList("mp4", "mpeg", "mpg", "avi", "flv", "mov"));
        if (videoExtension.contains(extension))
            return "Video";
        ArrayList<String> wordExtension = new ArrayList<>(Arrays.asList("doc", "docx"));
        if (wordExtension.contains(extension))
            return "Word";
        ArrayList<String> archiveExtension = new ArrayList<>(Arrays.asList("zip", "rar"));
        if (archiveExtension.contains(extension))
            return "Archive";
        if (extension.matches("pptx"))
            return "PowerPoint";
        if (extension.matches("xlsx"))
            return "Excel";
        if (extension.matches("pdf"))
            return "Pdf";
        if (extension.matches("txt"))
            return "Txt";
        return null;
    }

    public static boolean validateFile(File file) {
        String extension = FileUtils.getExtension(file);
        long sizeInBytes = file.length();
        //Readable form like 71KB
        String formattedSize = Formatter.formatFileSize(Core.getContext(), file.length());
        String[] splitArray = formattedSize.split("(?x)                  # verbose regex mode on                    \n" +
                "(?<=                  # Assert that the previous character is... \n" +
                " \\p{L}               # a letter                                 \n" +
                ")                     # and                                      \n" +
                "(?=                   # that the next character is...            \n" +
                " \\p{N}               # a digit.                                 \n" +
                ")                     #                                          \n" +
                "|                     # Or                                       \n" +
                "(?<=\\p{N})(?=\\p{L}) # vice versa");
        if (splitArray.length == 1)
            splitArray = formattedSize.split(" ");
        //Detect Type: KB/MB
        //length-2 is size, length-1 is type
        double size = Double.valueOf(splitArray[splitArray.length - 2]);
        String measurement = splitArray[splitArray.length - 1].toUpperCase();
        String fileType = validateFileExtension(extension);
        if (fileType != null) {
            if (measurement.matches("B") || measurement.matches("KB") || measurement.matches("MB")) {
                if (measurement.matches("MB")) {
                    if (isValidSize(size, fileType)) {
                        //ready to upload file
                        return true;
                    } else {
                        AppUtils.Toast("File is too big");
                        return false;
                    }
                } else {//Size in KB or Bytes
                    //ready to upload file
                    return true;
                }
            } else {
                AppUtils.Toast("File is too big");
                return false;
            }
        } else {
            AppUtils.Toast("Unsupported File Type");
            return false;
        }
    }

    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title" + System.currentTimeMillis(), null);
        return Uri.parse(path);
    }

    public static File getFileFromBitmap(Bitmap bitmap) {
        File imageFile = null;
        Uri bitmapUri = FileUtils.getImageUri(Core.getContext(), bitmap);
        try {
            imageFile = FileUtils.getFileFromUri(Core.getContext(), bitmapUri);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageFile;
    }
}
