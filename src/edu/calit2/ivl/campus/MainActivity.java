/* TODO align GPS
 * TODO find out which sensors
 * TODO textures TODO lighting
 * TODO fix at landscape TODO let switch orientation
 * TODO camera feed background
 */
package edu.calit2.ivl.campus;
import min3d.core.Object3dContainer;
import min3d.core.RendererActivity;
import min3d.parser.IParser;
import min3d.parser.Parser;
import min3d.vos.Light;
import android.os.Bundle;
import android.app.Activity;
import android.graphics.PixelFormat;
import android.util.Log;
import android.view.Menu;
import android.view.ViewGroup.LayoutParams;

public class MainActivity extends RendererActivity {
    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _cm = new CameraManager(scene);
    }
    
    @Override
    public void initScene() {
    	WorldObject thing;
    	
		scene.addChild((thing = new WorldObject(Administration.FILE_NAME,GPSLocation.BASE_LAT,GPSLocation.BASE_LONG)).object3d());
		scene.backgroundColor().setAll(0x00000000);
		scene.lights().add(new Light());
		scene.lights().add(new Light(1,0,0));
		scene.lights().add(new Light(0,1,0));
		scene.lights().add(new Light(0,0,1));
		scene.lights().add(new Light(-1,0,0));
		scene.lights().add(new Light(0,-1,0));
		scene.lights().add(new Light(0,0,-1));
		
		
		
		Log.v("~!@#$%^&*()","camera position " + scene.camera().position.x + " || " + scene.camera().position.y + " || " + scene.camera().position.z);
		Log.v("~!@#$%^&*()","camera target " + scene.camera().target.x + " || " + scene.camera().target.y + " || " + scene.camera().target.z);
		Log.v("~!@#$%^&*()","thing " + thing.object3d().position().x + " || " + thing.object3d().position().y + " || " + thing.object3d().position().z );
		
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	_cm.onResume();
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	_cm.onPause();
    }
    

    
    
    
    @Override
    public void onCreateSetContentView() {
    	if(!Administration.DISABLE_PHYSICAL_CAMERA) {
    		setContentView(new CameraSurfaceView());
    		addContentView(_glSurfaceView,new LayoutParams (LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    	} else {
    		setContentView(_glSurfaceView);
    	}
   
    }
	
    @Override 
	protected void glSurfaceViewConfig()
    {
        _glSurfaceView.setEGLConfigChooser(8,8,8,8, 16, 0);
        _glSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

    }
    
    
    CameraManager _cm;
}