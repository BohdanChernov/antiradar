import React, {useState} from 'react';
import MapView, {Marker, Polyline, PROVIDER_GOOGLE} from 'react-native-maps';
import {View} from 'react-native';
import LocationManager from './components/LocationManager';
import {Main} from './components/AppStyle';
import {CameraPositions} from './components/CameraPositions';

const App = () => {
  React.useEffect(() => {
    console.log('Component App is mounted');
    return function cleanup() {
      console.log('Component App is Unmounted');
    };
  }, []);

  let [currentLocation, setCurrentLocation] = useState({
    latitude: 50.4501,
    longitude: 30.5234,
    latitudeDelta: 0.0922,
    longitudeDelta: 0.0421,
  });



  let [listOfCameras, setListOfCameras] = useState([]);

  const fillCameraLocations = async () => {
    let cameraPoints = await CameraPositions();
    setListOfCameras(cameraPoints);
  };
  fillCameraLocations();

  return (
    <View style={Main.container}>
      <MapView
        provider={PROVIDER_GOOGLE}
        style={Main.mapStyle}
        showsUserLocation={true}
        initialRegion={currentLocation}>
        {listOfCameras.map((camera) => {
          return (
            <Marker
              key={camera.id}
              coordinate={{
                latitude: camera.latitude,
                longitude: camera.longitude,
              }}
              // image={require('./images/camera-96.png')}
              title={camera.name}
            />
          );
        })}
      </MapView>
      <LocationManager />
    </View>
  );
};

export default App;
