import { useEffect, useState } from "react"
import { UserAuthEventService } from "@/services/user-auth-event-service"
import { LoginUser } from "@/domains/login-user"
import { LoginUserItem } from "./login-user-item"

export function RecentLoginUsers() {
  const [ loginUsers, setLoginUsers ] = useState([] as Array<LoginUser>)

  useEffect(() => {
    UserAuthEventService.findRecentLoginUsers().then((resp) => {
      setLoginUsers(resp.data)
    })
  }, [])

  return (
    <div className='space-y-8'>
      {
        loginUsers.map((loginUser, index) => (
          <LoginUserItem key={index} {...loginUser} />
        ))
      }
    </div>
  )
}
