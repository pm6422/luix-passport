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
    title: "Console",
    label: "",
    href: "/console",
    icon: <IconDashboard size={15} />,
  },
  {
    title: "User",
    label: "",
    href: "",
    icon: <IconUsers size={15} />,
    sub: [
      {
        title: "Users",
        label: "",
        href: "/users",
        icon: <IconPoint size={15} />,
      },
      {
        title: "Permissions",
        label: "",
        href: "/permissions",
        icon: <IconPoint size={15} />,
      },
      {
        title: "Roles",
        label: "",
        href: "/roles",
        icon: <IconPoint size={15} />,
      },
    ],
  },
  {
    title: "Authentication",
    label: "",
    href: "",
    icon: <IconShieldLock size={15} />,
    sub: [
      {
        title: "Oauth2 Clients",
        label: "",
        href: "/oauth2-clients",
        icon: <IconPoint size={15} />,
      },
    ],
  }
]
