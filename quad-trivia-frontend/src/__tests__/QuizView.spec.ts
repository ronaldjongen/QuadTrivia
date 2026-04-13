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
vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: (key: string) => key,
  }),
}))
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
    expect(loadingWrapper.text()).toContain('quiz.loading')

    quizStore.loading = false
    const errorWrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: true,
        },
      },
    })
    expect(errorWrapper.text()).toContain('quiz.title')
  })

  it('renders question cards when questions are present', () => {
    quizStore.questions = [
      {
        id: 'q1',
        text: 'Question 1',
        options: [{ id: 'a1', text: 'A1' }],
      },
    ]
    const wrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: {
            props: ['question', 'selectedOptionId'],
            template: '<div class="question-card-stub">{{ question.text }}</div>'
          },
        },
      },
    })
    expect(wrapper.find('.question-card-stub').exists()).toBe(true)
    expect(wrapper.text()).toContain('Question 1')
  })

  it('calls setAnswer when QuestionCard emits select', async () => {
    quizStore.questions = [
      {
        id: 'q1',
        text: 'Question 1',
        options: [{ id: 'a1', text: 'A1' }],
      },
    ]
    const wrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: {
            props: ['question', 'selectedOptionId'],
            template: '<div class="question-card-stub" @click="$emit(\'select\', \'a1\')"></div>'
          },
        },
      },
    })
    await wrapper.find('.question-card-stub').trigger('click')
    expect(setAnswer).toHaveBeenCalledWith('q1', 'a1')
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
    quizStore.answers = { q1: 'a1' }

    const wrapper = mount(QuizView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
          QuestionCard: {
            props: ['question', 'selectedOptionId'],
            template: '<div class="question-card-stub"></div>'
          },
        },
      },
    })

    const form = wrapper.find('form')
    expect(form.exists()).toBe(true)
    await form.trigger('submit')

    expect(submitAnswers).toHaveBeenCalledTimes(1)
    expect(push).toHaveBeenCalledWith(RoutePath.Result)
  })
})
