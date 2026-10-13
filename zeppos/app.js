import { BaseApp } from '@zeppos/zml/base-app'

App(
  BaseApp({
    globalData: {
      navState: null
    },
    onCreate() {
      console.log('NaviHUD Zepp OS App created')
    },
    onDestroy() {
      console.log('NaviHUD Zepp OS App destroyed')
    }
  })
)
