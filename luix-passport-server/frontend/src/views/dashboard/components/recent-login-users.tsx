import { useEffect, useState } from "react"
import { LoginUserService } from "@/services/login-user-service"
import { LoginUser } from "@/domains/login-user"
import { LoginUserItem } from "./login-user-item.tsx";

export function RecentLoginUsers() {
  const [ loginUsers, setLoginUsers ] = useState([] as Array<LoginUser>)

  useEffect(() => {
    LoginUserService.findRecentLoginUsers().then((resp) => {
      setLoginUsers(resp.data)
    })
  }, [])

  return (
    <div className='space-y-8'>
      { loginUsers.map(loginUser => (
          <LoginUserItem {...loginUser} />
      ))
      }
    </div>
  )
}
