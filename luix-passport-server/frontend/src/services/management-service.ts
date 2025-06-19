import http from "@/axios"

export class ManagementService {
  constructor() {
  }

  public static getHealth(): Promise<any> {
    return http.get("management/health", {});
  }
}