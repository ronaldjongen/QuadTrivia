import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { reactive } from 'vue'
import QuizView from '../views/QuizView.vue'
import { RoutePath } from '../router/routePath'

const push = vi.fn()
const fetchQuestions = vi.fn()
const submitAnswers = vi.fn()
const setAnswer = vi.fn()

const quizStore = reactive({
  quizId: null as string | null,
  questions: [] as Array<{
    id: string
    text: string
    options: Array<{ id: string; text: string }>
  }>,
  answers: {} as Record<string, string>,
  result: null,
  loading: false,
  error: null as string | null,
  fetchQuestions,
  submitAnswers,
  setAnswer,
})

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
}))

vi.mock('../stores/quizStore', () => ({
  useQuizStore: () => quizStore,
}))

describe('QuizView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    quizStore.questions = []
    quizStore.answers = {}
    quizStore.loading = false
    quizStore.error = null
  })

  it('fetches questions on mount when store has no questions', () => {
    mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })

    expect(fetchQuestions).toHaveBeenCalledTimes(1)
  })

  it('does not fetch questions on mount when questions already exist', () => {
    quizStore.questions = [
      {
        id: 'q1',
        text: 'Question 1',
        options: [
          { id: 'a1', text: 'A1' },
          { id: 'a2', text: 'A2' },
        ],
      },
    ]

    mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })

    expect(fetchQuestions).not.toHaveBeenCalled()
  })

  it('shows loading and error feedback states', () => {
    quizStore.loading = true
    const loadingWrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })
    expect(loadingWrapper.text()).toContain('Wachten a.u.b. de vragen worden geladen...')

    quizStore.loading = false
    quizStore.error = 'boom'
    const errorWrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })
    expect(errorWrapper.text()).toContain('Er is een fout opgetreden bij het laden van de vragen.')
  })

  it('submits answers and navigates to result route', async () => {
    quizStore.questions = [
      {
        id: 'q1',
        text: 'Question 1',
        options: [
          { id: 'a1', text: 'A1' },
          { id: 'a2', text: 'A2' },
        ],
      },
    ]
    submitAnswers.mockResolvedValue(undefined)

    const wrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })

    await wrapper.find('form').trigger('submit.prevent')

    expect(submitAnswers).toHaveBeenCalledTimes(1)
    expect(push).toHaveBeenCalledWith(RoutePath.Resultroute)
  })
})
