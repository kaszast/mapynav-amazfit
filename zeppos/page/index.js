import { BasePage } from '@zeppos/zml/base-page'
import * as hmUI from '@zos/ui'
import {
  Vibrator,
  Buzzer,
  Time,
  VIBRATOR_SCENE_NOTIFICATION,
  VIBRATOR_SCENE_DURATION
} from '@zos/sensor'
import {
  setWakeUpRelaunch,
  setPageBrightTime,
  resetPageBrightTime
} from '@zos/display'

const ICON_MAP = {
  straight: 'straight.png',
  turn_left: 'turn_left.png',
  turn_right: 'turn_right.png',
  slight_left: 'slight_left.png',
  slight_right: 'slight_right.png',
  sharp_left: 'sharp_left.png',
  sharp_right: 'sharp_right.png',
  u_turn: 'u_turn.png',
  roundabout: 'roundabout.png',
  destination: 'destination.png',
  unknown: 'unknown.png'
}

Page(
  BasePage({
    state: {
      lastManeuver: '',
      lastVibeTimestamp: 0
    },
    clockTimer: null,

    build() {
      console.log('MapyNav Giant HUD Page build')

      this.initDisplayManagement()
      this.initSensors()
      this.initUI()
      this.updateClock()
      this.clockTimer = setInterval(() => {
        this.updateClock()
      }, 1000)
      this.requestInitialData()
    },

    initDisplayManagement() {
      // NEVER exit to watchface when screen goes off -> Return to this page upon wake
      try {
        setWakeUpRelaunch({ relaunch: true })
      } catch (e) {
        console.log('setWakeUpRelaunch error:', e)
      }
    },

    initSensors() {
      // Vibrator Sensor
      try {
        this.vibrator = new Vibrator()
      } catch (e) {
        console.log('Vibrator sensor init error:', e)
        this.vibrator = null
      }

      // Buzzer / Speaker Sensor
      try {
        this.buzzer = new Buzzer()
      } catch (e) {
        console.log('Buzzer sensor init error:', e)
        this.buzzer = null
      }

      // Time Sensor
      try {
        this.timeSensor = new Time()
      } catch (e) {
        console.log('Time sensor init error:', e)
        this.timeSensor = null
      }
    },

    initUI() {
      // 480x480 AMOLED Screen Layout - Giant High-Visibility Elements

      // 1. Giant Turn Icon (210x210 px, centered at X: 135, Y: 10..220)
      this.iconWidget = hmUI.createWidget(hmUI.widget.IMG, {
        x: 135,
        y: 10,
        w: 210,
        h: 210,
        src: 'unknown.png'
      })

      // 2. Giant Distance Text (72 px Bold White, Y: 224..298)
      this.distanceWidget = hmUI.createWidget(hmUI.widget.TEXT, {
        x: 10,
        y: 224,
        w: 460,
        h: 74,
        color: 0xFFFFFF,
        text_size: 72,
        align_h: hmUI.align.CENTER_H,
        text: 'Készenlét'
      })

      // 3. Street Name / Maneuver (32 px Bold Amber, Y: 302..364)
      this.streetWidget = hmUI.createWidget(hmUI.widget.TEXT, {
        x: 20,
        y: 302,
        w: 440,
        h: 62,
        color: 0xFACC15, // Bright Amber
        text_size: 32,
        align_h: hmUI.align.CENTER_H,
        text_style: hmUI.text_style.WRAP,
        text: 'Indíts útvonalat'
      })

      // 4. Exact Clock Time at Bottom (72 px Bold White, Y: 368..444)
      this.clockWidget = hmUI.createWidget(hmUI.widget.TEXT, {
        x: 10,
        y: 368,
        w: 460,
        h: 76,
        color: 0xFFFFFF,
        text_size: 72,
        align_h: hmUI.align.CENTER_H,
        text: '--:--'
      })
    },

    updateClock() {
      let hours = 0
      let minutes = 0
      if (this.timeSensor) {
        try {
          hours = this.timeSensor.getHours()
          minutes = this.timeSensor.getMinutes()
        } catch (_) {
          const d = new Date()
          hours = d.getHours()
          minutes = d.getMinutes()
        }
      } else {
        const d = new Date()
        hours = d.getHours()
        minutes = d.getMinutes()
      }
      const hStr = hours < 10 ? '0' + hours : '' + hours
      const mStr = minutes < 10 ? '0' + minutes : '' + minutes
      const timeStr = `${hStr}:${mStr}`
      if (this.clockWidget) {
        this.clockWidget.setProperty(hmUI.prop.MORE, { text: timeStr })
      }
    },

    requestInitialData() {
      this.request({
        method: 'GET_NAV'
      })
        .then((data) => {
          if (data && data.result) {
            this.updateDisplay(data.result)
          }
        })
        .catch((err) => {
          console.log('GET_NAV error:', err)
        })
    },

    onCall(req) {
      if (req.action === 'NAV_UPDATE' && req.data) {
        this.updateDisplay(req.data)
      }
    },

    updateDisplay(data) {
      if (!data || !data.active) {
        this.iconWidget.setProperty(hmUI.prop.MORE, { src: 'unknown.png' })
        this.distanceWidget.setProperty(hmUI.prop.MORE, { text: 'Készenlét', color: 0x64748B })
        this.streetWidget.setProperty(hmUI.prop.MORE, { text: 'Indíts útvonalat', color: 0x64748B })
        return
      }

      // 1. Giant Turn Icon
      const iconFile = ICON_MAP[data.iconKey] || 'unknown.png'
      this.iconWidget.setProperty(hmUI.prop.MORE, { src: iconFile })

      // 2. Giant Distance Number (72 px)
      const dist = data.distance || (data.distanceMeters > 0 ? `${data.distanceMeters} m` : '0 m')
      this.distanceWidget.setProperty(hmUI.prop.MORE, {
        text: dist,
        color: 0xFFFFFF
      })

      // 3. Big Street / Maneuver Name (40 px)
      let label = data.street
      if (!label || label.trim() === '') {
        label = data.directionText || data.action || ''
      }
      if (data.roundaboutExit > 0) {
        label = `${data.roundaboutExit}. kijárat: ${label}`
      }
      this.streetWidget.setProperty(hmUI.prop.MORE, {
        text: label,
        color: 0xFACC15
      })

      // 4. Update exact clock time
      this.updateClock()

      // 5. Trigger alert (Screen on, vibration, buzzer) on new maneuver or proximity
      this.checkAndTriggerAlert(data)
    },

    checkAndTriggerAlert(data) {
      const now = Date.now()
      const isNewManeuver = data.action && data.action !== this.state.lastManeuver
      const isClose = data.distanceMeters > 0 && data.distanceMeters <= 150
      const cooldownPassed = (now - this.state.lastVibeTimestamp) > 6000 // 6s cooldown

      if (isNewManeuver || (isClose && cooldownPassed)) {
        this.state.lastVibeTimestamp = now
        this.state.lastManeuver = data.action

        // Turn on screen / keep bright for 12 seconds when notification arrives
        try {
          setPageBrightTime({ brightTime: 12000 })
        } catch (e) {
          console.log('setPageBrightTime alert error:', e)
        }

        this.triggerHapticsAndSound(data.vibePattern || 1)
      }
    },

    triggerHapticsAndSound(patternId) {
      // 1. Haptic Vibration
      if (this.vibrator) {
        try {
          const scene = (patternId === 3 || patternId === 10)
            ? (VIBRATOR_SCENE_DURATION !== undefined ? VIBRATOR_SCENE_DURATION : 24)
            : (VIBRATOR_SCENE_NOTIFICATION !== undefined ? VIBRATOR_SCENE_NOTIFICATION : 25)

          this.vibrator.setMode(scene)
          this.vibrator.start()

          if (patternId === 2 || patternId === 9) {
            setTimeout(() => {
              try {
                this.vibrator.setMode(scene)
                this.vibrator.start()
              } catch (_) {}
            }, 300)
          }
        } catch (e) {
          console.log('Vibrator execution error:', e)
        }
      }

      // 2. Speaker Buzzer Beep
      if (this.buzzer) {
        try {
          const bType = (this.buzzer.TYPE && this.buzzer.TYPE.REMIND_1) ? this.buzzer.TYPE.REMIND_1 : 1
          this.buzzer.start(bType, 1)
        } catch (e) {
          console.log('Buzzer execution error:', e)
        }
      }
    },

    onDestroy() {
      console.log('MapyNav Page onDestroy')
      if (this.clockTimer) {
        clearInterval(this.clockTimer)
        this.clockTimer = null
      }
      try {
        resetPageBrightTime()
      } catch (_) {}
      if (this.vibrator) {
        try { this.vibrator.stop() } catch (_) {}
      }
      if (this.buzzer) {
        try { this.buzzer.stop() } catch (_) {}
      }
    }
  })
)
