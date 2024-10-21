import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/custom/button"
import { Link } from "react-router-dom"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuGroup,
  DropdownMenuItem,
  DropdownMenuLabel,
  DropdownMenuSeparator,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { IconUser, IconCodeCircle2, IconApi, IconVocabulary, IconLogout } from "@tabler/icons-react"
import { RoleDeveloper } from "@/components/custom/role/role-developer"
import { useAuthUser } from "@/stores/auth-user-provider"
import { AccountService } from "@/services/account-service.ts"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store.ts"

export function AccountNav() {
  const { authUser } = useAuthUser()
  const { appInfo } = useStore(appInfoStore)

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative size-12 rounded-full">
          <Avatar className="size-12 -intro-y">
            <AvatarImage src="api/accounts/profile-pic"/>
            <AvatarFallback>Picture</AvatarFallback>
          </Avatar>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48" align="end" forceMount>
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col space-y-2">
            <p className="text-sm font-medium leading-none">{authUser.firstName} {authUser.lastName}</p>
            <p className="text-xs leading-none text-muted-foreground">
              {authUser.email}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <Link to="/account-settings">
            <DropdownMenuItem className="cursor-pointer">
              <IconUser className="size-4 mr-2"/>Account Settings
              {/*<DropdownMenuShortcut>⌘S</DropdownMenuShortcut>*/}
            </DropdownMenuItem>
          </Link>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <RoleDeveloper>
          <DropdownMenuLabel className="flex items-center gap-2">
            <IconCodeCircle2 className="size-6"/>Developer Tools
          </DropdownMenuLabel>
          <DropdownMenuGroup>
            { appInfo.apiDocsEnabled && <Link to="/api-docs">
              <DropdownMenuItem className="cursor-pointer">
                <IconApi className="size-4 mr-2"/>API Documentation
              </DropdownMenuItem>
            </Link>
            }
            <Link to="/data-dicts">
              <DropdownMenuItem className="cursor-pointer">
                <IconVocabulary className="size-4 mr-2"/>Data Dictionaries
              </DropdownMenuItem>
            </Link>
          </DropdownMenuGroup>
          <DropdownMenuSeparator />
        </RoleDeveloper>
        <DropdownMenuItem className="cursor-pointer" onClick={() => AccountService.signOut()}>
          <IconLogout className="size-4 mr-2"/>Sign out
          {/*<DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>*/}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
