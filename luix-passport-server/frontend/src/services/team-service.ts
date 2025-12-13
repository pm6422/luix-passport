import http from "@/axios"
import { AxiosResponse } from "axios"

export class TeamService {
  constructor() {
  }

  public static count(): Promise<AxiosResponse<number>> {
    return http.get("/api/teams/count")
  }

  public static inviteMembers(teamId: string, invitations: { email: string, role: string }[]): Promise<AxiosResponse<void>> {
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
  profilePic?: string
  role: string
  joinedAt?: string
}