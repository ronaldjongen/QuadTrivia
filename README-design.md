# Quad Trivia Assignment

## Doel

Dit project is een uitwerking van de Quad assignment met een duidelijke scheiding tussen frontend en backend. De kern van de opdracht is:

- een Java backend bouwen;
- trivia-vragen ophalen via een externe bron;
- correcte antwoorden niet direct blootstellen aan de frontend;
- antwoorden server-side controleren;
- een eenvoudige UI voorzien.

De keuzes in dit project zijn bewust pragmatisch gehouden: niet maximaal veel technologie, maar een compacte, verdedigbare oplossing.

---

## Gekozen architectuur

De oplossing bestaat uit drie runtime-onderdelen:

- `quad-trivia-backend` — Spring Boot backend
- `quad-trivia-frontend` — Vue frontend
- `nginx` — reverse proxy voor één origin

Daarnaast is er een root-map met gedeelde infrastructuur:

- `docker-compose.yml`
- `.env`
- `nginx/default.conf`
- `README.md`

### Waarom losse projecten?

Er is bewust gekozen voor **twee losse projecten** in plaats van één gecombineerde monorepo-build:

- duidelijkere scheiding tussen frontend en backend;
- eenvoudiger te reviewen;
- geen onnodige Gradle-complexiteit voor de Vue-app;
- realistischer deployment-verhaal.

Deze keuze is eerder vastgelegd als aanbevolen projectstructuur. fileciteturn0file0

---

## Backend-keuzes

### Spring Boot 4.0.5

De backend draait op **Spring Boot 4.0.5**.

#### Waarom?

- sluit direct aan op de gevraagde Java/Spring-opdracht;
- moderne, production-grade basis;
- goede integratie met Spring Security, validation en HTTP-clients;
- ondersteund in combinatie met moderne Java- en Gradle-versies.

De combinatie **Spring Boot 4.0.5 + Java 25** is expliciet als technisch geldig beoordeeld. fileciteturn0file2

### Java 25

Er is bewust gekozen voor **Java 25**.

#### Waarom?

- modern platform;
- goed verdedigbaar in een assignment-context;
- compatibel met de gekozen Spring Boot-versie.

### Gradle

De backend gebruikt **Gradle**.

#### Waarom?

- was een expliciete voorkeur;
- past goed bij een los backend-project;
- werkt goed met Spring Boot 4.

### Packagestructuur

De backend wordt logisch opgesplitst in:

- `config`
- `security`
- `controller`
- `service`
- `client`
- `dto`
- `model`
- `exception`

Dit is geen Spring-verplichting, maar een bewuste indeling voor leesbaarheid en onderhoudbaarheid. fileciteturn0file2

---

## Externe trivia-integratie

### Gekozen: `WebClient`

Voor de call naar Open Trivia DB is gekozen voor **Spring `WebClient`**.

#### Waarom?

- er is maar één externe API;
- de integratie is eenvoudig;
- geen extra Spring Cloud-laag nodig;
- voldoende flexibel voor foutafhandeling en mapping.

### Niet gekozen: Feign Client

Een **Feign client** is overwogen, maar niet gekozen.

#### Waarom niet?

- voegt extra complexiteit toe voor een heel kleine use case;
- is interessanter bij meerdere downstream services;
- past minder goed bij een compacte assignment-oplossing;
- er is voor deze case geen duidelijke functionele winst t.o.v. `WebClient`.

De keuze voor `WebClient` boven Feign is expliciet als pragmatischer beoordeeld voor deze opdracht. fileciteturn0file2

---

## Persistence

### Niet gekozen: database

Er is **geen database** opgenomen in de basisoplossing.

#### Waarom niet?

- de opdracht vereist geen database;
- de kernlogica kan volledig in memory worden afgehandeld;
- een database zou vooral extra infrastructuur en code toevoegen zonder directe meerwaarde.

### Niet gekozen: MongoDB

MongoDB is bewust **niet** in de standaardoplossing opgenomen.

#### Waarom niet?

- geen noodzaak voor de opdracht;
- verhoogt de scope onnodig;
- maakt het project zwaarder zonder dat het primaire probleem daarmee beter wordt opgelost.

Quiz-state wordt daarom **in memory** bijgehouden, bijvoorbeeld met een `ConcurrentHashMap`. Deze keuze is eerder expliciet gemaakt. fileciteturn0file0 fileciteturn0file2

---

## Authenticatie en security

### Gekozen: Spring Security

Voor security is gekozen voor **Spring Security**.

#### Waarom?

- standaardoplossing binnen Spring Boot;
- geschikt voor browser-gebaseerde sessie-authenticatie;
- ondersteunt password hashing, authorization en sessiebeheer.

### Gekozen: session-based authentication

De login werkt via **server-side sessies**, niet via JWT.

#### Waarom?

