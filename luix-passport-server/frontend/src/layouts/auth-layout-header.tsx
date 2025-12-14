import { AccountNav } from "@/components/site/account-nav";
import { TopNav } from "@/components/site/top-nav";
import { SidebarTrigger } from "@/components/ui/sidebar";
// import { Search } from "@/components/custom/search"
// import { NotificationNav } from "@/components/site/notification-nav"

export function AuthLayoutHeader() {
  const topNavLinks = [
    {
      title: "Home",
      href: "/",
      isActive: false,
    }
  ]

  return (
    <div className="flex items-center justify-between w-full lg:pr-2 pr-0">
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
