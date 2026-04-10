export default {
  app: {
    title: 'Quad Trivia',
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
    brandPointnCleanFlow:'Login, quiz, result.'
  },
  quiz: {
    title: 'Trivia Quiz',
    loading: 'Please wait for questions to load...',
    submit: 'Submit',

    error: 'Error loading questions.',
  },
  result: {
    title: 'Quiz Results',
    score: 'Score: {score} / {total}',
    noResult: 'No result available.',
    correct: 'Correct',
    playAgain: 'New quiz',
    incorrect: 'Incorrect',
  },
}
