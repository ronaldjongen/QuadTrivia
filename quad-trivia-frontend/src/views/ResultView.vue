<script setup lang="ts">
import {computed} from 'vue'
import {useRouter} from 'vue-router'
import {useI18n} from 'vue-i18n'
import { onMounted } from 'vue'
import {useQuizStore} from '@/stores/quizStore'
import {RoutePath} from '@/router/routePath'
import AppLayout from '@/components/AppLayout.vue'



const {t} = useI18n()
const quizStore = useQuizStore()
const router = useRouter()


const summary = computed(() => quizStore.result)

onMounted(() => {
  if(!quizStore.result) {
    router.push(RoutePath.Quiz)
  }
})
function getQuestionText(questionId: string): string {
  const question = quizStore.questions.find((item) => item.id === questionId)
  return question?.text ?? questionId
}
const scorePercentage = computed(() => {
  if (!summary.value || summary.value.total === 0) return 0
  return Math.round((summary.value.score / summary.value.total) * 100)
})
const scoreLabel = computed(() => {
  const percentage = scorePercentage.value
  if (percentage >= 80) return t('result.strongResult')
  if (percentage >= 50) return t('result.decentResult')
  return t('result.needsImprovement')
})

const scoreClass = computed(() => {
  const percentage = scorePercentage.value

  if (percentage >= 80) return 'score-good'
  if (percentage >= 50) return 'score-medium'
  return 'score-low'
})


function playAgain() {
  quizStore.$reset()
  router.push(RoutePath.Quiz)
}

function decodeHtml(html: string): string {
  const txt = document.createElement('textarea')
  txt.innerHTML = html
  return txt.value
}
</script>

<template>
  <AppLayout>
    <section class="result-page">
      <div class="result-shell">
        <div v-if="summary" class="result-card">
          <header class="result-header">
            <p class="eyebrow">Quad Trivia</p>
            <h1>{{ t('result.title') }}</h1>
            <p class="subtitle">
              {{ t('result.subtitle') }}
            </p>
          </header>
          <section class="score-personal" :class="scoreClass">
            <div class="score-copy">
              <p class="score-kicker">
                {{ t('result.finalScore') }}
              </p>
              <h2>{{ summary.score }} / {{ summary.total }}</h2>
              <p class="score-subtitle">
                {{ scorePercentage }}% {{ t('result.correct') }} · {{ scoreLabel }}
              </p>
            </div>
            <div class="score-circle">
              <span>{{ scorePercentage }}%</span>
            </div>
          </section>
          <section class="results-section">
            <div class="section-heading">
              <h3>{{t('result.perQuestion')}}</h3>
              <p>{{ summary.results.length }} {{t('result.evaluatedItems')}}</p>
            </div>
            <div class="results-list">
              <article
                v-for="item in summary.results"
                :key="item.questionId"
                class="result-item"
                :class="item.correct ? 'result-correct' : 'result-incorrect'"
              >
                <div class="result-main">
                  <p class="question-text">
                    {{ decodeHtml(getQuestionText(item.questionId)) }}
                  </p>
                </div>

                <div
                  class="status-pill"
                  :class="item.correct ? 'pill-correct' : 'pill-incorrect'"
                >
                  {{ item.correct ? t('result.correct'): t('result.incorrect' )}}
                </div>
              </article>
            </div>
          </section>

          <div class="actions">
            <button class="primary-button" type="button" @click="playAgain">
              {{ t('result.playAgain') }}
            </button>
          </div>

        </div>
        <div v-else class="empty-state">
          <h1>{{ t('result.noResult')}}</h1>
          <p>{{ t('result.noResultText')}}</p>
          <button class="primary-button" type="button" @click="playAgain">
            {{ t('result.playAgain') }}
          </button>
        </div>
      </div>
    </section>
  </AppLayout>
</template>

<style scoped>
.result-page {
  min-height: calc(100vh - 72px);
  padding: 40px 24px 56px;
  background: radial-gradient(circle at top left, rgba(37, 99, 235, 0.08), transparent 26%),
  linear-gradient(180deg, #f8fafc 0%, #f3f4f6 100%);
}

.result-shell {
  max-width: 960px;
  margin: 0 auto;
}

.result-card,
.empty-state {
  background: #ffffff;
  border: 1px solid #e5e7eb;
  border-radius: 28px;
  box-shadow: 0 18px 45px rgba(15, 23, 42, 0.08);
}

.result-card {
  padding: 40px;
}

.empty-state {
  padding: 56px 32px;
  text-align: center;
}

.result-header {
  text-align: center;
  margin-bottom: 28px;
}

.eyebrow {
  margin: 0 0 10px;
  font-size: 0.82rem;
  font-weight: 700;
  letter-spacing: 0.1em;
  text-transform: uppercase;
  color: #2563eb;
}

.result-header h1,
.empty-state h1 {
  margin: 0;
  color: #0f172a;
  font-size: clamp(2rem, 4vw, 3.2rem);
  line-height: 1.05;
  letter-spacing: -0.04em;
}

.subtitle,
.empty-state p {
  margin: 14px auto 0;
  max-width: 44ch;
  color: #6b7280;
  font-size: 1rem;
  line-height: 1.65;
}

.score-personal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 24px;
  padding: 28px 30px;
  border-radius: 24px;
  border: 1px solid transparent;
  margin-bottom: 30px;
}

