import {
  IconPoint,
  IconDashboard,
  IconUsers,
  IconShieldLock,
} from "@tabler/icons-react"
import React from "react";

export interface NavLink {
  title: string
  label?: string
  href: string
  icon: React.JSX.Element
}

export interface SideLink extends NavLink {
  sub?: NavLink[]
}

export const sidelinks: SideLink[] = [
  {
    title: "Dashboard",
    label: "",
    href: "/",
    icon: <IconDashboard size={25} />,
  },
  {
    title: "User Roles",
    label: "",
    href: "",
    icon: <IconUsers size={25} />,
    sub: [
      {
        title: "Users",
        label: "",
        href: "/users",
        icon: <IconPoint size={25} />,
      },
      {
        title: "Permissions",
        label: "",
        href: "/permissions",
        icon: <IconPoint size={25} />,
      },
    ],
  },
  {
    title: "Authentication",
    label: "",
    href: "",
    icon: <IconShieldLock size={25} />,
    sub: [
      {
        title: "Oauth2 Clients",
        label: "",
        href: "/oauth2-clients",
        icon: <IconPoint size={25} />,
      },
    ],
  }
]
