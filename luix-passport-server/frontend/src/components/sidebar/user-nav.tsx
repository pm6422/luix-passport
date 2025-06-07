import { Link } from "react-router-dom"
import {
  IconUser,
  IconSelector,
  IconLogout,
  IconBellRinging,
} from "@tabler/icons-react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Badge } from "@/components/ui/badge"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import {
  SidebarMenu,
  SidebarMenuButton,
  SidebarMenuItem,
  useSidebar,
} from "@/components/ui/sidebar"
import { Skeleton } from "@/components/ui/skeleton"
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { AccountService } from "@/services/account-service"

export function UserNav() {
  const { isMobile } = useSidebar()
  const { loginUser } = useStore(loginUserStore)

  return (
    <SidebarMenu>
      <SidebarMenuItem>
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <SidebarMenuButton
              size="lg"
              className="data-[state=open]:bg-sidebar-accent data-[state=open]:text-sidebar-accent-foreground"
            >
              <Avatar className="h-8 w-8 rounded-lg">
                <AvatarImage src="/api/accounts/profile-pic" alt={loginUser.username} />
                <AvatarFallback className="rounded-lg">
                  <Skeleton className="w-full h-full" />
                </AvatarFallback>
              </Avatar>
              <div className="grid flex-1 text-left text-sm leading-tight">
                <span className="truncate font-semibold">{loginUser.username}</span>
                <span className="truncate text-xs">{loginUser.email}</span>
              </div>
              <IconSelector className="ml-auto size-4" />
            </SidebarMenuButton>
          </DropdownMenuTrigger>
          <DropdownMenuContent
            className="w-(--radix-dropdown-menu-trigger-width) min-w-56 rounded-lg"
            side={isMobile ? "bottom" : "right"}
            align="end"
            sideOffset={4}
          >
            <DropdownMenuLabel className="p-0 font-normal">
              <div className="flex items-center gap-2 px-1 py-1.5 text-left text-sm">
                <Avatar className="h-8 w-8 rounded-lg">
                  <AvatarImage src="/api/accounts/profile-pic" alt={loginUser.username} />
                  <AvatarFallback className="rounded-lg">
                    <Skeleton className="w-full h-full" />
                  </AvatarFallback>
                </Avatar>
                <div className="grid flex-1 text-left text-sm leading-tight">
                  <span className="truncate font-semibold">{loginUser.username}</span>
                  <span className="truncate text-xs">{loginUser.email}</span>
                </div>
              </div>
            </DropdownMenuLabel>
            <div className="flex flex-wrap items-center gap-x-1 gap-y-0.5 mt-1 px-1">
              {loginUser.roleIds?.map((role: string) => (
                <Badge
                  key={role}
                  className="bg-amber-600/10 dark:bg-amber-600/20 text-amber-500 hover:bg-amber-600/10 px-1.5 py-0.5 text-[9px] rounded-sm leading-none"
                >
                  {role.replace("ROLE_", "")}
                </Badge>
              ))}
            </div>
            <DropdownMenuSeparator />
            <DropdownMenuGroup>
              <DropdownMenuItem asChild className="cursor-pointer">
                <Link to="/account">
                  <IconUser />
                  Account
                </Link>
              </DropdownMenuItem>
              <DropdownMenuItem asChild className="cursor-pointer">
                <Link to="/notifications">
                  <IconBellRinging />
                  Notifications
                </Link>
              </DropdownMenuItem>
            </DropdownMenuGroup>
            <DropdownMenuSeparator />
            <DropdownMenuItem className="cursor-pointer" onClick={() => AccountService.signOut()}>
              <IconLogout />
              Sign out
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      </SidebarMenuItem>
    </SidebarMenu>
  )
}
