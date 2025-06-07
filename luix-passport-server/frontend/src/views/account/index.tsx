import { Outlet } from "react-router-dom"
import {
  IconNotification,
  IconPalette,
  IconTool,
  IconUser,
  IconKey
} from "@tabler/icons-react"
import { Separator } from "@/components/ui/separator"
import { LayoutBody } from "@/layouts/layout-definitions"
import OneLevelSidebar from "@/components/sidebar/one-level-sidebar"

export default function Account() {
  const sidebarNavItems = [
    {
      title: "Profile",
      icon: <IconUser size={18} />,
      href: "/account",
    },
    {
      title: "Settings",
      icon: <IconTool size={18} />,
      href: "/account/settings",
    },
    {
      title: "Password",
      icon: <IconKey size={18} />,
      href: "/account/change-password",
    },
    {
      title: "Appearance",
      icon: <IconPalette size={18} />,
      href: "/account/appearance",
    },
    {
      title: "Notifications",
      icon: <IconNotification size={18} />,
      href: "/account/notifications",
    }
  ]

  return (
    <LayoutBody className="flex flex-col" fixedHeight>
      <div className="space-y-0.5">
        <h1 className="text-2xl font-bold tracking-tight md:text-3xl">
          Personal Account
        </h1>
        <p className="text-muted-foreground">
          Manage your profile and account settings.
        </p>
      </div>
      <Separator className="my-6" />
      <div className="flex flex-1 flex-col space-y-8 overflow-auto lg:flex-row lg:space-x-12 lg:space-y-0">
        <aside className="sticky top-0 lg:w-1/5">
          <OneLevelSidebar items={sidebarNavItems} />
        </aside>
        <div className="w-full p-1 pr-4 lg:max-w-xl">
          <div className="pb-16">
            <Outlet />
          </div>
        </div>
      </div>
    </LayoutBody>
  )
}
