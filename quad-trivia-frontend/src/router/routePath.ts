export const RoutePath = {
  Loginroute: '/login',
  Quizroute: '/quiz',
  Resultroute: '/result',
} as const

export type RoutePathValue = (typeof RoutePath)[keyof typeof RoutePath]
