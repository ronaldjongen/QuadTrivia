import { createI18n } from 'vue-i18n'
import en from './locales/en'
import nl from './locales/nl'

const savedLocale = localStorage.getItem('locale')
const browserLocale = navigator.language.toLowerCase()

const locale =
  savedLocale ??
  (browserLocale.startsWith('nl') ? 'nl' : 'en')

export const i18n = createI18n({
  legacy: false,
  locale,
  fallbackLocale: 'en',
  messages: {
    en,
    nl,
  },
})
