import { beforeEach, describe, expect, it, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useQuizStore } from '../stores/quizStore'
import * as quizApi from '../api/quizApi'

vi.mock('../api/quizApi', () => ({
  getQuestions: vi.fn(),
  checkAnswers: vi.fn(),
}))

describe('quizStore', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  it('fetchQuestions loads quiz id and questions from API', async () => {
    vi.mocked(quizApi.getQuestions).mockResolvedValue({
      quizId: 'quiz-1',
      questions: [
        {
          id: 'q1',
          text: 'Question 1',
          options: [
            { id: 'a', text: 'A' },
            { id: 'b', text: 'B' },
          ],
        },
      ],
    })

    const store = useQuizStore()
    store.result = { score: 1, total: 1, results: [] }
    store.answers = { old: 'value' }

    await store.fetchQuestions()

    expect(store.quizId).toBe('quiz-1')
    expect(store.questions).toHaveLength(1)
    expect(store.result).toBeNull()
    expect(store.answers).toEqual({})
    expect(store.error).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('fetchQuestions sets error when API call fails', async () => {
    vi.mocked(quizApi.getQuestions).mockRejectedValue(new Error('Server down'))

    const store = useQuizStore()

    await store.fetchQuestions()

    expect(store.error).toBe('Vragen ophalen mislukt')
    expect(store.loading).toBe(false)
  })

  it('setAnswer stores selected option by question id', () => {
    const store = useQuizStore()

    store.setAnswer('q1', 'a2')

    expect(store.answers).toEqual({ q1: 'a2' })
  })

  it('submitAnswers throws when no active quiz exists', async () => {
    const store = useQuizStore()

    await expect(store.submitAnswers()).rejects.toThrow('Geen actieve quiz')
  })

  it('submitAnswers sends mapped payload and stores result', async () => {
    vi.mocked(quizApi.checkAnswers).mockResolvedValue({
      score: 1,
      total: 2,
      results: [
        { questionId: 'q1', correct: true, correctOptionId: 'a1' },
        { questionId: 'q2', correct: false, correctOptionId: 'b2' },
      ],
    })

    const store = useQuizStore()
    store.quizId = 'quiz-77'
    store.answers = {
      q1: 'a1',
      q2: 'b1',
    }

    await store.submitAnswers()

    expect(quizApi.checkAnswers).toHaveBeenCalledWith({
      quizId: 'quiz-77',
      answers: [
        { questionId: 'q1', optionId: 'a1' },
        { questionId: 'q2', optionId: 'b1' },
      ],
    })
    expect(store.result?.score).toBe(1)
    expect(store.error).toBeNull()
    expect(store.loading).toBe(false)
  })

  it('submitAnswers sets domain error and throws when API fails', async () => {
    vi.mocked(quizApi.checkAnswers).mockRejectedValue(new Error('Bad request'))

    const store = useQuizStore()
    store.quizId = 'quiz-99'
    store.answers = { q1: 'a1' }

    await expect(store.submitAnswers()).rejects.toThrow('Submit failed')

    expect(store.error).toBe('Antwoorden indienen mislukt')
    expect(store.loading).toBe(false)
  })
})
