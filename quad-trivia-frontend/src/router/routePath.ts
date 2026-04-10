export const RoutePath = {
  Login: '/login',
  Quiz: '/quiz',
  Result: '/result',
} as const

export type RoutePathValue = (typeof RoutePath)[keyof typeof RoutePath]
