package edu.calit2.ivl.campus;

import java.util.LinkedList;

import min3d.Shared;
import min3d.core.Scene;
import min3d.vos.FrustumManaged;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Manages orientation & location of camera
 *
 */
public class CameraManager implements SensorEventListener {


	public CameraManager(Scene scene) {
		
		_azimuthBuffer = new Buffer(Administration.SENSOR_BUFFER_SIZE);
		_pitchBuffer = new Buffer(Administration.SENSOR_BUFFER_SIZE);
		
		_scene = scene;
		
		_scene.camera().frustum = new FrustumManaged(0,0,0,1f,Administration.FRUSTUM_DEPTH,null);
		
		if(!Administration.AERIAL_VIEW) {
			_scene.camera().position.y = 2;
		} else {
			_scene.camera().position.y = Administration.AERIAL_HEIGHT;
		}
		
		if(!Administration.DISABLE_GPS) {
			updateCameraLocation();
		} else {
			_scene.camera().position.x = _scene.camera().position.z = 0;
			_scene.camera().target.x = _scene.camera().target.y = _scene.camera().target.z = 0;
		}
		
		_location = new GPSLocation();

		
		initSensors();
		
	}
	
	private void initSensors() {
		_sensorManager = (SensorManager) Shared.context().getSystemService(Context.SENSOR_SERVICE);
		_accelerometer = _sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		_geomagnetic = _sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		_orientation = _sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);



		_locationManager = (LocationManager) Shared.context().getSystemService(Context.LOCATION_SERVICE);
		_androidLocation = _locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		_locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if(GPSLocation.isBetterLocation(location,_androidLocation)) {
					_androidLocation = location;
					_location.update(location);
					updateCameraLocation();
				}
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		switch(event.sensor.getType()) {

		case(Sensor.TYPE_MAGNETIC_FIELD):
			_geomagneticVals = event.values.clone();
		break;

		case(Sensor.TYPE_ACCELEROMETER):
			_gravityVals = event.values.clone();
		break;

		case(Sensor.TYPE_ORIENTATION):
			_orientationVals3 = event.values.clone();
		break;

		}


		if( SensorManager.getRotationMatrix(_rotationMatrix,_inclinationMatrix,_gravityVals,_geomagneticVals) ) {

			SensorManager.getOrientation(_rotationMatrix,_orientationVals);
			_orientationVals[0] = _orientationVals[0];
			_orientationVals[1] = _orientationVals[1];
			_orientationVals[2] = _orientationVals[2];

			SensorManager.remapCoordinateSystem(_rotationMatrix,SensorManager.AXIS_MINUS_Y,SensorManager.AXIS_X,_rotationMatrix2);
			SensorManager.getOrientation(_rotationMatrix2,_orientationVals2);
			_orientationVals2[0] = _orientationVals2[0];
			_orientationVals2[1] = _orientationVals2[1];
			_orientationVals2[2] = _orientationVals2[2];
		}

		updateCameraOrientation();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int i) {}


	public void onResume() {
		_sensorManager.registerListener(this,_geomagnetic, SensorManager.SENSOR_DELAY_NORMAL);
		_sensorManager.registerListener(this,_orientation, SensorManager.SENSOR_DELAY_NORMAL);
		_sensorManager.registerListener(this,_accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

		if(!Administration.DISABLE_GPS) {
			_locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0, _locationListener);
		}
	}


	public void onPause() {
		_sensorManager.unregisterListener(this);

		_locationManager.removeUpdates(_locationListener);
	}

	public void updateCameraOrientation() {

		double azimuth = Math.toRadians(_orientationVals3[0]);
		double pitch = _orientationVals2[2] + 3 * Math.PI / 2;
		
		_azimuthBuffer.put(azimuth);
		_pitchBuffer.put(pitch);
		
		azimuth = _azimuthBuffer.average();
		pitch = _pitchBuffer.average();

		_scene.camera().target.x = _scene.camera().position.x + (float) (CAM_AXIS_LEN * Math.cos(azimuth) * -Math.cos(pitch));
		_scene.camera().target.y = _scene.camera().position.y + (float) (CAM_AXIS_LEN * Math.sin(pitch));
		_scene.camera().target.z = _scene.camera().position.z + (float) (CAM_AXIS_LEN * -Math.sin(azimuth) * Math.cos(pitch));
	}


	void updateCameraLocation() {

		
		_scene.camera().position.x = _location.positionX();
		_scene.camera().position.z = _location.positionZ();
	}
	

	private static final int CAM_AXIS_LEN = 5;

	private Scene _scene;

	private SensorManager _sensorManager;
	private Sensor _accelerometer, _geomagnetic, _orientation;


	private float[] _inclinationMatrix = new float[16];
	private float[] _gravityVals = new float[3];
	private float[] _geomagneticVals = new float[3];

	private float[] _orientationVals = new float[3]; // portrait orientation (radians)
	private float[] _rotationMatrix = new float[16];

	private float[] _orientationVals2 = new float[3]; // landscape orientation (radians)
	private float[] _rotationMatrix2 = new float[16];

	private float[] _orientationVals3 = new float[3]; // deprecated (degrees)







	LocationManager _locationManager;
	LocationListener _locationListener;





	Location _androidLocation;
	GPSLocation _location;
	
	
	Buffer _azimuthBuffer, _pitchBuffer;

}






class Buffer {

	public Buffer(int size) {
		_queue = new LinkedList<Double>();
		_size = size;
	}
	
	public void put(double val) {
		if(_queue.size() > _size) {
			_sum -= _queue.removeFirst();
		}
		
		_sum += val;
		_queue.add(val);
	}

	
	public float average() {
		
		return (float) _sum / _size;
	}
	
	
	
	private LinkedList<Double> _queue;
	private int _size;
	private double _sum;
}