- eenvoudiger voor een browser-app;
- goed passend bij Spring Security;
- geen noodzaak om tokens op te slaan in de frontend;
- veiliger en eenvoudiger dan een eigen JWT-flow voor deze opdracht.

### Gekozen: eenvoudige demo-user

Er is gekozen voor een **enkele demo-user** uit configuratie.

#### Waarom?

- voldoende voor de opdracht;
- vermijdt onnodige user management-flow;
- houdt de login simpel en uitlegbaar.

### Gekozen: bcrypt password hashing

Voor het wachtwoord is gekozen voor een **bcrypt hash** via Spring Security `PasswordEncoder`.

#### Waarom?

- veilige standaardkeuze;
- correcte manier om wachtwoorden op te slaan;
- beter verdedigbaar dan plain text of zelfbedachte hashing.

De bcrypt-keuze en demo-user aanpak zijn eerder expliciet uitgewerkt. fileciteturn0file0 fileciteturn0file2

### Niet gekozen: Keycloak

**Keycloak** is bewust niet gekozen.

#### Waarom niet?

- overkill voor een kleine assignment;
- vereist extra infrastructuur;
- voegt weinig toe aan het kernprobleem van deze opdracht;
- leidt de focus weg van API-design en backend-logica.

### Niet gekozen: JWT

**JWT** is niet gekozen.

#### Waarom niet?

- voor deze browser-based use case niet nodig;
- meer moving parts;
- minder pragmatisch dan sessions;
- voor een assignment eerder extra ballast dan meerwaarde.

### Niet gekozen: roles/permissions-model

Geen uitgebreid authorization-model.

#### Waarom niet?

- niet gevraagd;
- geen functionele noodzaak;
- beperkt de scope tot de essentie.

---

## CSRF

De initiële backend-opzet gebruikt **tijdelijk uitgeschakelde CSRF-protectie**.

#### Waarom?

- de app gebruikt een custom JSON login-flow;
- volledige CSRF-tokenafhandeling in een SPA zou extra frontend/backend-coördinatie vragen;
- voor een assignment is een werkende, eenvoudige sessie-oplossing verdedigbaar, zolang deze keuze benoemd wordt.

Dit is nadrukkelijk een **pragmatische assignment-keuze**, geen algemeen productieadvies. Deze afweging is eerder ook zo benoemd. fileciteturn0file2

---

## Frontend-keuzes

### Vue 3

De frontend is gebouwd met **Vue 3**.

#### Waarom?

- moderne frontend-stack;
- duidelijke componentstructuur;
- goed te combineren met Vite, Router en Pinia;
- passend voor een kleine, nette SPA.

### Vite

Als build tool is gekozen voor **Vite**.

#### Waarom?

- moderne standaard voor Vue-projecten;
- snelle dev-ervaring;
- eenvoudige setup;
- betere keuze dan oudere wizard-achtige projecttemplates.

### TypeScript

De frontend gebruikt **TypeScript**.

#### Waarom?

- betere typeveiligheid tussen frontend en backendcontracten;
- duidelijkere API-modellen;
- professionelere indruk in review;
- goede ondersteuning binnen Vue 3.

### Composition API

Er is gekozen voor **Composition API**.

#### Waarom?

- betere TypeScript-ervaring;
- schonere logische groepering van state en gedrag;
- beter passend bij moderne Vue 3-projecten.

De TypeScript-first frontendstructuur met Composition API, Router, Pinia en aparte `api/`, `stores/`, `types/`-mappen is eerder expliciet vastgelegd. fileciteturn0file1

---

## Frontend-architectuur

### Router

Er is gekozen voor **Vue Router**.

#### Waarom?

De applicatie heeft meerdere views:

- `/login`
- `/quiz`
- `/result`

Router maakt deze flow expliciet en onderhoudbaar.

### Pinia

Er is gekozen voor **Pinia**.

#### Waarom?

- duidelijke state management-oplossing;
- geschikt voor auth-state en quiz-state;
- goede TypeScript support;
- minder ruis dan losse state in componenten.

### Niet gekozen: JSX

Geen JSX in de Vue frontend.

#### Waarom niet?

- geen noodzaak voor deze app;
- standaard Vue SFC’s zijn duidelijker;
- voorkomt extra complexiteit.

### Niet gekozen: E2E testing

Geen end-to-end testing in de basisoplossing.

#### Waarom niet?

- tijdsinvestering is relatief groot;
- beperkte meerwaarde voor deze opdracht;
- unit tests en integratietests geven een betere effort/value-verhouding.

### Wel gekozen: Vitest, Linter en Prettier

#### Waarom?

- tonen aandacht voor kwaliteit;
- zorgen voor consistente code;
- passen goed in een professionele frontend workflow.

Deze selectie is eerder ook expliciet als aanbevolen wizard-configuratie vastgelegd. fileciteturn0file1

