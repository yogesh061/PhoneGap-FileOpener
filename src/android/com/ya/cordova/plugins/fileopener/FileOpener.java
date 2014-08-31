package com.ya.cordova.plugins.fileopener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class FileOpener extends CordovaPlugin {
	private static final String ASSETS = "file:///android_asset/";
	public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		
		if (action.equals("open")) {
			try {

				return this._open(args.getString(0), args.getString(1), callbackContext);

			} catch (JSONException e) {

				JSONObject errorObj = new JSONObject();
				errorObj.put("status", PluginResult.Status.JSON_EXCEPTION.ordinal());
				errorObj.put("message", e.getMessage());
				callbackContext.error(errorObj);
				return false;
			}

		} else {

			JSONObject errorObj = new JSONObject();
			errorObj.put("status", PluginResult.Status.INVALID_ACTION.ordinal());
			errorObj.put("message", "Invalid action");
			callbackContext.error(errorObj);
			return false;

		}

	}
	
	private boolean _open(String url, String contentType, CallbackContext callbackContext) throws JSONException {
		Intent intent = null;
		Uri uri = Uri.parse(url);
		if(url.contains(ASSETS)) {
			// get file path in assets folder
            String filepath = url.replace(ASSETS, "");
            //get actual filename from path as command to write to internal storage doesn't like folders
            String filename = filepath.substring(filepath.lastIndexOf("/")+1, filepath.length());
         // Don't copy the file if it already exists
            File fp = new File(this.cordova.getActivity().getFilesDir() + "/" + filename);
            if (!fp.exists()) {
                try {
					this.copy(filepath, filename);
				} catch (IOException e) {
				
					e.printStackTrace();
					return false;
				}
            }
         // change uri to be to the new file in internal storage
            uri = Uri.parse("file://" + this.cordova.getActivity().getFilesDir() + "/" + filename);
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, contentType);
            
		}else{
			intent = new Intent(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, contentType);
            
		}
		this.cordova.getActivity().startActivity(intent);
		return true;
	}
	private void copy(String fileFrom, String fileTo) throws IOException {
        // get file to be copied from assets
        InputStream in = this.cordova.getActivity().getAssets().open(fileFrom);
        // get file where copied too, in internal storage.
        // must be MODE_WORLD_READABLE or Android can't play it
        FileOutputStream out = this.cordova.getActivity().openFileOutput(fileTo, Context.MODE_WORLD_READABLE);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
            out.write(buf, 0, len);
        in.close();
        out.close();
    }
}
