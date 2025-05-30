import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import type { LoginUser } from "@/domains/login-user"
import { DateTime } from "@/components/custom/date-time"
import { Skeleton } from "@/components/ui/skeleton"

export function LoginUserItem(loginUser : LoginUser) {
    return (
        <div className="flex items-center">
            <Avatar className="size-9">
              <AvatarImage src={"/api/user-profile-pics/" + loginUser.username} alt="profile" />
              <AvatarFallback><Skeleton className="w-full" /></AvatarFallback>
            </Avatar>
            <div className="ml-2 space-y-1">
              <p className="text-sm font-medium leading-none">{loginUser.firstName} {loginUser.lastName}</p>
              <p className="text-xs text-muted-foreground truncate max-w-[130px] sm:max-w-[200px]">
                {loginUser.email}
              </p>
            </div>
            <div className="ml-auto">
              <DateTime value={loginUser.signInAt} className="text-xs sm:text-sm text-muted-foreground"/>
            </div>
        </div>
    )
}