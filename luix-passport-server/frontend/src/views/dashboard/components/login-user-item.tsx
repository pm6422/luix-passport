import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar.tsx"
import type { LoginUser } from "@/domains/login-user.ts"
import { DateTime } from "@/components/custom/date-time"

export function LoginUserItem(loginUser : LoginUser) {
    return (
        <div className='flex items-center'>
            <Avatar className='h-9 w-9'>
                <AvatarImage src={"api/user-profile-pics/" + loginUser.id} alt="profile" />
                <AvatarFallback>AVATAR</AvatarFallback>
            </Avatar>
            <div className='ml-4 space-y-1'>
                <p className='text-sm font-medium leading-none'>{loginUser.firstName} {loginUser.lastName}</p>
                <p className='text-xs text-muted-foreground'>
                    {loginUser.email}
                </p>
            </div>
            <div className='ml-auto text-sm'><DateTime value={loginUser.signInAt} className="text-xs"/></div>
        </div>
    )
}