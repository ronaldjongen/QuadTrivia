<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useI18n } from 'vue-i18n'
import QuestionCard from '../components/QuestionCard.vue'
import { useQuizStore } from '../stores/quizStore'
import { RoutePath } from '../router/routePath'
import AppLayout from '../components/AppLayout.vue'

const { t } = useI18n()
const quizStore = useQuizStore()
const router = useRouter()

onMounted(async () => {
  if (quizStore.questions.length === 0) {
    await quizStore.fetchQuestions()
  }
})

async function onSubmit() {
  await quizStore.submitAnswers()
  await router.push(RoutePath.Result)
}
</script>

<template>
  <AppLayout>
    <section class="quiz-container">
      <h1>{{ t('quiz.title') }}</h1>
      <p v-if="quizStore.loading" data-testid="quiz-loading">{{ t('quiz.loading') }}</p>
      <p v-else-if="quizStore.error" data-testid="quiz-error">{{ t('result.error') }}</p>
      <form v-else-if="quizStore.questions.length" @submit.prevent="onSubmit">
        <div class="questions">
          <Question-card v-for="question in quizStore.questions"
                         :key="question.id" :question="question"
                         :selectedOptionId="quizStore.answers[question.id] ?? null"
                         @select="quizStore.setAnswer(question.id, $event)">
          </Question-card>
        </div>
        <button data-testid="quiz-submit" class="primary" type="submit" :disabled="quizStore.loading">
          {{ t('quiz.submit') }}
        </button>
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
</style>
