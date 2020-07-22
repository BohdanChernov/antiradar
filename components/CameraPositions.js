import React from 'react';
import axios from 'axios';

export const CameraPositions = async () => {
  let answerCameraList;
  await axios
    .get('http://192.168.0.103:8080/getCamerasCoordinates')
    .then(async (response) => {
      let cameraPoints = await response.data;
      answerCameraList = cameraPoints;
    })
    .catch((error) => {
      console.log(error);
    });
  return answerCameraList;
};
