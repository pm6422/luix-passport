import { useLoginUserStore } from "@/stores/login-user-store"

export const ROLE_USER = "ROLE_USER"
export const ROLE_ADMIN = "ROLE_ADMIN"
export const ROLE_DEVELOPER = "ROLE_DEVELOPER"
export const ROLE_ANONYMOUS = "ROLE_ANONYMOUS"

export const TEAM_OWNER = "TEAM_OWNER"
export const TEAM_MEMBER = "TEAM_MEMBER"

export const SYSTEM_ROLES = [ROLE_USER, ROLE_ANONYMOUS]
export const TEAM_ROLES = [TEAM_OWNER, TEAM_MEMBER]

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
  <RoleAny roleIds={[TEAM_OWNER]}>{children}</RoleAny>
)
