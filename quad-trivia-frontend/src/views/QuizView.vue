<script setup lang="ts">
import {onMounted} from 'vue'
import {useRouter} from 'vue-router'
import QuestionCard from '../components/QuestionCard.vue'
import {useQuizStore} from '../stores/quizStore'
import {RoutePath} from '../router/routePath'
import AppLayout from '../components/AppLayout.vue'

const quizStore = useQuizStore()
const router = useRouter()

onMounted(async () => {
  if (quizStore.questions.length === 0) {
    await quizStore.fetchQuestions()
  }
})

async function onSubmit() {
  await quizStore.submitAnswers()
  await router.push(RoutePath.Resultroute)
}
</script>

<template>
  <AppLayout>
    <section class="quiz-container">
      <h1>Trivia Quiz</h1>
      <p v-if="quizStore.loading">Wachten a.u.b. de vragen worden geladen...</p>
      <p v-else-if="quizStore.error">Er is een fout opgetreden bij het laden van de vragen.</p>
      <form v-else-if="quizStore.questions.length" @submit.prevent="onSubmit">
        <div class="questions">
          <Question-card v-for="question in quizStore.questions"
                         :key="question.id" :question="question"
                         :selectedOptionId="quizStore.answers[question.id] ?? null"
                         @select="quizStore.setAnswer(question.id, $event)">
          </Question-card>
        </div>
        <button class="primary" type="submit" :disabled="quizStore.loading">
          Opslaan
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
