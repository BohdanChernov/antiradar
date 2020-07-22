import React from 'react';
import {PermissionsAndroid} from 'react-native';

export const isPermissionGranted = async () => {
  let isGranted = false;

  try {
    const granted = await PermissionsAndroid.request(
      PermissionsAndroid.PERMISSIONS.ACCESS_FINE_LOCATION,
      {
        title: 'Location permission',
        message: 'To get your location correctly the app need this permission',
        buttonNeutral: 'Ask Me Later',
        buttonNegative: 'Cancel',
        buttonPositive: 'OK',
      },
    );
    if (granted === PermissionsAndroid.RESULTS.GRANTED) {
      console.log('You can use the location tracker');
      isGranted = true;
    } else {
      console.log(JSON.stringify(granted));
      console.log('Location permission denied');
      return false;
    }
  } catch (err) {
    console.warn(err);
  }

  if (isGranted === true) {
    try {
      const granted = await PermissionsAndroid.request(
        PermissionsAndroid.PERMISSIONS.ACCESS_BACKGROUND_LOCATION,
        {
          title: 'Location permission',
          message:
            'To get your location correctly the app need this permission',
          buttonNeutral: 'Ask Me Later',
          buttonNegative: 'Cancel',
          buttonPositive: 'OK',
        },
      );
      if (granted === PermissionsAndroid.RESULTS.GRANTED) {
        console.log('You can use the location tracker in background');
        return true;
      } else {
        console.log(JSON.stringify(granted));
        console.log('Location permission denied');
        return false;
      }
    } catch (err) {
      console.warn(err);
    }
  }

  return false;
};
