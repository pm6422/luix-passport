import type { ComponentType } from "react"
import { createBrowserRouter, type RouteObject } from "react-router-dom"
import ForbiddenError from "@/views/errors/forbidden-error"
import GeneralError from "@/views/errors/general-error"
import MaintenanceError from "@/views/errors/maintenance-error"
import NotFoundError from "@/views/errors/not-found-error"

type DefaultExport<T> = { default: T }

const lazyComponent = (importer: () => Promise<DefaultExport<ComponentType>>) => {
  return async () => ({
    Component: (await importer()).default,
  })
}

const accountRoutes: RouteObject[] = [
  { path: "/sign-in", lazy: lazyComponent(() => import("@/views/account/sign-in")) },
  { path: "/sign-up", lazy: lazyComponent(() => import("@/views/account/sign-up")) },
  { path: "/activate-account", lazy: lazyComponent(() => import("@/views/account/activate-account")) },
  { path: "/forgot-password", lazy: lazyComponent(() => import("@/views/account/forgot-password")) },
  { path: "/reset-password", lazy: lazyComponent(() => import("@/views/account/reset-password")) },
]

const publicRoutes: RouteObject = {
  path: "/",
  lazy: lazyComponent(() => import("@/layouts/no-auth-layout")),
  errorElement: <GeneralError />,
  children: [
    { index: true, lazy: lazyComponent(() => import("@/views/site/home")) },
    { path: "features", lazy: lazyComponent(() => import("@/views/site/features")) },
    { path: "docs", lazy: lazyComponent(() => import("@/views/site/docs")) },
    { path: "docs/installation", lazy: lazyComponent(() => import("@/views/site/docs/installation")) },
    { path: "docs/authentication", lazy: lazyComponent(() => import("@/views/site/docs/authentication")) },
    { path: "docs/api-reference", lazy: lazyComponent(() => import("@/views/site/docs/api-reference")) },
    { path: "terms-of-service", lazy: lazyComponent(() => import("@/views/site/terms-of-service")) },
    { path: "privacy-policy", lazy: lazyComponent(() => import("@/views/site/privacy-policy")) },
    { path: "contact-us", lazy: lazyComponent(() => import("@/views/site/contact-us")) },
  ],
}

const authRoutes: RouteObject = {
  path: "/",
  lazy: lazyComponent(() => import("@/layouts/auth-layout")),
  errorElement: <GeneralError />,
  children: [
    { path: "console", lazy: lazyComponent(() => import("@/views/console")) },
    { path: "inbox", lazy: lazyComponent(() => import("@/views/inbox")) },
    { path: "api-docs", lazy: lazyComponent(() => import("@/views/api-docs")) },
    { path: "db-schema", lazy: lazyComponent(() => import("@/views/db-schema")) },
    { path: "health-check", lazy: lazyComponent(() => import("@/views/health-check")) },
    { path: "data-dicts", lazy: lazyComponent(() => import("@/views/data-dicts")) },
    { path: "schedule-logs", lazy: lazyComponent(() => import("@/views/schedule-logs")) },
    { path: "users", lazy: lazyComponent(() => import("@/views/users")) },
    { path: "permissions", lazy: lazyComponent(() => import("@/views/permissions")) },
    { path: "roles", lazy: lazyComponent(() => import("@/views/roles")) },
    { path: "promo-codes", lazy: lazyComponent(() => import("@/views/promo-codes")) },
    { path: "oauth-clients", lazy: lazyComponent(() => import("@/views/auth2-clients")) },
    { path: "teams/:teamId", lazy: lazyComponent(() => import("@/views/team")) },
    { path: "tech-docs", lazy: lazyComponent(() => import("@/views/site/docs")) },
    { path: "user-overview", lazy: lazyComponent(() => import("@/components/custom/display/coming-soon")) },
    {
      path: "account",
      lazy: lazyComponent(() => import("@/views/account")),
      errorElement: <GeneralError />,
      children: [
        { index: true, lazy: lazyComponent(() => import("@/views/account/profile")) },
        { path: "settings", lazy: lazyComponent(() => import("@/views/account/settings")) },
        { path: "change-password", lazy: lazyComponent(() => import("@/views/account/change-password")) },
        { path: "change-email", lazy: lazyComponent(() => import("@/views/account/change-email")) },
        { path: "membership", lazy: lazyComponent(() => import("@/views/account/membership")) },
        { path: "appearance", lazy: lazyComponent(() => import("@/views/account/appearance")) },
      ],
    },
  ],
}

const errorRoutes: RouteObject[] = [
  { path: "/403", Component: ForbiddenError },
  { path: "/404", Component: NotFoundError },
  { path: "/500", Component: GeneralError },
  { path: "/503", Component: MaintenanceError },
  { path: "*", Component: NotFoundError },
]

const router = createBrowserRouter([
  ...accountRoutes,
  publicRoutes,
  authRoutes,
  ...errorRoutes,
])

export default router