.score-good {
  background: linear-gradient(135deg, #ecfdf5 0%, #d1fae5 100%);
  border-color: #a7f3d0;
}

.score-medium {
  background: linear-gradient(135deg, #fffbeb 0%, #fef3c7 100%);
  border-color: #fde68a;
}

.score-low {
  background: linear-gradient(135deg, #fef2f2 0%, #fee2e2 100%);
  border-color: #fecaca;
}

.score-copy {
  min-width: 0;
}

.score-kicker {
  margin: 0 0 10px;
  font-size: 0.88rem;
  font-weight: 700;
  letter-spacing: 0.08em;
  text-transform: uppercase;
  color: #475569;
}

.score-copy h2 {
  margin: 0;
  font-size: clamp(2.6rem, 6vw, 4.4rem);
  line-height: 0.95;
  letter-spacing: -0.05em;
  color: #0f172a;
}

.score-subtitle {
  margin: 12px 0 0;
  color: #334155;
  font-size: 1rem;
  font-weight: 600;
}

.score-circle {
  width: 132px;
  height: 132px;
  border-radius: 999px;
  display: grid;
  place-items: center;
  background: rgba(255, 255, 255, 0.72);
  border: 1px solid rgba(255, 255, 255, 0.85);
  box-shadow: inset 0 1px 0 rgba(255, 255, 255, 0.65);
  flex-shrink: 0;
}

.score-circle span {
  font-size: 1.8rem;
  font-weight: 800;
  letter-spacing: -0.04em;
  color: #0f172a;
}

.results-section {
  margin-top: 8px;
}

.section-heading {
  display: flex;
  justify-content: space-between;
  align-items: end;
  gap: 16px;
  margin-bottom: 16px;
}

.section-heading h3 {
  margin: 0;
  font-size: 1.15rem;
  color: #0f172a;
}

.section-heading p {
  margin: 0;
  color: #6b7280;
  font-size: 0.92rem;
}

.results-list {
  display: grid;
  gap: 14px;
}

.result-item {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  gap: 18px;
  padding: 18px 20px;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  transition: transform 0.14s ease,
  box-shadow 0.14s ease;
}

.result-item:hover {
  transform: translateY(-1px);
  box-shadow: 0 10px 24px rgba(15, 23, 42, 0.06);
}

.result-correct {
  background: #f0fdf4;
  border-color: #bbf7d0;
}

.result-incorrect {
  background: #fff7f7;
  border-color: #fecaca;
}

.result-main {
  min-width: 0;
  flex: 1;
}

.question-text {
  margin: 0;
  color: #111827;
  font-size: 1rem;
  line-height: 1.55;
  font-weight: 600;
}

.correct-answer {
  margin: 10px 0 0;
  color: #6b7280;
  font-size: 0.94rem;
  line-height: 1.5;
}

.correct-answer strong {
  color: #111827;
}

.status-pill {
  flex-shrink: 0;
  padding: 9px 13px;
  border-radius: 999px;
  font-size: 0.84rem;
  font-weight: 800;
  white-space: nowrap;
}

.pill-correct {
  background: #dcfce7;
  color: #166534;
}

.pill-incorrect {
  background: #fee2e2;
  color: #991b1b;
}

.actions {
  margin-top: 28px;
}

.primary-button {
  width: 100%;
  height: 54px;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #1d4ed8 0%, #2563eb 100%);
  color: #ffffff;
  font: inherit;
  font-weight: 700;
  cursor: pointer;
  transition: transform 0.14s ease,
  box-shadow 0.14s ease,
  opacity 0.14s ease;
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.22);
}

.primary-button:hover {
  transform: translateY(-1px);
}

.primary-button:active {
  transform: translateY(0);
}

@media (max-width: 760px) {
  .result-page {
    padding: 24px 16px 40px;
  }

  .result-card,
  .empty-state {
    padding: 24px;
    border-radius: 22px;
  }

  .score-personal {
    flex-direction: column;
    align-items: flex-start;
    padding: 22px;
  }

  .score-circle {
    width: 96px;
    height: 96px;
  }

  .score-circle span {
    font-size: 1.35rem;
  }

  .section-heading {
    flex-direction: column;
    align-items: flex-start;
  }

  .result-item {
    flex-direction: column;
    align-items: flex-start;
  }

  .status-pill {
    white-space: normal;
  }
}
</style>
