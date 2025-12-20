export interface Team {
  id: string
  name: string
  photo?: string
  enabled: boolean
}

export interface TeamMember {
  userId: string
  email: string
  firstName: string
  lastName: string
  avatar?: string
  role: string
  roles: string[]
  joinedAt?: string
}

export interface TeamInvitation {
  id: string
  teamId: string
  inviteeEmail: string
  inviteeRoles: string
  inviterId: string
  status: string
  createdAt: string
  expiresAt: string
}
