import { NativeEventEmitter, NativeModules } from 'react-native'

const { AmazonFling } = NativeModules

const eventEmitter = new NativeEventEmitter( AmazonFling )


const listenEvents = () => {

}
module.exports = {
  findFireDevices () {
    AmazonFling.startSearch()
  },
  stopFindFireDevices () {
    AmazonFling.stopSearch()
  },
  addDeviceListener ( cb ) {
    eventEmitter.addListener( 'device_list', ( event ) => {
      console.log( 'event: device_list', event )
      cb( event )
    } )
  },
  fling ( target, url, title ) {
    AmazonFling.fling( target, url, title )
  },
  flingPause ( target ) {
    AmazonFling.doPause(target)
  },
  flingPlay ( target ) {
    AmazonFling.doPlay(target)
  },
  flingStop ( target ) {
    AmazonFling.doStop(target)
  },
  flingSeek ( target, position ) {
    // Note, position must be a string for now. RN can't handle other argument types
    AmazonFling.doSeek(target, position)
  }
}


