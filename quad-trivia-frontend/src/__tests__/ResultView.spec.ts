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
    results: Array<{ questionId: string; correct: boolean; correctOptionId: string }>
  },
  loading: false,
  error: null as string | null,
  $reset: reset,
})

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

  it('shows empty-state message when no result is available', () => {
    const wrapper = mount(ResultView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
        },
      },
    })

    expect(wrapper.text()).toContain('Geen resultaat beschikbaar.')
  })

  it('renders score and per-question correctness summary', () => {
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
          correctOptionId: 'a1',
        },
      ],
    }

    const wrapper = mount(ResultView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
        },
      },
    })

    expect(wrapper.text()).toContain('Score:1')
    expect(wrapper.text()).toContain('Your score: 1 / 1')
    expect(wrapper.text()).toContain('What is 2 & 2? - Correct')
  })

  it('resets quiz and routes back to quiz when user clicks play again', async () => {
    quizStore.result = {
      score: 0,
      total: 1,
      results: [],
    }

    const wrapper = mount(ResultView, {
      global: {
        stubs: {
          AppLayout: { template: '<div><slot /></div>' },
        },
      },
    })

    await wrapper.find('button.primary').trigger('click')

    expect(reset).toHaveBeenCalledTimes(1)
    expect(push).toHaveBeenCalledWith(RoutePath.Quizroute)
  })
})
