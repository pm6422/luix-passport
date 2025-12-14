import { useLoginUserStore } from "@/stores/login-user-store"

type Props = {
  children: React.ReactNode
  roleIds: string[]
}

export const RoleAny = ({
  children,
  roleIds
}: Props) => {
  const { hasRoles } = useLoginUserStore()
  const ok = hasRoles(roleIds, false)

  return (
    ok && children
  )
}
