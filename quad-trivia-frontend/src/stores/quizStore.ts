import { defineStore } from 'pinia'
import { checkAnswers, getQuestions } from '../api/quizApi'
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
        this.error = 'Vragen ophalen mislukt'
      } finally {
        this.loading = false
      }
    },

    setAnswer(questionId: string, optionId: string) {
      this.answers[questionId] = optionId
    },

    async submitAnswers() {
      if (!this.quizId) {
        throw new Error('Geen actieve quiz')
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
        this.error = 'Antwoorden indienen mislukt'
        throw new Error('Submit failed')
      } finally {
        this.loading = false
      }
    },
  },
})
