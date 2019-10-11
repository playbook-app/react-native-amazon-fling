import { NativeEventEmitter, NativeModules } from 'react-native';

const { AmazonFling } = NativeModules;

const eventEmitter = new NativeEventEmitter(AmazonFling);

const listenEvents = () => {
  eventEmitter.addListener('device_list', (event) => {
    console.log('event: device_list', event)
  })
}


export default AmazonFling;
