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
import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"
import { MinusIntroY } from "@/components/custom/intro-motion"

export function AccountNav() {
  const { loginUser } = useStore(loginUserStore)
  const [imageUrl, setImageUrl] = useState<string | undefined>(undefined);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    setIsLoading(true);

    AccountService.getProfilePic().then((res) => {
      setIsLoading(false);
      const blob = new Blob([res.data], { type: res.headers["content-type"] });
      setImageUrl(URL.createObjectURL(blob));
    })
  }, []);

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
                <Avatar className="size-12">
                  <img
                    src={imageUrl}
                    alt="Profile"
                    className="w-full h-full object-cover"
                  />
                </Avatar>
              </MinusIntroY>
            )}
          </div>
        </Button>
      </DropdownMenuTrigger>
      <DropdownMenuContent className="w-48" align="end" forceMount>
        <DropdownMenuLabel className="font-normal">
          <div className="flex flex-col space-y-2">
            <p className="text-sm font-medium leading-none">{loginUser.firstName} {loginUser.lastName}</p>
            <p className="text-xs leading-none text-muted-foreground">
              {loginUser.email}
            </p>
          </div>
        </DropdownMenuLabel>
        <DropdownMenuSeparator />
        <DropdownMenuGroup>
          <Link to="/account">
            <DropdownMenuItem className="cursor-pointer">
              <IconUser className="size-4 mr-2"/>Account Settings
              {/*<DropdownMenuShortcut>⌘S</DropdownMenuShortcut>*/}
            </DropdownMenuItem>
          </Link>
          <Link to="/notifications">
            <DropdownMenuItem className="cursor-pointer">
              <IconBellRinging className="size-4 mr-2"/>Notifications
            </DropdownMenuItem>
          </Link>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <DropdownMenuItem className="cursor-pointer" onClick={() => AccountService.signOut()}>
          <IconLogout className="size-4 mr-2"/>Sign out
          {/*<DropdownMenuShortcut>⇧⌘Q</DropdownMenuShortcut>*/}
        </DropdownMenuItem>
      </DropdownMenuContent>
    </DropdownMenu>
  )
}
