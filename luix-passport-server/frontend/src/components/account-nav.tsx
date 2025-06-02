import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { Button } from "@/components/custom/button"
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
  IconCodeCircle2,
  IconApi,
  IconVocabulary,
  IconLogout,
  IconBellRinging,
  IconDatabase,
} from '@tabler/icons-react'
import { RoleDeveloper } from "@/components/custom/role/role-developer"
import { AccountService } from "@/services/account-service"
import { useStore } from "exome/react"
import { appInfoStore } from "@/stores/app-info-store"
import { loginUserStore } from "@/stores/login-user-store"
import { useEffect, useRef, useState } from 'react'

export function AccountNav() {
  const { appInfo } = useStore(appInfoStore)
  const { loginUser } = useStore(loginUserStore)
  const avatarRef = useRef<HTMLButtonElement>(null);
  const [isInView, setIsInView] = useState(false);

  useEffect(() => {
    if (!avatarRef.current) return;

    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          setIsInView(true);
          observer.unobserve(entry.target);
        }
      },
      { threshold: 0.1 }
    );

    observer.observe(avatarRef.current);

    return () => {
      if (avatarRef.current) {
        observer.unobserve(avatarRef.current);
      }
    };
  }, []);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative size-12 rounded-full" ref={avatarRef}>
          {isInView ? (
            <Avatar className="size-12">
              <AvatarImage src="/api/accounts/profile-pic"/>
              <AvatarFallback><Skeleton className="w-full" /></AvatarFallback>
            </Avatar>
          ) : (
            <Skeleton className="w-full h-full rounded-full" />
          )}
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
          <Link to="/console/account">
            <DropdownMenuItem className="cursor-pointer">
              <IconUser className="size-4 mr-2"/>Account Settings
              {/*<DropdownMenuShortcut>⌘S</DropdownMenuShortcut>*/}
            </DropdownMenuItem>
          </Link>
          <Link to="/console/notifications">
            <DropdownMenuItem className="cursor-pointer">
              <IconBellRinging className="size-4 mr-2"/>Notifications
            </DropdownMenuItem>
          </Link>
        </DropdownMenuGroup>
        <DropdownMenuSeparator />
        <RoleDeveloper>
          <DropdownMenuLabel className="flex items-center gap-2">
            <IconCodeCircle2 className="size-6"/>Developer Tools
          </DropdownMenuLabel>
          <DropdownMenuGroup>
            { appInfo.apiDocsEnabled && <Link to="/console/api-docs">
              <DropdownMenuItem className="cursor-pointer">
                <IconApi className="size-4 mr-2"/>API Documentation
              </DropdownMenuItem>
            </Link>
            }
            <Link to="/console/db-schema">
              <DropdownMenuItem className="cursor-pointer">
                <IconDatabase className="size-4 mr-2"/>DB Schema
              </DropdownMenuItem>
            </Link>
            <Link to="/console/data-dicts">
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
