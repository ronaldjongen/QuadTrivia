<script setup lang="ts">
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'
import QuestionCard from '../components/QuestionCard.vue'
import { useQuizStore } from '../stores/quizStore'
import { RoutePath } from '../router/routePath'

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
  <section class="quiz-page">
    <h1>Trivia Quiz</h1>
    <p v-if="quizStore.loading">Wachten a.u.b. de vragen worden geladen...</p>
    <p v-else-if="quizStore.error">Er is een fout opgetreden bij het laden van de vragen.</p>
    <form v-else-if="quizStore.questions.length" @submit.prevent="onSubmit">
      <Question-card v-for="question in quizStore.questions"
                     :key="question.id" :question="question"
                     :selectedOptionId="quizStore.answers[question.id] ?? null"
                     @select="quizStore.setAnswer(question.id, $event)">
      </Question-card>

      <button type="submit" :disabled="quizStore.loading">
        Opslaan
      </button>
    </form>
  </section>
</template>
<style scoped>

</style>
