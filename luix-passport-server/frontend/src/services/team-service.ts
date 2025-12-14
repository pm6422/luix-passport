import http from "@/axios"
import { AxiosResponse } from "axios"

export class TeamService {
  constructor() {
  }

  public static count(): Promise<AxiosResponse<number>> {
    return http.get("/api/teams/count")
  }

  public static inviteMembers(teamId: string, invitations: { email: string, roles: string[] }[]): Promise<AxiosResponse<void>> {
    return http.post(`/api/teams/${teamId}/members`, invitations)
  }

  public static create(name: string, photo?: File): Promise<AxiosResponse<void>> {
    const formData = new FormData()
    formData.append("name", name)
    if (photo) {
      formData.append("photo", photo)
    }
    return http.post("/api/teams", formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
  }

  public static findUserTeams(): Promise<AxiosResponse<Team[]>> {
    return http.get("/api/teams")
  }

  public static getMembers(teamId: string): Promise<AxiosResponse<TeamMember[]>> {
    return http.get(`/api/teams/${teamId}/members`)
  }

  public static getPendingInvitations(teamId: string): Promise<AxiosResponse<TeamInvitation[]>> {
    return http.get(`/api/teams/${teamId}/invitations`)
  }

  public static getTeamPhoto(teamId: string): string {
    return `/api/teams/${teamId}/photo`
  }

  public static updateTeamPhoto(teamId: string, photo: File): Promise<AxiosResponse<void>> {
    const formData = new FormData()
    formData.append("photo", photo)
    return http.put(`/api/teams/${teamId}/photo`, formData, {
      headers: {
        "Content-Type": "multipart/form-data"
      }
    })
  }

  public static getUserInvitations(): Promise<AxiosResponse<TeamInvitation[]>> {
    return http.get("/api/user/invitations")
  }

  public static resendInvitation(invitationId: string): Promise<AxiosResponse<void>> {
    return http.post(`/api/invitations/${invitationId}/resend`)
  }

  public static acceptInvitation(invitationId: string): Promise<AxiosResponse<void>> {
    return http.post(`/api/invitations/${invitationId}/accept`)
  }

  public static rejectInvitation(invitationId: string): Promise<AxiosResponse<void>> {
    return http.post(`/api/invitations/${invitationId}/reject`)
  }

  // batch operations removed

  public static updateMemberRole(teamId: string, userId: string, roles: string[]): Promise<AxiosResponse<void>> {
    return http.put(`/api/teams/${teamId}/members/${userId}/role`, roles)
  }
}

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
  memberEmail: string
  memberRole: string
  inviterId: string
  status: string
  createdAt: string
  expiresAt: string
}
