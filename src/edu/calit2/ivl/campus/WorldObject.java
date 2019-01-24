package edu.calit2.ivl.campus;

import android.util.Log;
import min3d.Shared;
import min3d.core.Object3dContainer;
import min3d.parser.IParser;
import min3d.parser.Parser;





/**
 * Wrapper for 3d object filename, GPS location, 3d object
 * 
 *
 */
public class WorldObject {
	
	/**
	 * Initialize 3d object with position
	 * 
	 * @param fileName
	 * @param latitude degrees N
	 * @param longitude degrees W
	 */
	public WorldObject(String fileName, double latitude, double longitude) {
		
		_fileName = fileName;
		_location = new GPSLocation(latitude, longitude);
		
		initObject3d();

	}
	
	
	public void initObject3d() {
		long start = System.currentTimeMillis();
		IParser parser = Parser.createParser(Parser.Type.OBJ,
				Shared.context().getResources(), Administration.PACKAGE_NAME + ":raw/" + _fileName, true);
		parser.parse();
		
		_object3d = parser.getParsedObject();
		
		Log.d("campus","Minutes to parse " + _fileName + ": " + (System.currentTimeMillis() - start) / (60000));
		
		_object3d.scale().x = _object3d.scale().y = _object3d.scale().z = FEET_TO_METERS;
		_object3d.position().y = 0;

		if(!Administration.DISABLE_GPS) {
			_object3d.position().x = _location.positionX();
			_object3d.position().z = _location.positionZ();
		} else {
			_object3d.position().x = _object3d.position().z = 0;
		}
	}
	
	
	public Object3dContainer object3d() {
		return _object3d;
	}
	
	

	String _fileName;
	GPSLocation _location;
	Object3dContainer _object3d;
	
    private static final float FEET_TO_METERS = 0.3048f;

	
}