import { useEffect, useState } from "react"
import { Avatar } from "@/components/ui/avatar"
import { Button } from "@/components/ui/button"
import { Link } from "react-router-dom"
import { Skeleton } from "@/components/ui/skeleton"
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
  IconUser,
  IconLogout,
  IconBellRinging,
} from "@tabler/icons-react"
import { AccountService } from "@/services/account-service"
import { useLoginUserStore } from "@/stores/login-user-store"
import { MinusIntroY } from "@/components/custom/display/intro-animation"
import { MembershipBadge } from "@/components/custom/display/membership-badge"

export function AccountNav() {
  const { loginUser } = useLoginUserStore()
  const [imageUrl, setImageUrl] = useState<string | undefined>(undefined)
  const [isLoading, setIsLoading] = useState(false)

  useEffect(() => {
    setIsLoading(true)

    AccountService.getAvatar().then((res) => {
      setIsLoading(false)
      const blob = new Blob([res.data], { type: res.headers["content-type"] })
      setImageUrl(URL.createObjectURL(blob))
    })
  }, [])

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative size-12 rounded-full">
          <div>
            {isLoading ? (
              <Avatar className="size-12">
                <Skeleton className="w-full h-full" />
              </Avatar>
            ) : (
              <MinusIntroY>
                <div className="p-[2px] bg-gradient-to-tr from-cyan-400 via-blue-500 to-purple-500 rounded-full">
                  <Avatar className="size-12 border-2 border-background">
                    <img
                      src={imageUrl}
                      alt="Avatar"
                      className="w-full h-full object-cover"
                    />
                  </Avatar>
                </div>
              </MinusIntroY>
            )}
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48" align="end" forceMount>
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col gap-y-2">
            <div className="flex items-center justify-between">
              <p className="text-sm font-medium leading-none truncate">{loginUser.firstName} {loginUser.lastName}</p>
              {loginUser.membershipType && (
                <MembershipBadge type={loginUser.membershipType} />
              )}
            </div>
            <p className="text-xs leading-none text-muted-foreground">
              {loginUser.email}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <Link to="/account">
            <DropdownMenuItem className="cursor-pointer">
              <IconUser className="size-4 me-2" />Account Settings
              {/*<DropdownMenuShortcut>⌘S</DropdownMenuShortcut>*/}
            </DropdownMenuItem>
          </Link>
          <Link to="/inbox">
            <DropdownMenuItem className="cursor-pointer">
              <IconBellRinging className="size-4 me-2" />Inbox
            </DropdownMenuItem>
          </Link>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuItem className="cursor-pointer" onClick={() => AccountService.signOut()}>
          <IconLogout className="size-4 me-2" />Sign out
          {/*<DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>*/}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
