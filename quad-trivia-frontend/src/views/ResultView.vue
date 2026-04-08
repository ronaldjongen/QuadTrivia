<script setup lang="ts">
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useQuizStore } from '../stores/quizStore'
import { RoutePath } from '../router/routePath'

const quizStore = useQuizStore()
const router = useRouter()

const summary = computed(() => quizStore.result)

function getQuestionText(questionId: string): string {
  const question = quizStore.questions.find((item) => item.id === questionId)
  return question?.text ?? questionId
}

function playAgain() {
  quizStore.$reset()
  router.push(RoutePath.Quizroute)
}
</script>

<template>
  <section class="result-page">
    <h1>Quiz Results</h1>
    <template v-if="summary">
      <p>Score:{{ summary.score }}</p>
      <p>Your score: {{ summary.score }} / {{ summary.total }}</p>
      <ul>
        <li v-for="result in summary.results" :key="result.questionId">
          {{ getQuestionText(result.questionId) }} - {{ result.correct ? 'Correct' : 'Incorrect' }}
        </li>
      </ul>
      <button @click="playAgain">Probeer opnieuw</button>
    </template>
    <p v-else>Geen resultaat beschikbaar.</p>
  </section>
</template>

<style scoped>

</style>
