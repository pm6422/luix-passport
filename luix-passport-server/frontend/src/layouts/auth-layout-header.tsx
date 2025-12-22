import { AccountNav } from "@/layouts/nav/account-nav";
import { TopNav } from "@/layouts/nav/top-nav";
import { SidebarTrigger } from "@/components/ui/sidebar";
// import { Search } from "@/components/custom/form-element/search"
// import { NotificationNav } from "@/layouts/nav/notification-nav"

export function AuthLayoutHeader() {
  const topNavLinks = [
    {
      title: "Home",
      href: "/",
      isActive: false,
    },
    {
      title: "Console",
      href: "/console",
      isActive: false,
    }
  ]

  return (
    <div className="flex items-center justify-between w-full lg:pe-2 pe-0">
      <div className="bg-background flex h-16 items-center gap-3 sm:gap-4">
        <SidebarTrigger variant="outline" className="scale-125 sm:scale-100" />
        <TopNav links={topNavLinks}/>
        {/*<Search />*/}
      </div>
      <div className="flex items-center gap-4">
        {/*<NotificationNav />*/}
        <AccountNav />
      </div>
    </div>
  )
}
