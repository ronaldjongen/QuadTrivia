<script setup lang="ts">
import {computed,onMounted,ref} from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import QuestionCard from '../components/QuestionCard.vue'
import { useQuizStore } from '../stores/quizStore'
import { RoutePath } from '../router/routePath'
import AppLayout from '../components/AppLayout.vue'

const { t } = useI18n()
const quizStore = useQuizStore()
const router = useRouter()
const inputError =ref('')
onMounted(async () => {
  if (quizStore.questions.length === 0) {
    await quizStore.fetchQuestions()
  }
})
const canSubmit = computed(() => {
  if (quizStore.questions.length === 0) return false
  return quizStore.questions.every((question) => {    const answer = quizStore.answers[question.id]
    return answer !== undefined && answer !== null && answer !== ''
  })
})

async function onSubmit() {
  if (!canSubmit.value) {
    inputError.value = t('quiz.notAllQuestionsAnswered')
    return
  }
  inputError.value = '';
  try {
    await quizStore.submitAnswers()
    await router.push(RoutePath.Result)
  }
  catch {
    quizStore.error = t('quiz.submittingAnswerFailed')
  }
}
</script>

<template>
  <AppLayout>
    <section class="quiz-container">
      <h1>{{ t('quiz.title') }}</h1>
      <p v-if="quizStore.loading" data-testid="quiz-loading">{{ t('quiz.loading') }}</p>
      <p v-else-if="quizStore.error" data-testid="quiz-error">{{ quizStore.error }}</p>
      <form v-else-if="quizStore.questions.length" @submit.prevent="onSubmit">
        <div class="questions">
          <QuestionCard v-for="question in quizStore.questions"
                         :key="question.id" :question="question"
                         :selectedOptionId="quizStore.answers[question.id] ?? null"
                         @select="quizStore.setAnswer(question.id, $event)">
          </QuestionCard>
        </div>
        <button data-testid="quiz-submit" class="primary" type="submit" :disabled="quizStore.loading || !canSubmit">
          {{ t('quiz.submit') }}
        </button>
        <p v-if="inputError.length" data-testid="quiz-answer-error" class="error-message">
          {{ inputError }}
        </p>
      </form>
    </section>
  </AppLayout>
</template>

<style scoped>
.quiz-container {
  max-width: 720px;
  margin: 48px auto;
}

.questions {
  display: grid;
  gap: 20px;
}

.primary {
  margin-top: 24px;
  height: 52px;
  width: 100%;
  border-radius: 14px;
  background: #2563eb;
  color: white;
}
.primary:disabled {
  background: #93c5fd;
}
.error-message {
  margin: 14px 0 0;
  padding: 12px 14px;
  border-radius: 12px;
  background: #fef2f2;
  color: #b91c1c;
  border: 1px solid #fecaca;
  font-size: 0.93rem;
}
</style>
