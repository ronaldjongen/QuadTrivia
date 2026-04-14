import { beforeEach, describe, expect, it, vi } from 'vitest'
import { mount } from '@vue/test-utils'
import { reactive } from 'vue'
import ResultView from '../views/ResultView.vue'
import { RoutePath } from '../router/routePath'

const push = vi.fn()
const reset = vi.fn()

const quizStore = reactive({
  quizId: 'quiz-1' as string | null,
  questions: [] as Array<{ id: string; text: string; options: Array<{ id: string; text: string }> }>,
  answers: {} as Record<string, string>,
  result: null as null | {
    score: number
    total: number
    results: Array<{ questionId: string; correct: boolean }>
  },
  loading: false,
  error: null as string | null,
  $reset: reset,
})

const mountResultView = () =>
  mount(ResultView, {
    global: {
      stubs: {
        AppLayout: { template: '<div><slot /></div>' },
      },
    },
  })

vi.mock('vue-i18n', () => ({
  useI18n: () => ({
    t: (key: string, params?: any) => {
      if (params) {
        let res = key
        for (const [k, v] of Object.entries(params)) {
          res = res.replace(`{${k}}`, String(v))
        }
        return res
      }
      return key
    },
  }),
}))

vi.mock('vue-router', () => ({
  useRouter: () => ({ push }),
}))

vi.mock('../stores/quizStore', () => ({
  useQuizStore: () => quizStore,
}))

describe('ResultView', () => {
  beforeEach(() => {
    vi.clearAllMocks()
    quizStore.result = null
    quizStore.questions = []
  })

  it('redirects to quiz page when no result is available on mount', () => {
    quizStore.result = null
    mountResultView()
    expect(push).toHaveBeenCalledWith(RoutePath.Quiz)
  })

  it('renders score and per-question correctness summary with decoded HTML', () => {
    quizStore.questions = [
      {
        id: 'q1',
        text: 'What is 2 &amp; 2?',
        options: [],
      },
    ]
    quizStore.result = {
      score: 1,
      total: 1,
      results: [
        {
          questionId: 'q1',
          correct: true,
        },
      ],
    }

    const wrapper = mountResultView()

    expect(wrapper.text()).toContain('1 / 1')
    expect(wrapper.text()).toContain('100% result.correct')
    // HTML entity &amp; should be decoded to &
    expect(wrapper.text()).toContain('What is 2 & 2?')
    expect(wrapper.find('.status-pill').text()).toBe('result.correct')
  })

  it('shows Strong result label and good score class for high scores', () => {
    quizStore.result = {
      score: 8,
      total: 10,
      results: [],
    }
    const wrapper = mountResultView()
    expect(wrapper.text()).toContain('result.strongResult')
    expect(wrapper.find('.score-personal').classes()).toContain('score-good')
  })

  it('shows Decent result label and medium score class for average scores', () => {
    quizStore.result = {
      score: 5,
      total: 10,
      results: [],
    }
    const wrapper = mountResultView()
    expect(wrapper.text()).toContain('result.decentResult')
    expect(wrapper.find('.score-personal').classes()).toContain('score-medium')
  })

  it('shows Needs improvement label and low score class for low scores', () => {
    quizStore.result = {
      score: 2,
      total: 10,
      results: [],
    }
    const wrapper = mountResultView()
    expect(wrapper.text()).toContain('result.needsImprovement')
    expect(wrapper.find('.score-personal').classes()).toContain('score-low')
  })

  it('resets quiz and routes back to quiz when user clicks play again', async () => {
    quizStore.result = {
      score: 0,
      total: 1,
      results: [],
    }

    const wrapper = mountResultView()

    await wrapper.find('button.primary-button').trigger('click')

    expect(reset).toHaveBeenCalledTimes(1)
    expect(push).toHaveBeenCalledWith(RoutePath.Quiz)
  })

  it('displays empty state when summary is not available', () => {
    quizStore.result = null
    const wrapper = mountResultView()
    // It should have pushed to quiz, but let's check what it renders if it stays
    expect(wrapper.find('.empty-state').exists()).toBe(true)
  })
})
