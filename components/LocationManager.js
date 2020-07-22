import React, {Component} from 'react';
import {
  DeviceEventEmitter,
  NativeModules,
  Text,
  TouchableOpacity,
  View,
} from 'react-native';
import {isPermissionGranted} from './PermissionChecker';
import {Main} from './AppStyle';

const YOUR_API_KEY = 'AIzaSyD8-LGKBXSyCdFWTdeIwrSNUYiwLf7Z8yE';

type LocationCoordinates = {
  latitude: number,
  longitude: number,
  timestamp: number,
};
import axios from 'axios';

class MainPageClass extends Component {
  state = {
    locations: [],
    isWorking: false,
  };

  componentDidMount() {
    const isContained = (location, locationArray) => {
      let result = false;

      let a = location.latitude;
      let b = location.longitude;

      locationArray.map((item) => {
        let a1 = item.latitude;
        let b1 = item.longitude;
        if (a === a1 && b == b1) {
          result = true;
        }
      });

      console.log(result);
      return result;
    };

    const pushInState = (location) => {
      console.log(location);
      if (isContained(location, this.state.locations) === false) {
        console.log(this.state.locations.length);

        if (this.state.locations.length < 100) {
          this.state.locations.push(location);
        } else {
          this.state.locations.shift();
          this.state.locations.push(location);
        }
      }
    };

    this.subscription = DeviceEventEmitter.addListener(
      NativeModules.LocationManager.JS_LOCATION_EVENT_NAME,
      (e: LocationCoordinates) => {
        let location = {
          date: new Date(e.timestamp).toTimeString(),
          latitude: e.latitude,
          longitude: e.longitude,
        };

        console.log(location);

        pushInState(location);

        console.log(
          `Received Coordinates: ${new Date(e.timestamp).toTimeString()}: `,
          e.latitude,
          e.longitude,
        );

        console.log(this.state.locations);
      },
    );
  }

  componentWillUnmount() {
    this.subscription.remove();
  }

  startTrackingLocaton = async () => {
    if (this.state.isWorking == false) {
      if (await isPermissionGranted()) {
        NativeModules.LocationManager.startBackgroundLocation();
        this.state.isWorking = true;
      }
    }
  };

  stopTrackingLocaton = async () => {
    if (this.state.isWorking == true) {
      NativeModules.LocationManager.stopBackgroundLocation();
      this.state.isWorking = false;
    }
  };

  getPlaceId = async () => {
    let result = 'path=';
    await this.state.locations.forEach((value) => {
      let a = value.latitude + ', ' + value.longitude + '|';
      result += a;
    });
    let size = result.length;
    let str = result.slice(0, size - 1);
    console.log(str);

    await axios
      .get(
        'https://roads.googleapis.com/v1/snapToRoads?' +
          str +
          '&interpolate=true&key=' +
          YOUR_API_KEY,
      )
      .then(async (response) => {
        console.log(response.data);
      });
  };

  render() {
    return (
      <View
        style={
          this.state.isWorking ? Main.controlButtonsOn : Main.controlButtonsOff
        }>
        <TouchableOpacity
          style={Main.startButton}
          onPress={this.startTrackingLocaton}>
          <Text style={Main.buttonText}>Start tracking</Text>
        </TouchableOpacity>

        <TouchableOpacity
          style={Main.stopButton}
          onPress={this.stopTrackingLocaton}>
          <Text style={Main.buttonText}>Stop tracking</Text>
        </TouchableOpacity>

        <TouchableOpacity style={Main.stopButton} onPress={this.getPlaceId}>
          <Text style={Main.buttonText}>Get place id</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

export default MainPageClass;
