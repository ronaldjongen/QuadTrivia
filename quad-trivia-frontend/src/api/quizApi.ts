import { api } from './client'
import type {
  QuestionsResponse,
  CheckAnswersRequest,
  CheckAnswersResponse,
} from '../data/quiz'

export async function getQuestions(): Promise<QuestionsResponse> {
  const response = await api.get<QuestionsResponse>('/questions')
  return response.data
}

export async function checkAnswers(
  payload: CheckAnswersRequest,
): Promise<CheckAnswersResponse> {
  const response = await api.post<CheckAnswersResponse>('/checkanswers', payload)
  return response.data
}
