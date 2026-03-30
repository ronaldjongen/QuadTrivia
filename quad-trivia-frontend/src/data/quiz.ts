export interface QuestionOption {
  id: string
  text: string
}

export interface Question {
  id: string
  text: string
  options: QuestionOption[]
}

export interface QuestionsResponse {
  quizId: string
  questions: Question[]
}

export interface SubmittedAnswer {
  questionId: string
  optionId: string
}

export interface CheckAnswersRequest {
  quizId: string
  answers: SubmittedAnswer[]
}

export interface QuestionResult {
  questionId: string
  correct: boolean
  correctOptionId: string
}

export interface CheckAnswersResponse {
  score: number
  total: number
  results: QuestionResult[]
}
