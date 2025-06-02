import { Avatar, AvatarFallback } from "@/components/ui/avatar"
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
import axios from 'axios';

export function AccountNav() {
  const { appInfo } = useStore(appInfoStore)
  const { loginUser } = useStore(loginUserStore)
  const [imageUrl, setImageUrl] = useState<string | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<Error | null>(null);
  const avatarRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const fetchProfileImage = async () => {
      setIsLoading(true);
      setError(null);

      try {
        // 1. 使用Axios发起请求，设置responseType为'blob'
        const response = await axios.get('/api/accounts/profile-pic', {
          responseType: 'blob',
          withCredentials: true, // 携带cookie
          headers: {
            'Cache-Control': 'no-cache'
          },
          timeout: 5000 // 设置5秒超时
        });

        // 2. 创建Blob URL
        const blob = new Blob([response.data], { type: response.headers['content-type'] });
        const objectUrl = URL.createObjectURL(blob);
        setImageUrl(objectUrl);

      } catch (err) {
        console.error('Failed to load profile image:', err);
        setError(axios.isAxiosError(err) ? err : new Error('Unknown error'));
      } finally {
        setIsLoading(false);
      }
    };

    // 3. 实现懒加载
    const observer = new IntersectionObserver(
      ([entry]) => {
        if (entry.isIntersecting) {
          fetchProfileImage();
          observer.disconnect();
        }
      },
      { threshold: 0.1 }
    );

    if (avatarRef.current) {
      observer.observe(avatarRef.current);
    }

    return () => {
      observer.disconnect();
      // 清理Blob URL防止内存泄漏
      if (imageUrl) URL.revokeObjectURL(imageUrl);
    };
  }, []);

  return (
    <DropdownMenu>
      <DropdownMenuTrigger asChild>
        <Button variant="ghost" className="relative size-12 rounded-full">
          <div ref={avatarRef}>
            {isLoading ? (
              <Avatar className="size-12">
                <Skeleton className="w-full h-full" />
              </Avatar>
            ) : error ? (
              <Avatar className="size-12">
                <AvatarFallback><Skeleton className="w-full" /></AvatarFallback>
              </Avatar>
            ) : imageUrl ? (
              <Avatar className="size-12">
                <img
                  src={imageUrl}
                  alt="Profile"
                  className="w-full h-full object-cover"
                  onLoad={() => {
                    // 可以在这里添加加载完成后的逻辑
                  }}
                  onError={() => {
                    setError(new Error('Image load failed'));
                    setImageUrl(null);
                  }}
                />
              </Avatar>
            ) : (
              <Avatar className="size-12">
                <AvatarFallback><Skeleton className="w-full" /></AvatarFallback>
              </Avatar>
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
