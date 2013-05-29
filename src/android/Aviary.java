import java.io.File;

import org.apache.cordova.api.CallbackContext;
import org.apache.cordova.api.CordovaPlugin;
import org.apache.cordova.api.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import com.aviary.android.feather.Constants;
import com.aviary.android.feather.FeatherActivity;
import com.aviary.android.feather.library.utils.StringUtils;

public class Aviary extends CordovaPlugin {

	private static final int ACTION_REQUEST_FEATHER = 1;
	private static final int EXTERNAL_STORAGE_UNAVAILABLE = 1;	
	private static final String LOG_TAG = "Aviary";

	/** Folder name on the sdcard where the images will be saved **/
	private static final String FOLDER_NAME = "aviary";

	private File mGalleryFolder;

    private CallbackContext callbackContext;
	
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {

		mGalleryFolder = createFolders();
		
		if (action.equals("show")) {
			try {
				Log.i(LOG_TAG, action);
				this.callbackContext = callbackContext;
				String source = args.getString(0);
				Uri uri = Uri.parse(source);
				
				// first check the external storage availability
				if ( !isExternalStorageAvilable() ) {
					callbackContext.error("Cannot start aviary, external storage unavailable.");
					return true;
				}
				String mOutputFilePath;
				// create a temporary file where to store the resulting image
				File file = getNextFileName();
				if ( null != file ) {
					mOutputFilePath = file.getAbsolutePath();
				} else {
					callbackContext.error("Cannot start aviary, failed to create a temp file." );
					return true;
				}
				
				// Create the intent needed to start feather
				Class<FeatherActivity> clsAviary = FeatherActivity.class;
				Intent newIntent = new Intent( this.cordova.getActivity(), clsAviary );
				// set the source image uri
				newIntent.setData( uri );

				// pass the uri of the destination image file (optional)
				// This will be the same uri you will receive in the onActivityResult
				//newIntent.putExtra( "output", Uri.parse(picDir + File.separator + "Final_" + this.cordova.getActivity().getIntent().getStringExtra("file")));
			    
				newIntent.putExtra( "output", Uri.parse( "file://" + mOutputFilePath ) );
				// format of the destination image (optional)
				newIntent.putExtra( "output-format", Bitmap.CompressFormat.JPEG.name() );
				// output format quality (optional)
				newIntent.putExtra( "output-quality", 85 );
				// you can force feather to display only a certain tools
				// newIntent.putExtra( "tools-list", new String[]{"ADJUST", "BRIGHTNESS" } );

				// enable fast rendering preview
				newIntent.putExtra( "effect-enable-fast-preview", true );

				// limit the image size
				// You can pass the current display size as max image size because after
				// the execution of Aviary you can save the HI-RES image so you don't need a big
				// image for the preview
				// newIntent.putExtra( "max-image-size", 800 );

				// HI-RES
				// You need to generate a new session id key to pass to Aviary feather
				// this is the key used to operate with the hi-res image ( and must be unique for every new instance of Feather )
				// The session-id key must be 64 char length
				//String mSessionId = StringUtils.getSha256( System.currentTimeMillis() + API_KEY );
				//newIntent.putExtra( "output-hires-session-id", mSessionId );    

				// you want to hide the exit alert dialog shown when back is pressed
				// without saving image first
				// newIntent.putExtra( "hide-exit-unsave-confirmation", true );

				// -- VIBRATION --
				// Some aviary tools use the device vibration in order to give a better experience
				// to the final user. But if you want to disable this feature, just pass
				// any value with the key "tools-vibration-disabled" in the calling intent.
				// This option has been added to version 2.1.5 of the Aviary SDK
				newIntent.putExtra( Constants.EXTRA_TOOLS_DISABLE_VIBRATION, true );
				newIntent.putExtra("tools-list", new String[] {"SHARPNESS", "BRIGHTNESS", "CONTRAST", 
					"SATURATION", "RED_EYE", "CROP", "WHITEN", "DRAWING",
					"TEXT", "BLEMISH", "MEME", "ADJUST", "ENHANCE", "COLORTEMP"});

				// ..and start feather
				//startActivityForResult( newIntent, ACTION_REQUEST_FEATHER  );
				
				cordova.getActivity().startActivityForResult(newIntent, ACTION_REQUEST_FEATHER);
				cordova.setActivityResultCallback(this);
				return true;
			} catch (Exception ex) {
				Log.e(LOG_TAG, ex.toString());
				callbackContext.error("Unknown error occured showing aviary.");
			}
		}

		return false;
	}
	
	@Override
	public void onActivityResult( int requestCode, int resultCode, Intent data ) {
	    if( resultCode == Activity.RESULT_OK ) {
	        switch( requestCode ) {
	            case ACTION_REQUEST_FEATHER:
	                Uri mImageUri = data.getData();
	                try {
		                JSONObject returnVal = new JSONObject();
		                returnVal.put("src", mImageUri.toString());
		                returnVal.put("name", mImageUri.getLastPathSegment());		                
		                this.callbackContext.success(returnVal);
	                } catch(JSONException ex) {
	    				Log.e(LOG_TAG, ex.toString());
	                	this.callbackContext.error(ex.getMessage());
	                }
	                break;
	       }
	    }
	}

	/**
	 * Return a new image file. Name is based on the current time. Parent folder will be the one created with createFolders
	 * 
	 * @return
	 * @see #createFolders()
	 */
	private File getNextFileName() {
		if ( mGalleryFolder != null ) {
			if ( mGalleryFolder.exists() ) {
				File file = new File( mGalleryFolder, "aviary_" + System.currentTimeMillis() + ".jpg" );
				return file;
			}
		}
		return null;
	}

	/**
	 * Try to create the required folder on the sdcard where images will be saved to.
	 * 
	 * @return
	 */
	private File createFolders() {

		File baseDir;

		if ( android.os.Build.VERSION.SDK_INT < 8 ) {
			baseDir = Environment.getExternalStorageDirectory();
		} else {
			baseDir = Environment.getExternalStoragePublicDirectory( Environment.DIRECTORY_PICTURES );
		}

		if ( baseDir == null ) return Environment.getExternalStorageDirectory();

		Log.d( LOG_TAG, "Pictures folder: " + baseDir.getAbsolutePath() );
		File aviaryFolder = new File( baseDir, FOLDER_NAME );

		if ( aviaryFolder.exists() ) return aviaryFolder;
		if ( aviaryFolder.mkdirs() ) return aviaryFolder;

		return Environment.getExternalStorageDirectory();
	}
	
	/**
	 * Check the external storage status
	 * 
	 * @return
	 */
	private boolean isExternalStorageAvilable() {
		String state = Environment.getExternalStorageState();
		if ( Environment.MEDIA_MOUNTED.equals( state ) ) {
			return true;
		}
		return false;
	}
}