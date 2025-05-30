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
    href: "/console",
    icon: <IconDashboard size={25} />,
  },
  {
    title: "User",
    label: "",
    href: "",
    icon: <IconUsers size={25} />,
    sub: [
      {
        title: "Users",
        label: "",
        href: "/console/users",
        icon: <IconPoint size={25} />,
      },
      {
        title: "Permissions",
        label: "",
        href: "/console/permissions",
        icon: <IconPoint size={25} />,
      },
      {
        title: "Roles",
        label: "",
        href: "/console/roles",
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
        href: "/console/oauth2-clients",
        icon: <IconPoint size={25} />,
      },
    ],
  }
]
