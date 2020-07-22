import {Dimensions, StyleSheet} from 'react-native';

export const Main = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  mapStyle: {
    width: Dimensions.get('window').width,
    height: '75%',
  },
  controlButtonsOff: {
    height: '25%',
    width: Dimensions.get('window').width,
    backgroundColor: '#ebfce7',
  },
  controlButtonsOn: {
    height: '25%',
    width: Dimensions.get('window').width,
    backgroundColor: '#54e045',
  },
  startButton: {
    elevation: 8,
    backgroundColor: '#54e045',
    borderRadius: 10,
    paddingVertical: 10,
    paddingHorizontal: 12,
    margin: 5,
  },
  stopButton: {
    elevation: 8,
    backgroundColor: '#d90909',
    borderRadius: 10,
    paddingVertical: 10,
    paddingHorizontal: 12,
    margin: 5,
  },

  buttonText: {
    fontSize: 18,
    color: '#fff',
    fontWeight: 'bold',
    alignSelf: 'center',
    textTransform: 'uppercase',
  },
});
