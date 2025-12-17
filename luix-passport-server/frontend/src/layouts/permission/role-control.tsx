import { useLoginUserStore } from "@/stores/login-user-store"
import { ROLE_ADMIN, ROLE_DEVELOPER, ROLE_USER, ROLE_TEAM_OWNER } from "@/domains/role"

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

export const RoleAdmin = ({ children }: { children: React.ReactNode }) => (
  <RoleAny roleIds={[ROLE_ADMIN]}>{children}</RoleAny>
)

export const RoleDeveloper = ({ children }: { children: React.ReactNode }) => (
  <RoleAny roleIds={[ROLE_DEVELOPER]}>{children}</RoleAny>
)

export const RoleUser = ({ children }: { children: React.ReactNode }) => (
  <RoleAny roleIds={[ROLE_USER]}>{children}</RoleAny>
)

export const RoleTeamOwner = ({ children }: { children: React.ReactNode }) => (
  <RoleAny roleIds={[ROLE_TEAM_OWNER]}>{children}</RoleAny>
)
