export default {
  app: {
    title: 'Quad Trivia',
  },
  auth: {
    welkom: 'Welkom bij Quad Trivia!',
    loginTitle: 'Inloggen',
    username: 'Gebruikersnaam',
    password: 'Wachtwoord',
    submit: 'Inloggen',
    failed: 'Inloggen mislukt',
    logout: 'Uitloggen',
    busy: 'Bezig...',
    helper:'Gebruik het demo-account dat in je backend-configuratie is ingesteld.',
    intro:'Meld je aan met je demo-account om verder te gaan.',
    brand:'Log in om de quiz te starten. Een compacte demo met Vue, Spring Boot\n' +
      '          en session-based authentication.',
    brandPointJWT:'Geen JWT in de browseropslag.',
    brandPointServerSide:'Correcte antwoorden blijven in de backend.',
    brandPointnCleanFlow:'Login, quiz, resultaat.'
  },
  quiz: {
    title: 'Trivia Quiz',
    loading: 'Wachten a.u.b. de vragen worden geladen...',
    submit: 'Versturen',
    error: 'Er is een fout opgetreden bij het laden van de vragen.'
  },
  result: {
    title: 'Quiz Resultaten',
    score: 'Score: {score} / {total}',
    noResult: 'Geen resultaat beschikbaar.',
    correct: 'Juist',
    playAgain: 'Nieuwe quiz',
    incorrect: 'Fout',
  },
}
