import { useEffect, useState } from "react"
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar"
import { LoginUserService } from "@/services/login-user-service"
import type { LoginUser } from "@/domains/login-user.ts"

export function RecentLoginUsers() {
  const [ loginUsers, setLoginUsers ] = useState([] as Array<LoginUser>)

  useEffect(() => {
    LoginUserService.findRecentLoginUsers().then((resp) => {
      setLoginUsers(resp.data)
    })
  }, [])

  return (
    <div className='space-y-8'>
      <div className='flex items-center'>
        <Avatar className='h-9 w-9'>
          <AvatarImage src='/assets/images/cartoon/01.png' alt='Avatar' />
          <AvatarFallback>OM</AvatarFallback>
        </Avatar>
        <div className='ml-4 space-y-1'>
          <p className='text-sm font-medium leading-none'>Olivia Martin</p>
          <p className='text-sm text-muted-foreground'>
            olivia.martin@email.com
          </p>
        </div>
        <div className='ml-auto font-medium'>+$1,999.00</div>
      </div>
    </div>
  )
}
