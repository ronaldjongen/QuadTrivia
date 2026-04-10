<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import { useQuizStore } from '../stores/quizStore'
import { RoutePath } from '../router/routePath'
import AppLayout from '../components/AppLayout.vue'

const { t } = useI18n()
const quizStore = useQuizStore()
const router = useRouter()

const summary = computed(() => quizStore.result)

function getQuestionText(questionId: string): string {
  const question = quizStore.questions.find((item) => item.id === questionId)
  return question?.text ?? questionId
}

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
    <section class="result-container">
      <div class="result-card">
        <h1>{{ t('result.title') }}</h1>
        <template v-if="summary">
          <p data-testid="result-score">Score:{{ summary.score }}</p>
          <p data-testid="result-total">Your score: {{ summary.score }} / {{ summary.total }}</p>
          <ul data-testid="result-list">
            <li v-for="result in summary.results" :key="result.questionId" data-testid="result-item">
              {{ decodeHtml(getQuestionText(result.questionId)) }} - {{
                result.correct ? t('result.correct') : t('result.incorrect')
              }}
            </li>
          </ul>
          <button data-testid="result-play-again" class="primary" @click="playAgain">{{ t('result.playAgain') }}</button>
        </template>
        <p v-else data-testid="result-empty">{{ t('result.noResult') }}</p>
      </div>
    </section>
  </AppLayout>
</template>

<style scoped>
.score {
  font-size: 3rem;
  font-weight: 700;
  margin: 16px 0;
}

.result-item {
  padding: 16px;
  border-radius: 16px;
  border: 1px solid #e5e7eb;
  margin-bottom: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.result-item.correct {
  background: #ecfdf5;
  border-color: #a7f3d0;
}

.result-item.incorrect {
  background: #fef2f2;
  border-color: #fecaca;
}

.status.correct {
  color: #065f46;
  font-weight: 600;
}

.status.incorrect {
  color: #991b1b;
  font-weight: 600;
}
.result-container {
  display: grid;
  place-items: center;
  margin-top: 80px;
}

.result-card {
  padding: 40px;
  border-radius: 24px;
  background: white;
  border: 1px solid #e5e7eb;
  text-align: center;
}

.primary {
  width: 100%;
  height: 52px;
  border-radius: 14px;
  background: #2563eb;
  color: white;
  font-weight: 600;
  border: none;
  cursor: pointer;
}
</style>
