import { useStore } from "exome/react"
import { loginUserStore } from "@/stores/login-user-store"

type Props = {
  children: React.ReactNode
  roleIds: string[]
}

export const RoleAny = ({
  children,
  roleIds
}: Props) => {
  const { loginUser } = useStore(loginUserStore)
  const ok = Array.isArray(loginUser.roleIds)
    && Array.isArray(roleIds)
    && roleIds.length > 0
    && roleIds.some(id => loginUser.roleIds.includes(id))

  return (
    ok && children
  )
}
