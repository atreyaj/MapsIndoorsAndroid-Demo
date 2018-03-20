package com.mapsindoors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.mapspeople.Location;
import com.mapspeople.LocationDisplayRule;
import com.mapspeople.LocationDisplayRules;
import com.mapspeople.MapControl;
import com.mapspeople.MapsIndoors;
import com.mapspeople.dbglog;
import com.mapspeople.errors.MIError;


public class MainActivity extends AppCompatActivity
{
    public static final String TAG = MainActivity.class.getSimpleName();

    SupportMapFragment mapFragment;
    GoogleMap          mGoogleMap;
    MapControl         myMapControl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        // MapsIndoors SDK debug setup
        {
            // Enable/disable internal debug messages / assertions
            dbglog.useDebug( BuildConfig.DEBUG );

            // Add a log tag prefix to the MI SDK logs
            dbglog.setCustomTagPrefix( BuildConfig.FLAVOR + "_" );
        }

        // Initialize MapsIndoors Here
        MapsIndoors.initialize(
                getApplicationContext(),
                getString(R.string.mapsindoors_api_key),
                getString( R.string.google_maps_key )
        );

        //
	    MapsIndoors.synchronizeContent( error -> {
		    if(dbglog.isDebugMode())
		    {
                if( error == null )
                {
                    dbglog.LogI( TAG, "MapsIndoors.synchronizeContent: DONE" );
                }
                else
                {
                    dbglog.LogI( TAG, "MapsIndoors.synchronizeContent ERROR -> " + error.message );
                }
		    }
	    });

        //
        mapFragment = ((SupportMapFragment) getSupportFragmentManager().findFragmentById( R.id.map_fragment ));

        //
        mapFragment.getMapAsync( googleMap -> {

            mGoogleMap = googleMap;

            //
            mGoogleMap.moveCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( 57.05813067, 9.95058065 ), 13.0f ) );

            setupMapsIndoors();
        } );
    }

    void setupMapsIndoors() {

        myMapControl = new MapControl(this, mapFragment, mGoogleMap );

        //
        {
            LocationDisplayRules drs = myMapControl.getDisplayRules();

            LocationDisplayRules displayRules = new LocationDisplayRules();
            final LocationDisplayRule ruleA = new LocationDisplayRule.Builder("<Office>").
                    setBitmapIcon( R.drawable.misdk_dot_black ).
                    setShowLabel( false ).
                    setZOn( 16 ).
                    build();

            displayRules.add( ruleA );
            myMapControl.addDisplayRules( displayRules );
        }
        {
            LocationDisplayRules drs = myMapControl.getDisplayRules();

            final LocationDisplayRule ruleA = new LocationDisplayRule.Builder( "<Office2>" ).

                    //setBitmapIcon( R.drawable.misdk_dot_black ).
                    //setBitmapIcon( R.drawable.misdk_circle_shape ).
                    setBitmapIcon( R.drawable.ic_flight_takeoff_black_24dp ).
                    setPOISize( 40 ).
                    //setShowLabel( true ).
                    setZOn( 16 ).
                    build();

            myMapControl.getDisplayRules().add( ruleA );
        }

        myMapControl.setOnMarkerClickListener( marker -> {

            Location aLocation = myMapControl.getLocation( marker );
            if( aLocation != null )
            {
                LocationDisplayRules lds = myMapControl.getDisplayRules();

                String locationName = aLocation.getName();
                LocationDisplayRule locationDR = aLocation.getDisplayRule();

//                Marker m = aLocation.getMarker();
//                if( m != null )
//                {
//                    aLocation.setMarker( null );
//                    m.remove();
//                }

                aLocation.setMarkerAsSetup( false );
                aLocation.setDisplayRule( lds.getRule( "<Office2>" ) );
                aLocation.setVisible( false );
                aLocation.updateView( mGoogleMap );

                LocationDisplayRule locationDRN = aLocation.getDisplayRule();

                if(BuildConfig.DEBUG){}
            }

            return true;
        } );

        myMapControl.init( errorCode -> {
            if( errorCode == null )
            {
                runOnUiThread( () -> {

                    //
                    myMapControl.selectFloor( 1 );

	                LocationDisplayRules drs = myMapControl.getDisplayRules();

                    //
                    mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom( new LatLng( 57.05813067, 9.95058065 ), 19f ) );
                });
            }
        });
    }
}