---

## Centrale API-laag in de frontend

Er is gekozen voor een aparte API-laag:

- `src/api/client.ts`
- `src/api/authApi.ts`
- `src/api/quizApi.ts`

#### Waarom?

- scheidt infrastructuur van UI;
- centraliseert HTTP-configuratie;
- maakt hergebruik en onderhoud eenvoudiger;
- beter testbaar;
- voorkomt duplicatie in componenten.

### `withCredentials: true`

De Axios client gebruikt `withCredentials: true`.

#### Waarom?

- nodig/zinvol bij session-based auth;
- zorgt ervoor dat cookies correct meegestuurd kunnen worden;
- future-proof als origin-setup wijzigt.

### `baseURL: '/api'`

De API client gebruikt `baseURL: '/api'`.

#### Waarom?

- sluit aan op de reverse proxy via Nginx;
- vermijdt hardcoded backend-hosts;
- houdt frontend en backend onder één origin.

Deze frontend HTTP-structuur en motivaties zijn eerder concreet uitgewerkt. fileciteturn0file1

---

## Docker en infrastructuur

### Docker Compose

Er is gekozen voor **Docker Compose**.

#### Waarom?

- start frontend, backend en proxy in één commando;
- maakt lokaal draaien eenvoudiger;
- geeft een nettere demo-opzet;
- houdt de omgeving reproduceerbaar.

### Gekozen: Nginx reverse proxy

Er is gekozen voor een **Nginx reverse proxy**.

#### Waarom?

- frontend en backend draaien onder één origin;
- minder CORS-complexiteit;
- sessiecookies werken netter;
- realistischer infrastructuurverhaal.

### Belangrijke nuance rond Nginx

De SPA fallback (`/index.html`) hoort op de plek waar de frontend statische bestanden echt geserveerd worden, niet in een outer proxy-config die alleen doorproxy’t. Dat onderscheid is tijdens het bouwen expliciet duidelijk geworden door een redirect/rewrite cycle in Nginx.

### Niet gekozen: direct frontend-backend cross-origin development als eindoplossing

#### Waarom niet?

- levert extra CORS-complexiteit op;
- maakt cookies en sessions lastiger;
- minder elegant voor een demo-opstelling.

De Compose- en Nginx-keuzes zijn eerder vastgelegd als voorkeursarchitectuur. fileciteturn0file0

---

## API-ontwerp

De backend exposeert:

- `POST /api/auth/login`
- `POST /api/auth/logout`
- `GET /api/auth/me`
- `GET /api/questions`
- `POST /api/checkanswers`

### Waarom deze endpoints?

- sluiten aan op de opdracht;
- scheiden auth-flow van quiz-flow;
- houden de frontend simpel;
- ondersteunen server-side answer checking.

### Belangrijk ontwerpprincipe

**Het correcte antwoord wordt nooit direct naar de frontend gestuurd.**

In plaats daarvan:

- de backend haalt vragen op;
- randomiseert opties;
- bewaart de juiste mapping server-side;
- vergelijkt bij submit de ingestuurde antwoorden.

Dat is een kernbeslissing van deze oplossing en sluit direct aan op de opdrachtgedachte. fileciteturn0file0 fileciteturn0file2

---

## Bewust buiten scope gehouden

De volgende zaken zijn bewust niet opgenomen in de basisoplossing:

- MongoDB
- Keycloak
- JWT / refresh tokens
- registratieflow
- uitgebreid role/permission model
- microservices
- meerdere downstream service-clients
- geavanceerde production hardening

### Waarom?

Omdat deze assignment vooral beoordeeld zal worden op:

- heldere architectuur;
- nette Java/Spring implementatie;
- API-ontwerp;
- testbaarheid;
- pragmatische keuzes;
- leesbaarheid.

Over-engineering zou de oplossing eerder zwakker dan sterker maken.

---

## Samenvatting van hoofdkeuzes

### Gekozen

- Spring Boot 4.0.5
- Java 25
- Gradle
- Spring Security
- session-based login
- bcrypt password hashing
- in-memory quiz state
- Vue 3
- TypeScript
- Composition API
- Vue Router
- Pinia
- Axios met centrale API-laag
- Docker Compose
- Nginx reverse proxy
- `WebClient` voor Open Trivia DB

### Niet gekozen

- MongoDB
- Keycloak
- JWT
- Feign client
- E2E testing in de basisversie
- JSX
- uitgebreide IAM / user management

---

## Verdedigbare kernboodschap

De rode draad in alle keuzes is:

> **Niet de grootste stack bouwen, maar de juiste stack voor de opdracht.**

Deze oplossing probeert precies dat te doen:

- compact;
- modern;
- veilig genoeg voor de context;
- technisch verdedigbaar;
- zonder onnodige enterprise-complexiteit.
