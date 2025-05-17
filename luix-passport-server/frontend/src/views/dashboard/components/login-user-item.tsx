import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar.tsx"
import type { LoginUser } from "@/domains/login-user.ts"
import { DateTime } from "@/components/custom/date-time"

export function LoginUserItem(loginUser : LoginUser) {
    return (
        <div className="flex flex-col sm:flex-row gap-2 w-full">
            <div className="flex items-center w-full sm:w-auto">
              <Avatar className="size-9">
                <AvatarImage src={"api/user-profile-pics/" + loginUser.id} alt="profile" />
                <AvatarFallback>AVATAR</AvatarFallback>
              </Avatar>
              <div className="ml-4 space-y-1">
                <p className="text-sm font-medium leading-none">{loginUser.firstName} {loginUser.lastName}</p>
                <p className="text-xs text-muted-foreground">
                  {loginUser.email}
                </p>
              </div>
            </div>
            <div className="ml-auto w-full sm:w-auto"><DateTime value={loginUser.signInAt} className="text-sm text-muted-foreground"/></div>
        </div>
    )
}