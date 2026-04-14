import { expect, test, type Page } from '@playwright/test'

const username = process.env.E2E_USERNAME ?? 'demo'
const password = process.env.E2E_PASSWORD ?? 'demo123'

test.describe('Quiz flow', () => {
  async function login(page: Page) {
    await page.goto('/login')
    await page.getByTestId('login-username').fill(username)
    await page.getByTestId('login-password').fill(password)
    await page.getByTestId('login-submit').click()
    await expect(page).toHaveURL(/\/quiz$/)
  }

  test('user can login, answer quiz, and see results', async ({ page }) => {
    await login(page)

    const loadingMessage = page.getByTestId('quiz-loading')
    if (await loadingMessage.isVisible()) {
      await expect(loadingMessage).toBeHidden({ timeout: 20_000 })
    }

    const cards = page.getByTestId('question-card')
    await expect(cards.first()).toBeVisible()

    const cardCount = await cards.count()
    for (let i = 0; i < cardCount; i++) {
      await cards.nth(i).locator('input[type="radio"]').first().check()
    }

    await page.getByTestId('quiz-submit').click()
    await expect(page).toHaveURL(/\/result$/, { timeout: 10_000 })
    await expect(page.locator('.score-personal')).toBeVisible()
    await expect(page.locator('.score-copy h2')).toContainText('/')
  })

  test('authenticated user can logout back to login', async ({ page }) => {
    await login(page)

    await page.getByTestId('logout-button').click()
    await expect(page).toHaveURL(/\/login$/)
  })
})
