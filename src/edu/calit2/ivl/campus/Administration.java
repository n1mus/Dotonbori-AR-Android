package edu.calit2.ivl.campus;

public class Administration {

	static final String PACKAGE_NAME = "edu.calit2.ivl.campus";
	static final String FILE_NAME = "untitled_obj";
	
	// GPS coords, degrees N degrees W
	static final double CYBERMEDIA_CENTER_LAT = 34.805056, CYBERMEDIA_CENTER_LONG = -135.455642;
	static final double KANSAI_UNI_LAT = 34.878038, KANSAI_UNI_LONG = -135.575539;
	static final double CAMINO_TRANQUILO_LAT = 32.863493, CAMINO_TRANQUILO_LONG = 117.217816;
	static final double CALIT2_LAT = 32.882844, CALIT2_LONG = 117.235078;
	
	// debug
	static final boolean DISABLE_GPS = true;
	static final boolean DISABLE_PHYSICAL_CAMERA = true;
	static final boolean AERIAL_VIEW = true;
	
	// customize view
	static final int AERIAL_HEIGHT = 300;
	static final int FRUSTUM_DEPTH = 2000;
	
	//
	static final int SENSOR_BUFFER_SIZE = 4;
	
	
	
	
	// measurements of galaxy tablet
	private static final float SCREEN_WIDTH = 0.254f, SCREEN_HEIGHT = 0.173f;
}
