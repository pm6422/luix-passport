import http from "@/axios"
import { type Auth2Client } from "@/domains/auth2-client"
import { AxiosResponse } from "axios"

export class Oauth2ClientService {
  constructor() {
  }

  public static find(reqParams: object): Promise<AxiosResponse<Array<Auth2Client>>> {
    return http.get("/api/oauth2-clients", { params: reqParams })
  }

  public static findById(id: string): Promise<AxiosResponse<Auth2Client>> {
    return http.get("/api/oauth2-clients/" + id)
  }

  public static save(model: Auth2Client): Promise<void> {
    return model.id ? http.put("/api/oauth2-clients", model) : http.post("/api/oauth2-clients", model)
  }

  public static deleteById(id: string): Promise<void> {
    return http.delete("/api/oauth2-clients/" + id)
  }

  public static findClientAuthenticationMethods(): Promise<AxiosResponse<Array<string>>> {
    return http.get("/api/oauth2-clients/client-authentication-methods")
  }

  public static findAuthorizationGrantTypes(): Promise<AxiosResponse<Array<string>>> {
    return http.get("/api/oauth2-clients/authorization-grant-types")
  }

  public static findScopes(): Promise<AxiosResponse<Array<string>>> {
    return http.get("/api/oauth2-clients/scopes")
  }

  public static count(): Promise<AxiosResponse<number>> {
    return http.get("/api/oauth2-clients/count")
  }
}