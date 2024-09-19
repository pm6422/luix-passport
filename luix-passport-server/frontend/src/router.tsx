import { createHashRouter } from "react-router-dom"
import GeneralError from "./views/errors/general-error"
import ForbiddenError from "./views/errors/forbidden-error"
import NotFoundError from "./views/errors/not-found-error"
import MaintenanceError from "./views/errors/maintenance-error"

// const router = createBrowserRouter([
const router = createHashRouter([
  // Auth routes
  {
    path: "/sign-in",
    lazy: async () => ({
      Component: (await import("./views/auth/sign-in")).default
    }),
  },
  {
    path: "/sign-up",
    lazy: async () => ({
      Component: (await import("./views/auth/sign-up")).default
    }),
  },
  {
    path: "/forgot-password",
    lazy: async () => ({
      Component: (await import("./views/auth/forgot-password")).default
    }),
  },

  // Main routes
  {
    path: "/",
    lazy: async () => ({
      Component: (await import("./layouts/auth-layout")).default
    }),
    errorElement: <GeneralError />,
    children: [
      {
        index: true,
        lazy: async () => ({
          Component: (await import("./views/dashboard")).default
        }),
      },
      {
        path: "account-settings",
        lazy: async () => ({
          Component: (await import("./views/account-settings")).default
        }),
        errorElement: <GeneralError />,
        children: [
          {
            index: true,
            lazy: async () => ({
              Component: (await import("./views/account-settings/profile")).default
            }),
          },
          {
            path: "account",
            lazy: async () => ({
              Component: (await import("./views/account-settings/account")).default
            }),
          },
          {
            path: "change-password",
            lazy: async () => ({
              Component: (await import("./views/account-settings/change-password")).default
            }),
          },
          {
            path: "change-email",
            lazy: async () => ({
              Component: (await import("./views/account-settings/change-email")).default
            }),
          },
          {
            path: "appearance",
            lazy: async () => ({
              Component: (await import("./views/account-settings/appearance")).default
            }),
          },
          {
            path: "notifications",
            lazy: async () => ({
              Component: (await import("./views/account-settings/notifications")).default
            }),
          },
          {
            path: "display",
            lazy: async () => ({
              Component: (await import("./views/account-settings/display")).default
            }),
          },
          {
            path: "form",
            lazy: async () => ({
              Component: (await import("./views/account-settings/form")).default
            }),
          }
        ],
      },
      {
        path: "api-docs",
        lazy: async () => ({
          Component: (await import("@/views/developer/api-docs")).default
        }),
      },
      {
        path: "data-dicts",
        lazy: async () => ({
          Component: (await import("@/views/developer/data-dicts")).default
        }),
      },
      {
        path: "users",
        lazy: async () => ({
          Component: (await import("@/views/users")).default
        }),
      },
      {
        path: "roles",
        lazy: async () => ({
          Component: (await import("@/components/custom/coming-soon")).default
        }),
      },
      {
        path: "permissions",
        lazy: async () => ({
          Component: (await import("@/components/custom/coming-soon")).default
        }),
      },
      {
        path: "oauth2-clients",
        lazy: async () => ({
          Component: (await import("@/views/auth2-clients")).default
        }),
      }
    ],
  },

  // Error routes
  { path: "/500", Component: GeneralError },
  { path: "/403", Component: ForbiddenError },
  { path: "/404", Component: NotFoundError },
  { path: "/503", Component: MaintenanceError },

  // Fallback 404 route
  { path: "*", Component: NotFoundError },
])

export default router
