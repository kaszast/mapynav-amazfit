import { BaseSideService } from '@zeppos/zml/base-side'

AppSideService(
  BaseSideService({
    onInit() {
      console.log('MapyNav AppSideService onInit')
      this.pollTimer = null
      this.lastStateTimestamp = 0
      this.startPolling()
    },

    onRequest(req, res) {
      console.log('AppSideService onRequest:', req.method)
      if (req.method === 'GET_NAV') {
        this.fetchNavData((data) => {
          res(null, { result: data })
        })
      }
    },

    onRun() {
      console.log('MapyNav AppSideService onRun')
    },

    onDestroy() {
      console.log('MapyNav AppSideService onDestroy')
      this.stopPolling()
    },

    startPolling() {
      this.stopPolling()
      this.pollTimer = setInterval(() => {
        this.fetchNavData((data) => {
          if (!data) return

          // Send update if state has changed or new timestamp
          if (data.timestamp !== this.lastStateTimestamp) {
            this.lastStateTimestamp = data.timestamp
            this.call({
              action: 'NAV_UPDATE',
              data: data
            })
          }
        })
      }, 1500)
    },

    stopPolling() {
      if (this.pollTimer) {
        clearInterval(this.pollTimer)
        this.pollTimer = null
      }
    },

    fetchNavData(callback) {
      fetch({
        url: 'http://127.0.0.1:8088/api/nav',
        method: 'GET'
      })
        .then((response) => {
          try {
            const body = typeof response.body === 'string' ? JSON.parse(response.body) : response.body
            callback(body)
          } catch (e) {
            callback(null)
          }
        })
        .catch((err) => {
          callback(null)
        })
    }
  })
)
