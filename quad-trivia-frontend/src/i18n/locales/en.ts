export default {
  app: {
    title: 'Quad Trivia',
    description: 'Secure trivia demo',
  },
  auth: {
    welcome: 'Welcome to Quad Trivia!',
    loginTitle: 'Login',
    username: 'Username',
    password: 'Password',
    submit: 'Sign in',
    failed: 'Login failed',
    logout: 'Logout',
    busy: 'Busy...',
    helper:'Use the demo account that is set in your backend configuration.',
    intro:'Login with your demo account to continue.',
    brand:'Login to start the quiz. A compact demo with Vue, Spring Boot\n' +
      '          and session-based authentication.'
    ,
    brandPointJWT:'No JWT in the browser storage.',
    brandPointServerSide:'Correct answers stay in the backend.',
    brandPointCleanFlow:'Login, quiz, result.',
    brandJWT:'Secure session auth',
    brandServerSide:'Server-side validation',
    brandCleanFlow:'Clean demo flow',
    startDemo:'Start de demo',

  },
  quiz: {
    title: 'Trivia Quiz',
    loading: 'Please wait for questions to load...',
    submit: 'Submit',
    error: 'Error loading questions.',
    noActiveQuiz:'No active quiz.',
    notAllQuestionsAnswered:'Answer all questions.',
    inComplete:'Incomplete quiz',
    submittingAnswerFailed:'Submitting answer failed'
  },
  result: {
    title: 'Quiz Results',
    score: 'Score: {score} / {total}',
    noResult: 'No result available.',
    noResultText:'Complete a quiz first to see your results here.',
    correct: 'Correct',
    playAgain: 'New quiz',
    incorrect: 'Incorrect',
    strongResult:'Strong result',
    decentResult:'Decent result',
    needsImprovement:'Needs improvement',
    subtitle:'Review your score and the result for each question.',
    finalScore:'Final score',
    perQuestion:'Per question',
    evaluatedItems:'Evaluated items',

  },
}
