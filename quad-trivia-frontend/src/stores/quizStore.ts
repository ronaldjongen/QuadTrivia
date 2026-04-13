import { defineStore } from 'pinia'
import { checkAnswers, getQuestions } from '../api/quizApi'
import { i18n } from '../i18n'

import type {
  CheckAnswersResponse,
  Question,
  SubmittedAnswer,
} from '../data/quiz'

interface QuizState {
  quizId: string | null
  questions: Question[]
  answers: Record<string, string>
  result: CheckAnswersResponse | null
  loading: boolean
  error: string | null
}

function t(key: string, values: Record<string, unknown> = {}) {
  return String(i18n.global.t(key, values))
}

export const useQuizStore = defineStore('quiz', {
  state: (): QuizState => ({
    quizId: null,
    questions: [],
    answers: {},
    result: null,
    loading: false,
    error: null,
  }),

  actions: {
    async fetchQuestions() {
      this.loading = true
      this.error = null
      this.result = null
      this.answers = {}

      try {
        const data = await getQuestions()
        this.quizId = data.quizId
        this.questions = data.questions
      } catch {
        this.error = t('quiz.error')
      } finally {
        this.loading = false
      }
    },

    setAnswer(questionId: string, optionId: string) {
      this.answers[questionId] = optionId
    },

    async submitAnswers() {
      if (!this.quizId) {
        throw new Error(t('quiz.noActiveQuiz'))
      }
      const answeredCount = Object.keys(this.answers).length
      if (answeredCount !== this.questions.length) {
        this.result = null
        this.error = t('quiz.notAllQuestionsAnswered', {
          count: this.questions.length - answeredCount,
        })
        throw new Error(t('quiz.inComplete'))
      }

      this.loading = true
      this.error = null

      try {
        const payload: SubmittedAnswer[] = Object.entries(this.answers).map(
          ([questionId, optionId]) => ({
            questionId,
            optionId,
          }),
        )

        this.result = await checkAnswers({
          quizId: this.quizId,
          answers: payload,
        })
      } catch {
        this.error = t('quiz.submittingAnswerFailed')
        throw new Error('Submit failed')
      } finally {
        this.loading = false
      }
    },
  },
})
