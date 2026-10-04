import { BaseApp } from '@zeppos/zml/base-app'

App(
  BaseApp({
    globalData: {
      navState: null
    },
    onCreate() {
      console.log('MapyNav Zepp OS App created')
    },
    onDestroy() {
      console.log('MapyNav Zepp OS App destroyed')
    }
  })
)
