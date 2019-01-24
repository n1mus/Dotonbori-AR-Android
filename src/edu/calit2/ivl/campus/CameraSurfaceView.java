package edu.calit2.ivl.campus;

import java.io.IOException;
import java.util.List;

import min3d.Shared;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Container for camera feed
 *
 */
class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback { 
    SurfaceHolder mHolder; 
    Camera mCamera; 

    CameraSurfaceView() { 
        super(Shared.context()); 

        // Install a SurfaceHolder.Callback so we get notified when the 
        // underlying surface is created and destroyed. 
        mHolder = getHolder(); 
        mHolder.addCallback(this); 
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS); 
 
    } 

    public void surfaceCreated(SurfaceHolder holder) { 
        // The Surface has been created, acquire the camera and tell it where 
        // to draw. 
        mCamera = Camera.open();
    	try {
			mCamera.setPreviewDisplay(holder);
			mCamera.startPreview();
		} catch (IOException e) {
			Log.d("ERROR","Error starting camera preview");
		} 
    } 

    public void surfaceDestroyed(SurfaceHolder holder) { 
        // Surface will be destroyed when we return, so stop the preview. 
        // Because the CameraDevice object is not a shared resource, it's very 
        // important to release it when the activity is paused. 
        if(mCamera != null) {
        	mCamera.stopPreview(); 
        	mCamera.release();
            mCamera = null; 
        }
        
    } 

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { 
        // Now that the size is known, set up the camera parameters and begin 
        // the preview. 
        Camera.Parameters parameters = mCamera.getParameters(); 
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();
        Camera.Size cs = sizes.get(0); 
        parameters.setPreviewSize(cs.width, cs.height);
        mCamera.setParameters(parameters);
        mCamera.startPreview(); 
    }

}