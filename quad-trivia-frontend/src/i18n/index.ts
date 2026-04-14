import { createI18n } from 'vue-i18n'
import en from './locales/en'
import nl from './locales/nl'

const canReadStorage =
  typeof globalThis.localStorage !== 'undefined' &&
  typeof globalThis.localStorage.getItem === 'function'
const canReadNavigator =
  typeof globalThis.navigator !== 'undefined' &&
  typeof globalThis.navigator.language === 'string'

const savedLocale = canReadStorage
  ? globalThis.localStorage.getItem('locale')
  : null
const browserLocale = canReadNavigator
  ? globalThis.navigator.language.toLowerCase()
  : 'nl'

const locale =
  savedLocale ??
  (browserLocale.startsWith('nl') ? 'nl' : 'en')

export const i18n = createI18n({
  legacy: false,
  locale,
  fallbackLocale: 'nl',
  messages: {
    nl,
    en,
  },
})
