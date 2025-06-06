import { createBrowserRouter } from "react-router-dom"
import GeneralError from "./views/errors/general-error"
import ForbiddenError from "./views/errors/forbidden-error"
import NotFoundError from "./views/errors/not-found-error"
import MaintenanceError from "./views/errors/maintenance-error"

const router = createBrowserRouter([
  // account-related routes
  {
    path: "/sign-in",
    lazy: async () => ({
      Component: (await import("./views/account/sign-in")).default
    }),
  },
  {
    path: "/sign-up",
    lazy: async () => ({
      Component: (await import("./views/account/sign-up")).default
    }),
  },
  {
    path: "/activate-account",
    lazy: async () => ({
      Component: (await import("./views/account/activate-account")).default
    }),
  },
  {
    path: "/forgot-password",
    lazy: async () => ({
      Component: (await import("./views/account/forgot-password")).default
    }),
  },
  {
    path: "/reset-password",
    lazy: async () => ({
      Component: (await import("./views/account/reset-password")).default
    }),
  },
  {
    path: "/rpc",
    lazy: async () => ({
      Component: (await import("./views/rpc")).default
    }),
  },

  // no-auth routes
  {
    path: "/",
    lazy: async () => ({
      Component: (await import("./layouts/no-auth-layout")).default
    }),
    errorElement: <GeneralError />,
    children: [
      {
        index: true,
        lazy: async () => ({
          Component: (await import("./views/site/home")).default
        }),
      },
      {
        path: "/features",
        lazy: async () => ({
          Component: (await import("./views/site/features")).default
        }),
      },
      {
        path: "/pricing",
        lazy: async () => ({
          Component: (await import("./views/site/pricing")).default
        }),
      },
      {
        path: "/docs",
        lazy: async () => ({
          Component: (await import("./views/site/docs")).default
        }),
      },
      {
        path: "/docs/installation",
        lazy: async () => ({
          Component: (await import("./views/site/docs/installation")).default
        }),
      },
      {
        path: "/docs/authentication",
        lazy: async () => ({
          Component: (await import("./views/site/docs/authentication")).default
        }),
      },
      {
        path: "/docs/api-reference",
        lazy: async () => ({
          Component: (await import("./views/site/docs/api-reference")).default
        }),
      },
      {
        path: "/terms-of-service",
        lazy: async () => ({
          Component: (await import("./views/site/terms-of-service")).default
        }),
      },
      {
        path: "/privacy-policy",
        lazy: async () => ({
          Component: (await import("./views/site/privacy-policy")).default
        }),
      },
      {
        path: "/contact-us",
        lazy: async () => ({
          Component: (await import("./views/site/contact-us")).default
        }),
      }
    ],
  },

  // auth routes
  {
    path: "/",
    lazy: async () => ({
      Component: (await import("./layouts/auth-layout")).default
    }),
    errorElement: <GeneralError />,
    children: [
      {
        path: "console",
        lazy: async () => ({
          Component: (await import("./views/console")).default
        }),
      },
      {
        path: "notifications",
        lazy: async () => ({
          Component: (await import("@/views/notifications")).default
        }),
      },
      {
        path: "api-docs",
        lazy: async () => ({
          Component: (await import("@/views/api-docs")).default
        }),
      },
      {
        path: "db-schema",
        lazy: async () => ({
          Component: (await import("@/views/db-schema")).default
        }),
      },
      {
        path: "data-dicts",
        lazy: async () => ({
          Component: (await import("@/views/data-dicts")).default
        }),
      },
      {
        path: "users",
        lazy: async () => ({
          Component: (await import("@/views/users")).default
        }),
      },
      {
        path: "permissions",
        lazy: async () => ({
          Component: (await import("@/views/permissions")).default
        }),
      },
      {
        path: "roles",
        lazy: async () => ({
          Component: (await import("@/views/roles")).default
        }),
      },
      {
        path: "oauth2-clients",
        lazy: async () => ({
          Component: (await import("@/views/auth2-clients")).default
        }),
      },
      {
        path: "user-overview",
        lazy: async () => ({
          Component: (await import("@/components/custom/coming-soon")).default
        }),
      },
      {
        path: "account",
        lazy: async () => ({
          Component: (await import("./views/account")).default
        }),
        errorElement: <GeneralError />,
        children: [
          {
            index: true,
            lazy: async () => ({
              Component: (await import("./views/account/profile")).default
            }),
          },
          {
            path: "settings",
            lazy: async () => ({
              Component: (await import("./views/account/settings")).default
            }),
          },
          {
            path: "change-password",
            lazy: async () => ({
              Component: (await import("./views/account/change-password")).default
            }),
          },
          {
            path: "change-email",
            lazy: async () => ({
              Component: (await import("./views/account/change-email")).default
            }),
          },
          {
            path: "appearance",
            lazy: async () => ({
              Component: (await import("./views/account/appearance")).default
            }),
          },
          {
            path: "notifications",
            lazy: async () => ({
              Component: (await import("./views/account/notifications")).default
            }),
          }
        ],
      }
    ],
  },

  // Error routes
  { path: "/403", Component: ForbiddenError },
  { path: "/404", Component: NotFoundError },
  { path: "/500", Component: GeneralError },
  { path: "/503", Component: MaintenanceError },

  // Fallback 404 route
  { path: "*", Component: NotFoundError },
])

export default router
