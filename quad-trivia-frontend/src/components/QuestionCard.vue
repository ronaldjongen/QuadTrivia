<script setup lang="ts">
import type { Question } from '../data/quiz'
//If not using <script setup>, it is necessary to use defineComponent() to enable props type inference.
// The type of the props object passed to setup() is inferred from the props option.
//https://vuejs.org/guide/typescript/composition-api
defineProps<{
  question: Question
  selectedOptionId: string | null
}>()

const emit = defineEmits<{
  (e: 'select', optionId: string): void
}>()
function decodeHtml(html: string): string {
  const txt = document.createElement('textarea')
  txt.innerHTML = html
  return txt.value
}
</script>

<template>
  <article class="question-card">
    <h2>{{ decodeHtml(question.text) }}</h2>

    <label v-for="option in question.options" :key="option.id" class="option">
      <input
        type="radio"
        :name="question.id"
        :value="option.id"
        :checked="selectedOptionId === option.id"
        @change="emit('select', option.id)"
      />
      <span>{{ option.text }}</span>
    </label>
  </article>
</template>

<style scoped>

</style>
