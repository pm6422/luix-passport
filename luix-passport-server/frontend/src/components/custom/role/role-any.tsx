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
  useStore(loginUserStore)
  const ok = loginUserStore.hasRoles(roleIds, false)

  return (
    ok && children
  )
}
